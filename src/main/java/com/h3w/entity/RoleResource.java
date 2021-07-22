package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
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

}
