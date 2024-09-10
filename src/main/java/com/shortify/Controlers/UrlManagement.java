package com.shortify.Controlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shortify.Services.UrlService;
import com.shortify.models.Url;
import com.shortify.models.User;
import com.shortify.utils.Utils;

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
        List<Url> listUrls= urlService.getListUrlsByUser(user);
        if(listUrls != null){
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String jsonData = gson.toJson(listUrls);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter out = resp.getWriter();
            out.write(jsonData);
            out.flush();
        }
        

    }
}
