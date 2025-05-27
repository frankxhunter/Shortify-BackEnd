package com.frank.shortify.services;

import com.frank.shortify.Utils.UtilsRequest;
import com.frank.shortify.models.InfoRequest;
import com.frank.shortify.models.Url;
import com.frank.shortify.repositories.InfoRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class InfoRequestService {

    @Autowired
    private InfoRequestRepository infoRequestRepository;

    public Iterable<InfoRequest> findByUrl(Url url) {
        return infoRequestRepository.findByUrl(url);
    }

    public InfoRequest save(InfoRequest infoRequest) {
        return infoRequestRepository.save(infoRequest);
    }

    public InfoRequest getInfoRequestFromHttpRequest(HttpServletRequest req) {
        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setIp(UtilsRequest.getClientIp(req));
        infoRequest.setBrowser(UtilsRequest.getBrowser(req));
        infoRequest.setOs(UtilsRequest.getOs(req));
        infoRequest.setArchitecture(UtilsRequest.getArchitecture(req));
        infoRequest.setDate(new Timestamp(System.currentTimeMillis()));
        return infoRequest;
    }
}
