package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Message;
import model.User;
import org.hibernate.Session;
import DAO.HibernateUtil;
import DAO.DAO;
import util.Unicode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MessageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Unicode.setUnicode(req, resp);

        List messages = null;
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

        resp.setStatus(200);
        resp.getWriter().println(messagesJson);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Unicode.setUnicode(req, resp);

        String messageText = req.getParameter("messageText");
        int userId = Integer.parseInt(req.getParameter("userId"));

        User user = null;
        try {
            user = (User) DAO.getObjectById(userId, User.class);
            DAO.closeOpenedSession();
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.getWriter().println(e.getMessage());
            return;
        }
        Message message = new Message(messageText);
        message.setUser(user);
        try {
            DAO.addObject(message);
            DAO.closeOpenedSession();
            resp.setStatus(200);
            resp.getWriter().println("Message added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(400);
            resp.getWriter().println(e.getMessage());
        }
    }
}
