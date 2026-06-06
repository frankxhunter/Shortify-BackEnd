package com.frank.shortify.services.external;

import com.frank.shortify.dto.IpDataExternal;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IpInfoServiceExternal {
    private final RestClient restClient;

    public IpInfoServiceExternal(RestClient.Builder builder, @Value("${ipinfo.token}") String token) {
        this.restClient = builder
                .baseUrl("https://api.ipinfo.io/lite/")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    public IpDataExternal getInfoIp(String ip) {
        IpDataExternal result = restClient.get()
                .uri("/{ip}", ip)
                .retrieve().body(IpDataExternal.class);
        if (StringUtils.isBlank(result.getCountryCode())) {
            result.setCountry("Unknown");
            result.setCountryCode("--");
        }
        return result;
    }
}
