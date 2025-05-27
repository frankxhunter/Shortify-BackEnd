package com.frank.shortify.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<InfoRequest> infoRequests;
    private String shortUrl;
    private String originalUrl;

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                (user != null ? ", user=" + user.getId() : "") +
                ", shortUrl='" + shortUrl + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                '}';
    }
}
