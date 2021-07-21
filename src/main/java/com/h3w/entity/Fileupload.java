package com.h3w.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "pro_fileupload", catalog = "")
public class Fileupload implements Serializable {

    private static final long serialVersionUID = -7720136874773171716L;

    public static Integer FLAG_ED = 1;
    public static Integer TOPDF_ED = 1;
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


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "filepath", nullable = true, length = 200)
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Basic
    @Column(name = "filetype", nullable = true, length = 20)
    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    @Basic
    @Column(name = "file_extname", nullable = true, length = 20)
    public String getFileExtname() {
        return fileExtname;
    }

    public void setFileExtname(String fileExtname) {
        this.fileExtname = fileExtname;
    }

    @Basic
    @Column(name = "filename", nullable = true, length = 100)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic
    @Column(name = "filesize", nullable = true)
    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    @Basic
    @Column(name = "uploadtime", nullable = true)
    public Date getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(Date uploadtime) {
        this.uploadtime = uploadtime;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Integer getDataid() {
        return dataid;
    }

    public void setDataid(Integer dataid) {
        this.dataid = dataid;
    }

    public String getUploadurl() {
        return uploadurl;
    }

    public void setUploadurl(String uploadurl) {
        this.uploadurl = uploadurl;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getTopdf() {
        return topdf;
    }

    public void setTopdf(Integer topdf) {
        this.topdf = topdf;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getFilemd5() {
        return filemd5;
    }

    public void setFilemd5(String filemd5) {
        this.filemd5 = filemd5;
    }
}
