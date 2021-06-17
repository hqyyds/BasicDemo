package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ass_dictionary", catalog = "")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = -691859058775986001L;
    public static Integer TYPE_TYPE = 1;//字典分类
    public static Integer TYPE_DATA = 2;//字典数据
    public static String TYPE_USER = "user_type";
    public static Integer STATUS_INIT = 0;
    public static Integer STATUS_DEL = -1;

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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "parentid")
    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    @Basic
    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "seq")
    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    @Basic
    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "createtime")
    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getDicvalue() {
        return dicvalue;
    }

    public void setDicvalue(String dicvalue) {
        this.dicvalue = dicvalue;
    }

    public String getDictype() {
        return dictype;
    }

    public void setDictype(String dictype) {
        this.dictype = dictype;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
