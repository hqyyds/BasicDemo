package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Table(name = "pro_permission")
@Entity
public class Permission implements Serializable, Comparable<Permission> {

    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;
    private static final long serialVersionUID = -1221256213813968794L;

    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String code;
    private Integer type;
    private String url;
    private String icon;
    private String parentcode;
    private String name;
    private String pname;
    private Integer seq;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permission", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RolePermission> rolePermissions;
    @Transient
    private List<Permission> children;

    @Override
    public int compareTo(Permission o) { //重写Comparable接口的compareTo方法，
        return this.seq - o.getSeq();// 根据 升序排列，降序修改相减顺序即可
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return code.equals(that.code);
    }
}
