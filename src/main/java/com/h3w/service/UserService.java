package com.h3w.service;

import com.h3w.entity.Log;
import com.h3w.entity.User;
import com.h3w.entity.UserRole;
import com.h3w.utils.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {

    //注册登陆刷新
    Integer register(User userToAdd);

    String login(String username, String password);

    String refresh(String oldToken);

    User getUserByUsername(String username);

    String getNameByUserid(Integer userid);

    List<User> findAll();

    List<User> findUserByDep(Integer depid);

    List<User> selectByDeptidAndStr(Integer deptid, String str);

    void insertSelect(User User);

    void updateSelect(User user);

    User getById(Integer id);

    void insertUserRoleSelect(UserRole userRole);

    void deleteUserRoleByUserid(Integer uid);

    List<UserRole> getUserRoleList(Integer uid);

    User findByUsernameAndNotId(String username, Integer id);

    void changeStatusById(Integer status, Integer id);

    Page<Log> queryLogs(Page<Log> page, Integer optype, Integer userid, String logtime, String name);

    void saveLog(Log log);
}
