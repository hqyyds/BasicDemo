package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "sys_user")
public class User implements Serializable {

    public static final int STATUS_DEL_ED=-1;//已删除
    public static final int STATUS_ENABLE=0;//正常使用
    public static final int STATUS_DISABLE=1;//不可使用
    public static final int STATUS_DELETED=2;//删除状态
    private static final long serialVersionUID = 726596797441866380L;

    public static Map<Integer,String> statusMap;
    static {
        statusMap = new HashMap<>();
        statusMap.put(-1,"已删除");
        statusMap.put(0,"启用");
        statusMap.put(1,"禁用");
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Department getDept() {
        return dept;
    }

    public void setDept(Department dept) {
        this.dept = dept;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getStatusstr() {
        return statusstr;
    }

    public void setStatusstr(String statusstr) {
        this.statusstr = statusstr;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Integer getDeptid() {
        return deptid;
    }

    public void setDeptid(Integer deptid) {
        this.deptid = deptid;
    }

    public String getPasswordn() {
        return passwordn;
    }

    public void setPasswordn(String passwordn) {
        this.passwordn = passwordn;
    }
}
