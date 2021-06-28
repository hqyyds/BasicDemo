package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "ass_log", catalog = "")
public class Log implements Serializable {
    private static final long serialVersionUID = 7138058752708665657L;

    public static Map<Integer,String> optypes;
    static {
        optypes = new HashMap<>();
        optypes.put(1,"登录");
        optypes.put(-1,"退出");
        optypes.put(3,"上传文件");
        optypes.put(4,"删除文件");
        optypes.put(30,"系统管理");
        optypes.put(31,"保存用户");
        optypes.put(32,"分配角色");
        optypes.put(33,"删除用户");
        optypes.put(34,"重置密码");
    }


    public static final int OPTYPE_LOGIN = 1;//登录
    public static final int OPTYPE_LOGIN_OUT = -1;//退出
    public static final int OPTYPE_UPLOAD_FILE = 3;
    public static final int OPTYPE_DEL_FILE = 4;
    public static final int OPTYPE_CHECK = 21;//审批任务
    public static final int OPTYPE_SYS_MANAGE = 30;
    public static final int OPTYPE_SYS_SAVEUSER = 31;
    public static final int OPTYPE_SYS_ADDROLE = 32;
    public static final int OPTYPE_SYS_DELUSER = 33;
    public static final int OPTYPE_SYS_REPASSWORD = 34;

    private int id;
    private Integer userid;
    private Integer deptid;
    private String realname;
    private String dataid;
    private String tbname;
    private Integer optype;
    private String ip;
    private Date logtime;
    private String action;
    private String content;
    private Long runtime;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getDeptid() {
        return deptid;
    }

    public void setDeptid(Integer deptid) {
        this.deptid = deptid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public String getTbname() {
        return tbname;
    }

    public void setTbname(String tbname) {
        this.tbname = tbname;
    }

    public Integer getOptype() {
        return optype;
    }

    public void setOptype(Integer optype) {
        this.optype = optype;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLogtime() {
        return logtime;
    }

    public void setLogtime(Date logtime) {
        this.logtime = logtime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log assLog = (Log) o;
        return id == assLog.id &&
                Objects.equals(userid, assLog.userid) &&
                Objects.equals(deptid, assLog.deptid) &&
                Objects.equals(realname, assLog.realname) &&
                Objects.equals(dataid, assLog.dataid) &&
                Objects.equals(tbname, assLog.tbname) &&
                Objects.equals(optype, assLog.optype) &&
                Objects.equals(ip, assLog.ip) &&
                Objects.equals(logtime, assLog.logtime) &&
                Objects.equals(action, assLog.action) &&
                Objects.equals(content, assLog.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid, deptid, realname, dataid, tbname, optype, ip, logtime, action, content);
    }

    public Long getRuntime() {
        return runtime;
    }

    public void setRuntime(Long runtime) {
        this.runtime = runtime;
    }
}
