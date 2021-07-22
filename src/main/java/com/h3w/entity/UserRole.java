package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "pro_user_role", catalog = "")
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

}
