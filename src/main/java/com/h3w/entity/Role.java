package com.h3w.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "pro_role", catalog = "")
public class Role implements Serializable {
    private static final long serialVersionUID = -1716493836578180320L;

    public final static String CODE_GLY = "sysgly";
    public final static String CODE_BASE = "base";

    @Id
    private String code;
    private String name;
    private String description;
    private Integer level;

}
