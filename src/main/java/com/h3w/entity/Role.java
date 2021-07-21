package com.h3w.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
