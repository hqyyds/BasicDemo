package com.h3w.security.permission;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * 自定义的权限控制管理器
 *
 * @author hyyds
 * @date 2021/6/16
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {

    /**
     * 判断是否有权限
     *
     * @param auth             包含了UserDetails用户信息
     * @param object           包含了request请求信息
     * @param configAttributes 由CustomFilterInvocationSecurityMetadataSource.getAttributes(object)返回的请求的资源（url）所需要的权限（角色）集合
     */
    @Override
    public void decide(Authentication auth, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        /**
         * 如果请求的资源不需要权限，则直接放行
         */
        if (configAttributes == null || configAttributes.size() <= 0) {
            return;
        }
        /**
         * 判断用户所拥有的权限是否是资源所需要的权限之一，如果是则放行，否则拦截
         */
        Iterator<ConfigAttribute> iter = configAttributes.iterator();
        while (iter.hasNext()) {
            String needRole = iter.next().getAttribute();
            for (GrantedAuthority grantRole : auth.getAuthorities()) {
                if (needRole.trim().equals(grantRole.getAuthority().trim())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("没有调用权限");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}

