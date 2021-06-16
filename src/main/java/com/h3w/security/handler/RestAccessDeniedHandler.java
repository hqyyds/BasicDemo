package com.h3w.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.h3w.ResultObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 权限校验处理器
 * @author K. L. Mao
 * @create 2019/1/11
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        String str = JSONObject.toJSONString(ResultObject.error("权限校验失败"));
        out.write(str);
        out.flush();
    }
}
