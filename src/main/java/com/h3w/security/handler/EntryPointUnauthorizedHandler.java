package com.h3w.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.h3w.ResultObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 身份校验失败处理器，如 token 错误
 * @author hyyds
 * @date 2021/6/16
 */
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        String str = JSONObject.toJSONString(ResultObject.error("身份校验失败"));
        out.write(str);
        out.flush();
    }
}
