package com.h3w.security.handler;

import com.h3w.security.jwt.JwtUserDetailsServiceImpl;
import com.h3w.utils.MD5Util;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义的authenticationProvider认证逻辑
 * @author hyyds
 * @date 2021/6/16
 */
@Component
public class MyAuthenticationProvide implements AuthenticationProvider {

    @Autowired
    private JwtUserDetailsServiceImpl userDetailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        if(userDetails==null){
            throw new BadCredentialsException("用户名不存在");
        }
        if(!MD5Util.validPassword(password, userDetails.getPassword())){
            throw new BadCredentialsException("密码不正确");
        }
//        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//            throw new BadCredentialsException("密码不正确");
//        }
        if(!userDetails.isEnabled()){
            throw new BadCredentialsException("用户未启用");
        }
        /*用户名密码认证成功*/
        return new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
