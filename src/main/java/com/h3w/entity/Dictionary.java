package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "pro_dictionary", catalog = "")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = -691859058775986001L;
    public static Integer TYPE_TYPE = 1;//字典分类
    public static Integer TYPE_DATA = 2;//字典数据
    public static String TYPE_USER = "user_type";
    public static Integer STATUS_INIT = 0;
    public static Integer STATUS_DEL = -1;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    private String name;
    private String dicvalue;
    private Integer parentid;
    private String dictype;
    private Integer type;
    private Integer status;
    private Integer seq;
    private String remark;
    private Date createtime;

}
