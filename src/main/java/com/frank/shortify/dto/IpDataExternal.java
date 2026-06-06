package com.frank.shortify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IpDataExternal {
    private String ip;
    private String country;
    @JsonProperty("country_code")
    private String countryCode;
}
