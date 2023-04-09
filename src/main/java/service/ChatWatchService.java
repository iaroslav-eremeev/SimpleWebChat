package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Event;
import model.Message;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.persister.entity.EntityPersister;
import repository.SSEEmittersRepository;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import util.Constants;

public class ChatWatchService {
    private SSEEmittersRepository repository;
    private BlockingQueue<Event> messageBlockingQueue = new LinkedBlockingQueue<>();
    private ExecutorService singleThreadExecutorWatcher;
    private ExecutorService singleThreadExecutorTasker;

    private void sendMessage(PrintWriter writer, Event message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message));
            writer.println();
            writer.flush();
        } catch (Exception ignored) {
        }
    }

    public ChatWatchService(String folderName, SSEEmittersRepository repository) {
        this.repository = repository;
        this.listenToDatabase();
        this.startMessageReceive();
    }

    private void listenToDatabase() {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", Constants.DB_URL)
                .setProperty("hibernate.connection.username", Constants.USERNAME)
                .setProperty("hibernate.connection.password", Constants.PASSWORD);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());

        MetadataSources metadataSources = new MetadataSources(builder.build());
        metadataSources.addAnnotatedClass(Message.class);

        Metadata metadata = metadataSources.getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        AuditReader auditReader = AuditReaderFactory.get(sessionFactory.getCurrentSession());

        DatabaseListener listener = new DatabaseListener(messageBlockingQueue) {
            @Override
            public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
                return false;
            }
        };

        // Register the listener with the session factory
        EventListenerRegistry eventListenerRegistry = ((SessionFactoryImplementor) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        eventListenerRegistry.appendListeners(EventType.POST_INSERT, listener);

        // Poll the database for new messages when a new event is added to the blocking queue
        while (true) {
            try {
                Event event = messageBlockingQueue.take();
                if (event.getAction().equals("create") && event.getPath().equals(Constants.DB_URL)) {
                    // Query the database for new messages
                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    List messages = auditReader.createQuery()
                            .forRevisionsOfEntity(Message.class, false, true)
                            .add(AuditEntity.revisionType().eq(RevisionType.ADD))
                            .add(AuditEntity.property("path").eq(Constants.DB_URL))
                            .getResultList();

                    session.getTransaction().commit();
                    session.close();

                    // Add the new messages to the message blocking queue
                    messageBlockingQueue.addAll(messages);
                }
            } catch (InterruptedException e) {
                // Handle interrupted exception
                e.printStackTrace();
            }
        }
    }

    private void startMessageReceive() {
        singleThreadExecutorTasker = Executors.newSingleThreadExecutor();
        singleThreadExecutorTasker.execute(() -> {
            try {
                while (true) {
                    // Next event from the blocking queue
                    Event message = messageBlockingQueue.take();
                    System.out.println("Start sending\n" + repository.getList());
                    for (AsyncContext asyncContext : repository.getList()) {
                        try {
                            sendMessage(asyncContext.getResponse().getWriter(), message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread is interrupting");
            }
            System.out.println("Thread is interrupted");
        });
    }

    public void stop() {
        this.singleThreadExecutorTasker.shutdownNow();
        this.singleThreadExecutorWatcher.shutdownNow();
        this.repository.clear();
        this.messageBlockingQueue.clear();
    }
}
