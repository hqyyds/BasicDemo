package com.h3w.dao;

import com.h3w.dao.base.IBaseDao;
import com.h3w.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends IBaseDao<User,Integer> {
}
