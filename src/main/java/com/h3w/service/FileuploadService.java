package com.h3w.service;

import com.h3w.entity.Fileupload;

import java.util.List;

public interface FileuploadService {
    public Fileupload getById(Integer id);

    public Fileupload getByFilename(String filename);

    public void insertSelect(Fileupload file);

    public void updateSelect(Fileupload file);

    public void deleteById(Integer id);

    public Fileupload getByMd5(String md5);

    public Long getCountByMd5(String md5);

}
