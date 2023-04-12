package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Message;
import repository.SSEEmittersRepository;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatWatchService {
    private static final Logger LOGGER = Logger.getLogger(ChatWatchService.class.getName());
    private SSEEmittersRepository repository;
    private BlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>();
    private ExecutorService singleThreadExecutorTasker;

    private void sendMessage(PrintWriter writer, Message message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message));
            writer.println();
            writer.flush();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error sending message", e);
        }
    }

    public void add(Message message){
        this.messageBlockingQueue.add(message);
    }

    public ChatWatchService(SSEEmittersRepository repository) {
        this.repository = repository;
        this.startMessageReceive();
    }

    private void startMessageReceive() {
        singleThreadExecutorTasker = Executors.newSingleThreadExecutor();
        singleThreadExecutorTasker.execute(() -> {
            try {
                while (true) {
                    // Next event from the blocking queue
                    Message message = messageBlockingQueue.take();
                    LOGGER.log(Level.FINE, "Start sending\n" + repository.getList());
                    for (AsyncContext asyncContext : repository.getList()) {
                        try {
                            sendMessage(asyncContext.getResponse().getWriter(), message);
                        } catch (IOException e) {
                            LOGGER.log(Level.WARNING, "Error sending message to client", e);
                        }
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Thread was interrupted", e);
            } finally {
                LOGGER.log(Level.INFO, "Thread is done receiving messages");
            }
        });
    }

    public void stop() {
        this.singleThreadExecutorTasker.shutdownNow();
        this.repository.clear();
        this.messageBlockingQueue.clear();
    }
}
