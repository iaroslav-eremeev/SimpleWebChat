package servlets;

import DAO.DAO;
import com.google.gson.Gson;
import model.User;
import util.Encrypt;
import util.Unicode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/login")
public class UserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the response character encoding to UTF-8
        Unicode.setUnicode(request, response);
        // Get the login and password parameters from the request
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        // Check if the parameters are not null
        if (login != null && password != null) {
            try {
                // Try to get the user from the database using the login and password
                User user = (User) DAO.getObjectByParams(new String[]{"login", "password"}, new Object[]{login, password}, User.class);
                // Close the Hibernate session
                DAO.closeOpenedSession();
                if (user != null) {
                    // Generate a new hash for the user
                    String hash = Encrypt.generateHash();
                    user.setHash(hash);
                    // Update the user object in the database
                    DAO.updateObject(user);
                    // Create a new cookie with the hash value and set its properties
                    Cookie cookie = new Cookie("hash", hash);
                    cookie.setMaxAge(30 * 60);
                    cookie.setPath("/");
                    // Add the cookie to the response
                    response.addCookie(cookie);
                } else {
                    // If the user is not found, set the response status to 400 Bad Request
                    response.setStatus(400);
                    response.getWriter().print("Incorrect login or password");
                }
            } catch (Exception e) {
                // If an exception is thrown, set the response status to 500 Internal Server Error
                response.setStatus(500);
                response.getWriter().println(e.getMessage());
            }
        } else {
            // If the parameters are null, print a message to the console
            System.out.println("Login or password are incorrect");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set unicode encoding for request and response
        Unicode.setUnicode(request, response);
        // Set response content type to JSON
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            List<User> onlineUsers = DAO.getObjectsByParams(new String[]{"isOnline"}, new Object[]{true}, User.class);
            DAO.closeOpenedSession();
            if (onlineUsers.isEmpty()) {
                // If no one is online, return all users
                List<User> allUsers = DAO.getAllObjects(User.class);
                DAO.closeOpenedSession();
                writer.write(new Gson().toJson(allUsers));
            } else {
                DAO.closeOpenedSession();
                // Write online users list to response in JSON format
                writer.write(new Gson().toJson(onlineUsers));
            }
        } catch (Exception e) {
            // Log error and send 500 error response
            log("Error getting online users", e);
            response.sendError(500, "Error getting online users");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the request and response character encoding to Unicode
        Unicode.setUnicode(request, response);
        // Get the id parameter from the request
        String id = request.getParameter("id");
        if (id != null) {
            try {
                // Convert the id parameter to long and delete the corresponding User object from the database
                DAO.deleteObjectById(Math.toIntExact(Long.parseLong(id)), User.class);
                DAO.closeOpenedSession();
            } catch (Exception e) {
                // If there is an exception, set the response status to 200 (OK)
                response.setStatus(200);
            }
        }
    }

}
