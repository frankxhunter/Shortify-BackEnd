package com.shortify.Controlers.UserRegister;

import java.io.IOException;
import java.io.PrintWriter;

import com.shortify.Services.UserService;
import com.shortify.models.User;
import com.shortify.utils.Utils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogInServlet extends HttpServlet {

    @Inject
    UserService userService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = Utils.getUserFromSession(req);
        if (user == null) {
            user = new User();
            user.setUsername(req.getParameter("username"));
            user.setEmail(req.getParameter("email"));
            user.setPassword(req.getParameter("password"));

            if (user.getPassword() != null && (user.getEmail() != null || user.getUsername() != null)) {
                User loginUser = userService.logInUser(user);
                if (loginUser != null) {
                    session.setAttribute("user", loginUser);
                    session.setMaxInactiveInterval(1000000);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(Utils.convertObjectToJson(loginUser));
                } else {
                    Utils.sendErrorJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
                }

            } else {
                Utils.sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(Utils.convertObjectToJson(user));
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = Utils.getUserFromSession(req);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();
        out.write(Utils.convertObjectToJson(user));
        out.flush();
    }
}
