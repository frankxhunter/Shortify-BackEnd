package com.frank.shortify.dto;

import com.frank.shortify.Validators.annotations.UrlFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUrlDto {
    @NotBlank(message = "The url is required")
    @UrlFormat
    private String url;
    @Pattern(regexp = "^$|^.{2,80}$", message = "Name must be between 2 and 80 characters")
    private String name;
}
