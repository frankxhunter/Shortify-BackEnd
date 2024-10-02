package com.shortify.Controlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.shortify.Services.InfoRequestService;
import com.shortify.Services.UrlService;
import com.shortify.models.InfoRequest;
import com.shortify.utils.Utils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InfoRequestServlet extends HttpServlet {

    @Inject
    InfoRequestService infoRequestService;

    @Inject
    UrlService urlService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int url_id = Integer.parseInt(req.getParameter("id"));

            // Validar q el usuario tenga acceso a esa url
            if (urlService.getListUrlsByUser(Utils.getUserFromSession(req))
                    .stream().anyMatch(url -> url.getId() == url_id)) {

                List<InfoRequest> info_requestList = infoRequestService.getAllRequestByUrlr(url_id);
                if (info_requestList == null) {
                    info_requestList = new ArrayList<InfoRequest>();
                }
                Utils.sendRespJson(resp, info_requestList);
            } else {
                Utils.sendErrorJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                        "You have not authorization for this resource");
            }
        } catch (NumberFormatException e) {
            Utils.sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing parameters or invalid type");
        }
    }
}
