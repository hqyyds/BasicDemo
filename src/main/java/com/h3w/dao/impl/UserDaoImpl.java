package com.h3w.dao.impl;

import com.h3w.dao.base.BaseDao;
import com.h3w.dao.UserDao;
import com.h3w.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends BaseDao<User, Integer> implements UserDao {

}
