package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Message;
import model.User;
import DAO.DAO;
import repository.SSEEmittersRepository;
import service.ChatWatchService;
import util.Unicode;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/sse/chat-watch", asyncSupported = true)
public class MessageServlet extends HttpServlet {

    private SSEEmittersRepository emitters = new SSEEmittersRepository();
    private ChatWatchService service;

    @Override
    public void init() {
        this.service = new ChatWatchService(this.emitters);
    }

    @Override
    public void destroy() {
        this.service.stop();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Unicode.setUnicode(req, resp);
        if (req.getHeader("Accept").equals("text/event-stream")) {
            resp.setContentType("text/event-stream");
            resp.setHeader("Connection", "keep-alive");
            resp.setCharacterEncoding("UTF-8");

            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(60000L);
            this.emitters.add(asyncContext);
        } else {
            List<Message> messages = new ArrayList();
            try {
                messages = DAO.getAllObjects(Message.class);
                DAO.closeOpenedSession();
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(400);
                resp.getWriter().println(e.getMessage());
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String messagesJson = objectMapper.writeValueAsString(messages);
            resp.getWriter().println(messagesJson);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Unicode.setUnicode(req, resp);

        String messageText = req.getParameter("messageText");
        int userId = Integer.parseInt(req.getParameter("userId"));

        User user = (User) DAO.getObjectById(userId, User.class);
        DAO.closeOpenedSession();
        if (user != null) {
            Message message = new Message(messageText, user);
            try {
                DAO.addObject(message);
                resp.getWriter().println("Message added successfully");
                service.add(message);
            } catch (Exception e) {
                resp.setStatus(400);
                resp.getWriter().println(e.getMessage());
            }
        }

    }
}
