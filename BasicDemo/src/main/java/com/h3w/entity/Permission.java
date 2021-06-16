package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "doc_permission")
@Entity
public class Permission implements Serializable {

    public static final int TYPE_MENU=1;
    private static final long serialVersionUID = -3208533504812880919L;

    @Id
    private String code;
    private Integer type;
    private String url;
    private String parentcode;
    private String name;
    private String description;
    private String fun;
    private Integer seq;
    private Integer status;
    private String per;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permission", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RolePermission> rolePermissions;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentcode() {
        return parentcode;
    }

    public void setParentcode(String parentcode) {
        this.parentcode = parentcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getFun() {
        return fun;
    }

    public void setFun(String fun) {
        this.fun = fun;
    }

    public List<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPer() {
        return per;
    }

    public void setPer(String per) {
        this.per = per;
    }
}
