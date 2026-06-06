package com.frank.shortify.services;

import com.frank.shortify.Utils.UtilsRequest;
import com.frank.shortify.dto.IpDataExternal;
import com.frank.shortify.models.InfoRequest;
import com.frank.shortify.models.Url;
import com.frank.shortify.repositories.InfoRequestRepository;
import com.frank.shortify.services.external.IpInfoServiceExternal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class InfoRequestService {

    private final InfoRequestRepository infoRequestRepository;
    private final ClickCounterWebSocket clickCounterWebSocket;
    private final IpInfoServiceExternal ipInfoServiceExternal;
    private final UrlService urlService;

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

    @Async
    @Transactional
    public void saveDataAsync(InfoRequest infoRequest, Url url) {
        urlService.incrementClickCounter(url);
        clickCounterWebSocket.sendIncrement(url.getId(), url.getClickCounter());
        infoRequest.setUrl(url);
        setCountryValues(infoRequest);
        this.save(infoRequest);
    }

    private void setCountryValues(InfoRequest infoRequest) {
        IpDataExternal ipDataExternal = ipInfoServiceExternal.getInfoIp(infoRequest.getIp());
        infoRequest.setCountry(ipDataExternal.getCountry());
        infoRequest.setCountryCode(ipDataExternal.getCountryCode());
    }
}
