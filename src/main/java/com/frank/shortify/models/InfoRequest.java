package com.frank.shortify.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "info_requests")
public class InfoRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "url_id")
    @JsonBackReference
    private Url url;
    private String ip;
    private String browser;
    private String os;
    private String architecture;
    private Timestamp date;

}
