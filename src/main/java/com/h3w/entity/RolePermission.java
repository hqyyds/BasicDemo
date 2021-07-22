package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "pro_role_permission")
@Entity
public class RolePermission implements Serializable {
    private static final long serialVersionUID = -8368611905056762329L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "rolecode")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "permissioncode")
    private Permission permission;

}
