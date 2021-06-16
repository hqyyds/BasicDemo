package com.h3w.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.h3w.security.jwt.JwtTokenUtil;
import com.h3w.utils.RedisUtil;
import com.h3w.ResultObject;
import com.h3w.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录拦截
 * @author hyyds
 * @date 2021/6/16
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 在DispatcherServlet之前执行
     * */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("LoginInterceptor executed");
        String access_token = request.getParameter("access_token");
        if(StringUtil.isNotBlank(access_token)){

            while (true) {
                //oa登录token,获取用户信息
                String status = "on";
                String url = ("msghttp.user.url") + access_token;
                if (status.equals("on")) {
                    System.out.println("OA单点登陆...");
                    String username = "管理员";
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    // 将用户信息存入 authentication，方便后续校验
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String token = jwtTokenUtil.generateToken(userDetails);
                    String res = JSONObject.toJSONString(ResultObject.success(token));
                    renderToken(response,res);

//                    String result = httpGet(url);
//                    System.out.println("查询oa用户返回结果：" + result);
//                    if (result != null) {
//                        JSONObject re = new JSONObject(result);
//                        if (re.has("name")) {
//                            String username = re.getString("name");
//                            System.out.println("查询oa用户：" + username);
//                            //自动登录
//                            String res = createToken(request, response, username, redirect);
//                            appPrint(response, res);
//                        }
//
//                    }
                    break;
                }
            }

//            throw new CustomException("-1","token参数不能为空");
        }
        return true;
        //读取Redis缓存数据
//        Object uid = redisUtil.get(token);
//        if(uid==null){
//            throw new CustomException("-1","token已失效");
//        }
//        try {
//            //JWT验证token
//            Payload payload = jwtUtil.verifyToken(token);
//            //获取JWT携带自定义信息
//            request.setAttribute("login_user_id", payload.getClaimValue("userid"));
//            return true;
//        } catch (AlgorithmMismatchException e) {
//            log.error("Token Checkout processing AlgorithmMismatchException 异常！"+e.getLocalizedMessage());
//            throw new CustomException("-1","不合法的token");
//        }catch (TokenExpiredException e) {
//            log.info("token已经过期");
//            throw new CustomException("1","token已经过期");
//        }catch (SignatureVerificationException e) {
//            log.error("Token Checkout processing SignatureVerificationException 异常！"+e.getLocalizedMessage());
//            throw new CustomException("2","token验证失败");
//        }catch (JWTDecodeException e){
//            log.error("Token Checkout processing SignatureVerificationException 异常！"+e.getLocalizedMessage());
//            throw new CustomException("-1","不合法的token");
//        }catch (Exception e) {
//            log.error("Token Checkout processing 未知异常！"+e.getLocalizedMessage());
//            throw e;
//        }

    }

    /**
     * 在controller执行之后的DispatcherServlet之后执行
     * */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在页面渲染完成返回给客户端之前执行
     * */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private void appPrint(HttpServletResponse response,String result){
//        response.reset();
        response.setContentType("text/json;charset=utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out;
        try {
            out = response.getWriter();
            out.print(result);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void renderToken(HttpServletResponse response, String str) throws IOException {
        //报错getWriter() has already been called for this response，可用getOutputStream()代替
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
