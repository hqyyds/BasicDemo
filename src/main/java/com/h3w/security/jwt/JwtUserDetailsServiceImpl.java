package com.h3w.security.jwt;

import com.h3w.entity.Role;
import com.h3w.entity.User;
import com.h3w.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义的UserDetailesService接口，获取认证数据源，只需要将用户信息（用户名，密码，权限列表）包装成UserDetailes
 * @author hyyds
 * @date 2021/6/16
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * 构建UserDetails对象，通过loadUserByUsername根据userName获取UserDetail对象 （可以在这里基于自身业务进行自定义的实现 如通过数据库，xml,缓存获取等）。
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("%s.这个用户不存在", username));
        }
        //Role.getCode后续权限验证需要
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream().map(Role::getCode).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new JwtUser(user.getUsername(), user.getPassword(), user.getStatus(), authorities);
    }
}
