package service;

import dto.Event;
import model.Message;
import org.hibernate.envers.RevisionListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

public class DatabaseListener implements PostInsertEventListener {

    private BlockingQueue<Event> messageBlockingQueue;

    public DatabaseListener(BlockingQueue<Event> messageBlockingQueue) {
        this.messageBlockingQueue = messageBlockingQueue;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof Message) {
            Message message = (Message) entity;
            messageBlockingQueue.add(new Event("create", Paths.get("jdbc:mysql://localhost:3306/simple_chat/messages")));
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return false;
    }
}

