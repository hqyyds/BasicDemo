package com.h3w.service;

import com.h3w.entity.Fileupload;

import java.util.List;

/**
 * Created by hyyd on 2017/6/30.
 */
public interface FileuploadService {
    public Fileupload getById(Integer id);

    public Fileupload getByFilename(String filename);

    public void insertSelect(Fileupload file);

    public void updateSelect(Fileupload file);

    public void deleteById(Integer id);

    List<Fileupload> getListBySdetailsid(Integer sdetailid);

    List<Object> getIDListBySdetailsid(Integer sdetailid);

    List<Fileupload> getListByMeetid(Integer meetid);

}