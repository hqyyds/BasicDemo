package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "ass_log", catalog = "")
public class Log implements Serializable {
    private static final long serialVersionUID = 7138058752708665657L;
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
}
