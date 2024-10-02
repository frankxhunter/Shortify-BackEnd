package com.shortify.Services;

import java.sql.SQLException;
import java.util.List;

import com.shortify.models.InfoRequest;
import com.shortify.models.Url;
import com.shortify.repositories.InfoRequestRepository;
import com.shortify.utils.ServiceJDBCException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class InfoRequestService {
    
    @Inject
    InfoRequestRepository infoRequestRepository;

    public List<InfoRequest> getAllRequestByUrlr(Url url){
        int id_url = url.getId();
        try {
            List<InfoRequest> list = infoRequestRepository.getInfoRequestsByUrl(id_url);
            return list;
        } catch (SQLException e) {
            throw new ServiceJDBCException(e.getMessage(), e );
        }
    }

    public void saveInfo(HttpServletRequest req, Url url){

        InfoRequest infoRequest = new InfoRequest(req, url.getId());
        
        try {
            infoRequestRepository.save(infoRequest);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
