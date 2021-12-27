package com.h3w.dao.impl;

import com.h3w.dao.PermissionDao;
import com.h3w.dao.base.BaseDao;
import com.h3w.entity.Permission;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionDaoImpl extends BaseDao<Permission, Integer> implements PermissionDao {
}
