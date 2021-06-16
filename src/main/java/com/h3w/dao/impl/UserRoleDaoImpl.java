package com.h3w.dao.impl;

import com.h3w.dao.base.BaseDao;
import com.h3w.dao.UserRoleDao;
import com.h3w.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleDaoImpl extends BaseDao<UserRole,Integer> implements UserRoleDao {
}
