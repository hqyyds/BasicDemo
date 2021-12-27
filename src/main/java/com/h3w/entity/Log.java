package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Entity
@Table(name = "pro_log", catalog = "")
public class Log implements Serializable {
    private static final long serialVersionUID = 7138058752708665657L;

    public static Map<Integer, String> optypes;

    static {
        optypes = new HashMap<>();
        optypes.put(1, "登录");
        optypes.put(-1, "退出");
        optypes.put(3, "上传文件");
        optypes.put(4, "删除文件");
        optypes.put(30, "系统管理");
        optypes.put(31, "保存用户");
        optypes.put(32, "分配角色");
        optypes.put(33, "删除用户");
        optypes.put(34, "重置密码");
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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

}
