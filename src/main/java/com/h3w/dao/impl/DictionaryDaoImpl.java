package com.h3w.dao.impl;

import com.h3w.dao.DictionaryDao;
import com.h3w.dao.base.BaseDao;
import com.h3w.entity.Dictionary;
import org.springframework.stereotype.Repository;

@Repository
public class DictionaryDaoImpl extends BaseDao<Dictionary, Integer> implements DictionaryDao {
}
