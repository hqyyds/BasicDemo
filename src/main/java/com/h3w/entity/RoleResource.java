package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pro_role_resource", catalog = "")
public class RoleResource implements Serializable {
    private static final long serialVersionUID = -8368611905056762329L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "rolecode")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "resourceid")
    private Resource resource;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
