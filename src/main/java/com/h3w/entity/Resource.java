package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "pro_resource", catalog = "")
public class Resource implements Serializable {

    private static final long serialVersionUID = 987327960381045562L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private String url;
    private String name;
    private Integer type;
    private String description;
    private String roles;
    private String fun;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resource", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RoleResource> roleResources;

}
