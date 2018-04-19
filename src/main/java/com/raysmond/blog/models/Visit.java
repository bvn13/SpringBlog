package com.raysmond.blog.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "visits")
@Getter @Setter
public class Visit extends BaseModel {

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String clientIp;

    @ManyToOne
    private Post post;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isAdmin;

    @Column(columnDefinition = "VARCHAR")
    private String userAgent;

}
