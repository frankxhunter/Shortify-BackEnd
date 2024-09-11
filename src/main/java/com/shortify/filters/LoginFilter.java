package com.shortify.filters;

import java.io.IOException;

import com.shortify.models.User;
import com.shortify.utils.Utils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter({"/urls", "/urls/*"})
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
                User user = Utils.getUserFromSession((HttpServletRequest)request);
                if(user!= null){
                    chain.doFilter(request, response);
                }
                else{
                    ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                     "Authorization required, please log in to access this resource");
                }
    }

}
