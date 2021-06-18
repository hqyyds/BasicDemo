package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 60292 on 2018/7/23.
 */
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getFun() {
        return fun;
    }

    public void setFun(String fun) {
        this.fun = fun;
    }

    public List<RoleResource> getRoleResources() {
        return roleResources;
    }

    public void setRoleResources(List<RoleResource> roleResources) {
        this.roleResources = roleResources;
    }
}
