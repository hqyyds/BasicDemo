package com.h3w.dao.impl;

import com.h3w.dao.base.BaseDao;
import com.h3w.dao.FileuploadDao;
import com.h3w.entity.Fileupload;
import org.springframework.stereotype.Repository;

@Repository
public class FileuploadDaoImpl extends BaseDao<Fileupload,Integer> implements FileuploadDao {
}
