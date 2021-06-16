package com.h3w.filter;


import com.alibaba.fastjson.JSONObject;
import com.h3w.ResultObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 防止XSS 攻击和SQL注入攻击的Filter
 * @author hyyds
 * @date 2021/6/16
 */
public class XssAndSqlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String method = "GET";//设置初始值
        String param = "";
        XssAndSqlHttpServletRequestWrapper xssRequest = null;
        if (request instanceof HttpServletRequest) {//判断左边的对象是否是它右边对象的实例
            method = ((HttpServletRequest) request).getMethod();//得到请求URL地址时使用的方法
            xssRequest = new XssAndSqlHttpServletRequestWrapper((HttpServletRequest) request);//创建对象
        }
        if ("POST".equalsIgnoreCase(method)) {//判断是否为post
            param = this.getBodyString(xssRequest.getReader());//获取参数
            if(StringUtils.isNotBlank(param)){//等价于 str != null && str.length > 0 && str.trim().length > 0
                if(xssRequest.checkXSSAndSql(param)){//进行参数审查
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.write(JSONObject.toJSONString(ResultObject.newError("您所访问的请求中有违反安全规则元素存在，拒绝访问!")));
                    return;
                }
            }
        }

        /**
         * 检查参数的时候 同时检查请求的方法
         * 只检查get请求方法和post请求方法的的参数的数据是否合法
         * 并不是所有参数都要检查，首先必须是一个get或者post，再去校验参数
         * 因为PUT方法在进行参数审查的时候没办法通过所以直接过滤掉
         */
        if (xssRequest.checkParameter()&&(method.equals("POST") || method.equals("GET"))){
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write(JSONObject.toJSONString(ResultObject.newError("您所访问的请求中有违反安全规则元素存在，拒绝访问!")));
            return;
        }
        chain.doFilter(xssRequest, response);

    }

    // 获取request请求body中参数
    public static String getBodyString(BufferedReader br) {
        String inputLine;
        String str = "";
        try {
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return str;
    }

}
