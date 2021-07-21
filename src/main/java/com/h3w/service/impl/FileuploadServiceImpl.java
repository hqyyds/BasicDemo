package com.h3w.service.impl;

import com.h3w.dao.FileuploadDao;
import com.h3w.entity.Fileupload;
import com.h3w.service.FileuploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FileuploadServiceImpl implements FileuploadService {

    @Autowired
    private FileuploadDao fileuploadDao;

    @Override
    public Fileupload getById(Integer id) {
        return fileuploadDao.get(id);
    }

    @Override
    public Fileupload getByFilename(String filename) {
        return fileuploadDao.getByHQL("from Fileupload where filename=?1",filename);
    }

    @Override
    public void insertSelect(Fileupload file) {
        fileuploadDao.save(file);
    }

    @Override
    public void updateSelect(Fileupload file) {
        fileuploadDao.saveOrUpdate(file);
    }

    @Override
    public void deleteById(Integer id) {
        fileuploadDao.deleteById(id);
    }
}
