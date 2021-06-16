package com.h3w.dao.impl;

import com.h3w.dao.base.BaseDao;
import com.h3w.dao.RoleDao;
import com.h3w.entity.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl extends BaseDao<Role,Integer> implements RoleDao {
}
