package com.h3w.dao.impl;

import com.h3w.dao.LogDao;
import com.h3w.dao.base.BaseDao;
import com.h3w.entity.Log;
import org.springframework.stereotype.Repository;

@Repository
public class LogDaoImpl extends BaseDao<Log, Integer> implements LogDao {
}
