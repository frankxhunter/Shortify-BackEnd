package com.shortify.Controlers;

import java.io.IOException;

import com.shortify.Services.UrlService;
import com.shortify.utils.Utils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RedirectionServlet extends HttpServlet {

    @Inject
    private UrlService urlService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo().substring(1);
        String originalUrl = urlService.getOriginalUrl(path);
        if (originalUrl != null) {
            resp.sendRedirect(originalUrl);
        }
        else{
            Utils.sendErrorJson(resp, HttpServletResponse.SC_NOT_FOUND, """
                    This URL is not avaliable!
                    """);
        }
    }

}
