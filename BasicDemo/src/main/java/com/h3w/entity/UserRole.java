package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "doc_user_role", catalog = "")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 8965249711785008896L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "rolecode")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
