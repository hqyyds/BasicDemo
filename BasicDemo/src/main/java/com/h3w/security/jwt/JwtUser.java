package com.h3w.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author K. L. Mao
 * @create 2019/1/10
 */
public class JwtUser implements UserDetails {

    public static Integer STATUS_ENABLE = 0;

    private String username;

    private String password;

    private Integer status;

    private Collection<? extends GrantedAuthority> authorities;

    public JwtUser(String username, String password, Integer status, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Integer getStatus() {
        return status;
    }

    // 账户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 账户是否未被锁
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status==STATUS_ENABLE?true:false;
    }
}