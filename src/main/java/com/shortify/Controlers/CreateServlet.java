package com.shortify.Controlers;

import java.io.IOException;
import java.io.PrintWriter;

import com.shortify.Services.UrlService;
import com.shortify.models.User;
import com.shortify.utils.Utils;
import com.shortify.utils.Validate;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CreateServlet extends HttpServlet {

    @Inject
    private UrlService urlService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String originalUrl = req.getParameter("url");
        User user = Utils.getUserFromSession(req);
        if(originalUrl != null && originalUrl.trim().length()>0){
            if(Validate.validateHttpAddress(originalUrl)){

                String shortUrl = urlService.generateUrlAndSave(originalUrl, user);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
            
                PrintWriter out = resp.getWriter();
                out.print("{\"shortUrl\": "+ "\""+shortUrl+"\"}");
                out.flush();
            }else{
                System.out.println("----\n"+originalUrl);
                Utils.sendErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, """
                    The format of the url is invalid
                    """);
            }
        }else{
            Utils.sendErrorJson(resp,HttpServletResponse.SC_BAD_REQUEST, """
                    You must send the url for short!!
                    """);
        }
    }

    
}
