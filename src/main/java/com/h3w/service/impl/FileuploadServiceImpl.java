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

    @Override
    public Fileupload getByMd5(String md5) {
        List<Fileupload> items = fileuploadDao.getListByHQL("from Fileupload where filemd5=?1",md5);
        return items.size()>0?items.get(0):null;
    }

    @Override
    public Long getCountByMd5(String md5) {
        Long c = fileuploadDao.countByHql("select count(id) from Fileupload where filemd5=?1",md5);
        return c;
    }
}
