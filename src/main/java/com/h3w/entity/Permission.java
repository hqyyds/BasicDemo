package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "doc_permission")
@Entity
public class Permission implements Serializable,Comparable<Permission> {

    public static final int TYPE_1=1;
    public static final int TYPE_2=2;
    public static final int TYPE_3=3;
    private static final long serialVersionUID = -1221256213813968794L;
    private String code;
    private Integer type;
    private String url;
    private String icon;
    private String parentcode;
    private String name;
    private String pname;
    private Integer seq;
    private List<RolePermission> rolePermissions;

    private List<Permission> children;

    @Id
    @Column(name = "code", nullable = false, length = 20)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "type", nullable = true)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Basic
    @Column(name = "url", nullable = true, length = 100)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "parentcode", nullable = true, length = 20)
    public String getParentcode() {
        return parentcode;
    }

    public void setParentcode(String parentcode) {
        this.parentcode = parentcode;
    }

    @Basic
    @Column(name = "name", nullable = true, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "seq", nullable = true)
    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }


    @Transient
    public List<Permission> getChildren() {
        return children;
    }

    public void setChildren(List<Permission> children) {
        this.children = children;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permission", orphanRemoval = true, fetch = FetchType.EAGER)
    public List<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int compareTo(Permission o) { //重写Comparable接口的compareTo方法，
        return this.seq - o.getSeq();// 根据 升序排列，降序修改相减顺序即可
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return code.equals(that.code);
    }
}
