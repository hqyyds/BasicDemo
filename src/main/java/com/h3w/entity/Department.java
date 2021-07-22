package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "sys_department",catalog = "")
public class Department implements Serializable {

    public static final int ROOT_ID=1;//根目录
    public static final Integer FLAG_INIT= 0;//初始
    private static final long serialVersionUID = -1254919484847345489L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String name;
    private String remark;
    private Integer parentid;
    private String icon;
    private Integer flag;
    private String code;
    @Transient
    private List<Department> departments;

}
