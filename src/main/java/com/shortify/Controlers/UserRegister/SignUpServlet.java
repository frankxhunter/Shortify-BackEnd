package com.shortify.Controlers.UserRegister;

import java.io.IOException;

import com.shortify.Services.UserService;
import com.shortify.models.User;
import com.shortify.utils.UserRegisterException;
import com.shortify.utils.Utils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SignUpServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("user") == null) {
            User user = new User();
            user.setUsername(req.getParameter("username"));
            user.setEmail(req.getParameter("email"));
            user.setPassword(req.getParameter("password"));

            if (user.getPassword() != null && user.getEmail() != null && user.getUsername() != null) {
                try {
                    User registerUser = userService.signUp(user);
                    if (registerUser != null) {
                        session.setAttribute("user", registerUser);
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write(Utils.convertObjectToJson(registerUser));
                    } else {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid data format");
                    }
                    
                } catch (UserRegisterException e) {
                    resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
                }

            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("User is already logged in");
        }

    }
}
