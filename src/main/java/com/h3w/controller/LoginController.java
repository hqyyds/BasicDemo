package com.h3w.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3w.entity.Permission;
import com.h3w.entity.Role;
import com.h3w.ResultObject;
import com.h3w.entity.User;
import com.h3w.service.SysService;
import com.h3w.service.UserService;
import com.h3w.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;


/**
 *
 * @author hyyds
 * @date 2021/6/16
 */
@RestController
public class LoginController {

	Logger log = LoggerFactory.getLogger(LoginController.class);
	public static ResourceBundle resource = ResourceBundle.getBundle("constant");
	public static final String EQUIPMENT_MENT = resource.getString("equipment.url");

	@Value("${tokentime:#{null}}")
	private Integer tokentime;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisUtil redisUtil;

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private UserService authService;
    @Autowired
    private SysService sysService;

	/**
	 * 授权获取Token,用于调用接口的凭证
	 * @param username
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	@ResponseBody
	@RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
	public String createAuthenticationToken(String username,String password) throws AuthenticationException {
		final String token = authService.login(username, password);

		// Return the token
		JSONObject result = new JSONObject();
		result.put("token", token);
		return ResultObject.success(result).toString();
	}

	/**
	 * 刷新Token,刷新Token有效期
	 * @param request
	 * @return
	 * @throws AuthenticationException
	 */
	@RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
	public String refreshAndGetAuthenticationToken(
			HttpServletRequest request) throws AuthenticationException{
		String token = request.getHeader(tokenHeader);
		String refreshedToken = authService.refresh(token);
		if(refreshedToken == null) {
			return ResultObject.newError("刷新失败").toString();
		} else {
			return ResultObject.success(refreshedToken).toString();
		}
	}


	@ResponseBody
	@RequestMapping(value = "auth/register", method = RequestMethod.POST)
	public String register(User addedUser) throws AuthenticationException{
		Integer r = authService.register(addedUser);
		if(r!=null && r ==-1){
			return ResultObject.newError("用户已存在").toString();
		}
		return ResultObject.newOk("新增成功").toString();
	}

	public User getCurrentUser() {
//		JwtUser user=(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			User currentUser = userService.getUserByUsername(currentUserName);
			if(currentUser!=null){
				return currentUser;
			}
		}
		return null;
	}

	@RequestMapping(value = "/myinfo", method = RequestMethod.GET)
	@ResponseBody
	public String myinfo() {
//		JwtUser user=(JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			User currentUser = userService.getUserByUsername(currentUserName);
			if(currentUser!=null){
				JSONObject obj = new JSONObject();
				JSONArray rows = new JSONArray();
				JSONArray menus = new JSONArray();
				obj.put("id",currentUser.getId());
				obj.put("username",currentUser.getUsername());
				obj.put("status",currentUser.getStatus());
				obj.put("realname",currentUser.getRealname());
				obj.put("depid",currentUser.getDept()!=null?currentUser.getDept().getId():null);
				obj.put("depname",currentUser.getDept()!=null?currentUser.getDept().getName():null);
				obj.put("photourl", EQUIPMENT_MENT+currentUser.getPhotopath());//照片路径
				List<Role> roles = currentUser.getRoles();
				for(Role r: roles){
					JSONObject o = new JSONObject();
					o.put("code",r.getCode());
					o.put("name",r.getName());
					o.put("level",r.getLevel());
					rows.add(o);

                    //查询用户角色权限
                    List<Permission> list = sysService.findPermissionListByRoleId(r.getCode());
                    for(Permission p:list){
                        JSONObject node = new JSONObject();
                        node.put("code", p.getCode());
                        node.put("parentcode", p.getParentcode()==null?"":p.getParentcode());
                        node.put("url", p.getUrl()==null?"":p.getUrl());
                        node.put("name", p.getName());
                        node.put("seq", p.getSeq()==null?"":p.getSeq());
                        node.put("type", p.getType()==null?"":p.getType());
                        menus.add(node);
                    }
				}
				obj.put("roles",rows);
				obj.put("menus",menus);
				return ResultObject.success(obj).toString();
			}
		}
		return ResultObject.newError("未找到当前用户").toString();
	}

    /**
     * 获取用户菜单信息
     * @param request
     * @return
     * @throws JSONException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/mymenu", method = RequestMethod.GET)
    public String userMenu(HttpServletRequest request) {
        User user = getCurrentUser();

        org.json.JSONArray array = new org.json.JSONArray();
        String rolecode = "";
        List<Role> roles = user.getRoles();
        for(Role r: roles){
            //查询用户角色权限
            List<Permission> list = sysService.findPermissionListByRoleId(r.getCode());
            for(Permission p:list){
                org.json.JSONObject node = new org.json.JSONObject();
                node.put("code", p.getCode());
                node.put("parentcode", p.getParentcode()==null?"":p.getParentcode());
                node.put("url", p.getUrl()==null?"":p.getUrl());
                node.put("name", p.getName());
                node.put("seq", p.getSeq()==null?"":p.getSeq());
                node.put("type", p.getType()==null?"":p.getType());
                array.put(node);
            }
        }
        return ResultObject.newJSONRows(array).toString();
    }

	@ResponseBody
	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String login(){
		return "login";
	}

	/**
	 * 登录验证，目前无用
	 * @param request
	 * @param response
	 * @param username
	 * @param password
	 * @return

	@ResponseBody
	@RequestMapping(value = "loginPost")
	public String loginAjax(HttpServletRequest request, HttpServletResponse response,
                            String username, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if(StringUtil.isBlank(username)){
			return  ResultObject.error("用户名不能为空").toString();//用户名不能为空
		}
		if(StringUtil.isBlank(password)){
			return  ResultObject.error("密码不能为空").toString();//密码不能为空
		}

		User loginUser = userService.getUserByUsername(username);
		if(loginUser == null){
			return ResultObject.error("用户不存在").toString();//用户不存在
		}else if(MD5Util.validPassword(password, loginUser.getPassword())){//

			//携带自定义信息生成JWT token，作为登陆成功所用参数
			Map<String,String> userInfo = new HashMap<String,String>();
			userInfo.put("username", username);
			userInfo.put("userid", String.valueOf(loginUser.getId()));
			//过期时间
			Long exptime = System.currentTimeMillis()+tokentime;
			String token = "";
//			String token = jwtUtil.createToken(userInfo, exptime);
			log.info("token:"+token);

			//放入Redis缓存
			//redisUtil.set(token,loginUser.getId());

			JSONObject result = new JSONObject();
			result.put("token", token);
			result.put("exptime", exptime);
			return ResultObject.success(result).toString();

		}else{
			return ResultObject.success("密码错误，登录失败").toString();//密码错误
		}
	}*/

	@GetMapping("/logout")
	public String logout(String token,String issuer,HttpServletRequest request) {
		ServletContext context = request.getServletContext();
		context.removeAttribute(token);
		return ResultObject.success().toString();
	}

}
