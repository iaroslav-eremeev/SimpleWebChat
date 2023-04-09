package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Event;
import model.Message;
import repository.SSEEmittersRepository;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatWatchService {
    private SSEEmittersRepository repository;
    private BlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>();
    private ExecutorService singleThreadExecutorTasker;

    private void sendMessage(PrintWriter writer, Message message) {
        try {
            writer.println("data: " + new ObjectMapper().writeValueAsString(message));
            writer.println();
            writer.flush();
        } catch (Exception ignored) {
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
        this.repository.clear();
        this.messageBlockingQueue.clear();
    }
}
