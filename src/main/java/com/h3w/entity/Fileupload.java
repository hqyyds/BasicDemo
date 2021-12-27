package com.h3w.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "pro_fileupload", catalog = "")
public class Fileupload implements Serializable {

    private static final long serialVersionUID = -7720136874773171716L;

    public static Integer FLAG_ED = 1;
    public static Integer TOPDF_ED = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private Integer dataid;
    private String filepath;
    private String uploadurl;
    private String filetype;
    private String fileExtname;
    private String filename;
    private String realname;
    private Long filesize;
    private Date uploadtime;
    private Integer userid;
    private Integer flag;
    private Integer topdf;
    private String filemd5;

}
