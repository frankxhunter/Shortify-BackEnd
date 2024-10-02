package com.shortify.Controlers;

import java.io.IOException;

import com.shortify.Services.InfoRequestService;
import com.shortify.Services.UrlService;
import com.shortify.models.Url;
import com.shortify.utils.Utils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RedirectionServlet extends HttpServlet {

    @Inject
    private UrlService urlService;

    @Inject
    private InfoRequestService requestService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo().substring(1);
        Url url = urlService.getOriginalUrl(path);
        if (url != null && url.getOriginalUrl() != null) {
            resp.sendRedirect(url.getOriginalUrl());
            System.out.println("HOla mundo ");
            requestService.saveInfo(req, url);
        }
        else{
            Utils.sendErrorJson(resp, HttpServletResponse.SC_NOT_FOUND, """
                    This URL is not avaliable!
                    """);
        }
    }

}
