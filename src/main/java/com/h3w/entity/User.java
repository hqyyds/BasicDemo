package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "sys_user")
public class User implements Serializable {

    public static final int STATUS_DEL_ED = -1;//已删除
    public static final int STATUS_ENABLE = 0;//正常使用
    public static final int STATUS_DISABLE = 1;//不可使用
    public static final int STATUS_DELETED = 2;//删除状态
    private static final long serialVersionUID = 726596797441866380L;

    public static Map<Integer, String> statusMap;

    static {
        statusMap = new HashMap<>();
        statusMap.put(-1, "已删除");
        statusMap.put(0, "启用");
        statusMap.put(1, "禁用");
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "id") 用来跟表的字段做个映射，如果名字相同就不需要写
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "passwordn")
    private String passwordn;

    @Column(name = "sex")
    private String sex;

    @Column(name = "status")
    private Integer status;

    private String realname;
    private String position;
    private String phone;
    private String email;
    private String remark;

    @ManyToOne()
    @JoinColumn(name = "depid")
    private Department dept;
    private Date createtime;
    private String photopath;
    private Integer seq;
    private Integer flag;
    private String imgurl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRole> userRoles;

    @Transient
    private Integer deptid;

    @Transient
    private String statusstr;

    @Transient
    private List<Role> roles;

}
