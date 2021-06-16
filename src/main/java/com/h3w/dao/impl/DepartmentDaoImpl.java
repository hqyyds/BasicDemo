package com.h3w.dao.impl;

import com.h3w.dao.base.BaseDao;
import com.h3w.dao.DepartmentDao;
import com.h3w.entity.Department;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentDaoImpl extends BaseDao<Department,Integer> implements DepartmentDao {
}
