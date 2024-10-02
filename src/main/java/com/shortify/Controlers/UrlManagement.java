package com.shortify.Controlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.shortify.Services.UrlService;
import com.shortify.models.Url;
import com.shortify.models.User;
import com.shortify.utils.Utils;
import com.shortify.utils.Validate;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @WebServlet("/urls/")
public class UrlManagement extends HttpServlet {

    @Inject
    UrlService urlService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = Utils.getUserFromSession(req);
        List<Url> listUrls = urlService.getListUrlsByUser(user);
        if (listUrls != null) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter out = resp.getWriter();
            out.write(Utils.convertObjectToJson(listUrls));
            out.flush();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println(req.getParameter("id"));
            int idUrl = Integer.parseInt(req.getParameter("id"));
            String newOriginalUrl = req.getParameter("originalUrl");
            User user = Utils.getUserFromSession(req);
            if (idUrl == 0) {
                Utils.sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing 'id' parameter in the request");
                return;
            }
            if (newOriginalUrl == null || !Validate.validateHttpAddress(newOriginalUrl)) {
                Utils.sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Missing 'originalUrl' parameter in the request or format invalid");
                return;
            }

            int affectRows = urlService.updateOriginalUrl(user, idUrl, newOriginalUrl);

            if (affectRows == 0) {
                Utils.sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Please check the parameters, as there seems to be error.");
                return;
            }
            Utils.sendRespJson(resp, 200, "Ok");

        } catch (NumberFormatException e) {
            Utils.sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "The format of the id is incorrect, must be a integer");
        }
    }
}
