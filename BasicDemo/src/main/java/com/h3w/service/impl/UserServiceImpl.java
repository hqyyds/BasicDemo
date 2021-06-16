package com.h3w.service.impl;

import com.h3w.dao.LogDao;
import com.h3w.dao.UserDao;
import com.h3w.dao.UserRoleDao;
import com.h3w.entity.Log;
import com.h3w.entity.Role;
import com.h3w.entity.User;
import com.h3w.entity.UserRole;
import com.h3w.security.jwt.JwtTokenUtil;
import com.h3w.security.jwt.JwtUser;
import com.h3w.service.UserService;
import com.h3w.utils.DateUtil;
import com.h3w.utils.Page;
import com.h3w.utils.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
//@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private LogDao logDao;

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    public UserServiceImpl(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Integer register(User userToAdd) {
        final String username = userToAdd.getUsername();
        User ouser = userDao.getByHQL("from User where username=?1",username);
        if(ouser!=null) {
            return -1;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        final String rawPassword = userToAdd.getPassword();
        userToAdd.setPassword(DigestUtils.md5Hex(rawPassword));
        userToAdd.setPasswordn(encoder.encode(rawPassword));
        userToAdd.setStatus(0);
//        userToAdd.setRoles(Arrays.asList("ROLE_USER"));
        return userDao.save(userToAdd);
    }

    @Override
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

    @Override
    public String refresh(String token) {
//        final String token = oldToken.substring(tokenHead.length());
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.validateToken(token, user)){
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userDao.getByHQL("from User where username=?1",username);
        if(user==null){
            return user;
        }
        //查询用户角色
        List<UserRole> userRoles = user.getUserRoles();
        List<Role> roles = new ArrayList<>();
        for(UserRole ur: userRoles){
            roles.add(ur.getRole());
        }
        user.setRoles(roles);
        return user;
    }

    @Override
    public String getNameByUserid(Integer userid) {
        Object o = userDao.getBySQL("select realname from sys_user where id=?1", userid);
        return o != null?o.toString():"";
    }

    @Override
    public List<User> findAll(){
        return userDao.getListByHQL("from User where status!=?1 order by id",User.STATUS_DELETED);
    }

    @Override
    public List<User> findUserByDep(Integer depid) {
        String hql = "from User where status!=2";
        if(depid != null){
            hql += " and dept.id="+depid;
        }
        hql += " order by seq";
        return userDao.getListByHQL(hql);
    }

    @Override
    public List<User> selectByDeptidAndStr(Integer deptid, String str) {
        List<Object> param = new ArrayList<>();
        String hql = "from User where status!=-1 and depid=?1";
        param.add(deptid);
        if(StringUtil.isNotBlank(str)){
            hql+= " and username like '%'||?2||'%' or realname like '%'||?3||'%' ";
            param.add(str);
            param.add(str);
        }
        return userDao.getListByHQL(hql,param.toArray());
    }

    @Override
    public void insertSelect(User User) {
        userDao.save(User);
    }

    @Override
    public void updateSelect(User user) {
        userDao.saveOrUpdate(user);
    }

    @Override
    public User getById(Integer id) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("线程名：" + Thread.currentThread().getName());
        return userDao.getByHQL("from User where id=?1",id);
    }

    @Override
    public void insertUserRoleSelect(UserRole userRole) {
        userRoleDao.save(userRole);
    }

    @Override
    public void deleteUserRoleByUserid(Integer uid) {
        userRoleDao.queryHql("Delete FROM UserRole where userid=?1",uid);
    }

    @Override
    public List<UserRole> getUserRoleList(Integer uid) {
        return userRoleDao.getListByHQL("from UserRole where userid=?1",uid);
    }

    @Override
    public User findByUsernameAndNotId(String username, Integer id) {
        return userDao.getByHQL("from User where username=?1 and id!=?2  and status!=-1", username,id);
    }

    @Override
    public void changeStatusById(Integer status,Integer id) {
        String hql = "update User set status=?1 where id=?2";
        userDao.queryHql(hql, status,id);
    }

    @Override
    public Page<Log> queryLogs(Page<Log> page, String logtime, String name) {
        String hql = "from Log where 1=1";
        List<Object> param = new ArrayList<>();
        if(StringUtil.isNotBlank(logtime)){
            Date date = DateUtil.parseDate(logtime,"yyyy-MM-dd");
            Date sday = DateUtil.getFirstTimeOfDay(date);
            Date eday = DateUtil.getLastTimeOfDay(date);
            hql+= " and logtime>=?"+(param.size()+1);
            param.add(sday);
            hql+= "  and logtime<=?"+(param.size()+1);
            param.add(eday);
        }
        if(StringUtil.isNotBlank(name)){
            hql+= " and realname like '%"+name+"%'";
        }
        hql += " order by logtime desc";
        return logDao.findPageByFetchedHql(hql, page,param.toArray());
    }

    @Override
    public void saveLog(Log log) {
        logDao.save(log);
    }
}
