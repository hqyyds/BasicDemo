package com.h3w.controller;

import com.h3w.ResultObject;
import com.h3w.StaticConstans;
import com.h3w.annotation.NoRepeatSubmit;
import com.h3w.annotation.ParamCheck;
import com.h3w.annotation.PerResource;
import com.h3w.entity.*;
import com.h3w.enums.FunEnum;
import com.h3w.service.SysService;
import com.h3w.service.UserService;
import com.h3w.utils.*;
import io.swagger.annotations.Api;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统管理
 * @author hyyds
 * @date 2021/6/16
 */
@Api(value = "系统管理接口", tags = "系统管理接口")
@RestController
@RequestMapping("/sys")
public class SysController {

    @Autowired
    private SysService sysService;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginController loginController;
    @Autowired
    FileuploadController fileuploadController;

    public static SysController baseController;
    @PostConstruct
    public void init() {
        baseController = this;
        baseController.sysService = this.sysService;
    }

    /**
     * 用户列表
     * @param deptid
     * @param request
     * @return
     * @throws JSONException
     */
    @com.h3w.annotation.Log(action = "用户列表",dataid = "#name")
    @GetMapping("/user/list")
    public String getUserlist(Integer deptid, HttpServletRequest request,String str) throws JSONException {

        List<User> items = userService.selectByDeptidAndStr(deptid,str);
        JSONArray array = new JSONArray();
        for(User user : items){
            String roleNames = "";
            List<UserRole> userRoles = user.getUserRoles();
            for (UserRole ur : userRoles) {
                roleNames += ur.getRole().getName() + ",";
            }
            JSONObject obj = new JSONObject();
            obj.put("id",user.getId());
            obj.put("username",user.getUsername());
            obj.put("realname",user.getRealname());
            obj.put("seq",user.getSeq());
            obj.put("phone",user.getPhone());
            obj.put("email",user.getEmail());
            obj.put("remark",user.getRemark());
            obj.put("createtimestr",DateUtil.formatDate(user.getCreatetime(),"yyyy-MM-dd"));
            obj.put("status",user.getStatus());
            obj.put("statusname",User.statusMap.get(user.getStatus()));
            obj.put("depid",user.getDept()!=null?user.getDept().getId():null);
            obj.put("deptname",user.getDept()!=null?user.getDept().getName():null);
            obj.put("rolenames",roleNames);
            array.put(obj);
        }
        return ResultObject.newJSONRows(array).toString();
    }

    /**
     * 查询部门用户树
     * @param request
     * @return
     */
    @GetMapping(value = "/user/tree")
    public String depUserData(HttpServletRequest request){
        List<Department> list = sysService.findAllDep();
        JSONObject result = new JSONObject();
        result.put("statusCode", 200);
        result.put("message", "查询成功");
        JSONArray data = new JSONArray();
        for(Department dep:list){
            JSONObject node = new JSONObject();
            node.put("id", "d"+dep.getId());
            node.put("deptid", dep.getId());
            node.put("name", dep.getName());
            JSONArray users = new JSONArray();
            List<User> userList = userService.findUserByDep(dep.getId());
            for(User u:userList){
                JSONObject user = new JSONObject();
                user.put("id", u.getId().toString());
                user.put("userid", u.getId());
                user.put("name", u.getRealname());
                users.put(user);
            }
            node.put("users", users);
            data.put(node);
        }
        result.put("data", data);
        return result.toString();
    }

    @PerResource(url = "/sys/user/create",name = "创建用户",roles = "sysgly",fun = FunEnum.CREATE)
    @PostMapping(value = "/user/create")
    public ResultObject create(User user,String rcodes){
        User ouser = userService.getUserByUsername(user.getUsername());
        if(ouser!= null){
            return ResultObject.newError("用户名不能重复");
        }

        user.setStatus(User.STATUS_ENABLE);
        user.setCreatetime(new Date());
        try{
            String psd = StaticConstans.PASSWORD_INIT;
            if(StringUtil.isNotBlank(user.getPassword())){
                psd = user.getPassword();
            }
            user.setPassword(DigestUtils.md5Hex(psd));
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            List<UserRole> userRoles = new ArrayList<>();
            if(StringUtil.isNotBlank(rcodes)){
                String[] arr = rcodes.split(",");
                for(String code: arr){
                    UserRole ur = new UserRole();
                    ur.setUser(user);
                    ur.setRole(sysService.getRoleByCode(code));
                    userRoles.add(ur);
                }
            }
            user.setUserRoles(userRoles);
            userService.insertSelect(user);

            return ResultObject.newOk("添加成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统内部错误！");
        }
    }

    @GetMapping("/get/{id}")
    public String getUser(@PathVariable Integer id) throws IOException, JSONException {
        User user = userService.getById(id);
        if(user== null){
            return ResultObject.newError("用户不存在").toString();
        }
        JSONObject obj = new JSONObject();
        obj.put("id",user.getId());
        obj.put("username",user.getUsername());
        obj.put("realname",user.getRealname());
        obj.put("seq",user.getSeq());
        obj.put("phone",user.getPhone());
        obj.put("email",user.getEmail());
        obj.put("remark",user.getRemark());
        obj.put("createtimestr", DateUtil.formatDate(user.getCreatetime(),"yyyy-MM-dd"));
        obj.put("status",user.getStatus());
        obj.put("statusname",User.statusMap.get(user.getStatus()));
        obj.put("depid",user.getDept()!=null?user.getDept().getId():null);
        obj.put("deptname",user.getDept()!=null?user.getDept().getName():null);
        String roleNames = "";
        JSONArray roleCodes = new JSONArray();
        List<UserRole> userRoles = user.getUserRoles();
        for (UserRole ur : userRoles) {
            roleNames += ur.getRole().getName() + ",";
            roleCodes.put(ur.getRole().getCode());
        }
        obj.put("rolenames",roleNames);
        obj.put("rolecodes",roleCodes);
        return ResultObject.newJSONData(obj).toString();
    }

    @PerResource(url = "/sys/user/edit",name = "创建用户",roles = "sysgly",fun = FunEnum.CREATE)
    @PostMapping(value = "/user/edit")
    public ResultObject edit(User user,String rcodes){

        Integer uid = user.getId();
        User ouser = userService.findByUsernameAndNotId(user.getUsername(),uid);
        if(ouser!= null){
            return ResultObject.newError("用户名不能重复");
        }

        User nuser = userService.getById(uid);
        selectUser(nuser,user);
        try {
            List<UserRole> userRoles = nuser.getUserRoles();
            userRoles.clear();
//            userService.deleteUserRoleByUserid(uid);
            if(StringUtil.isNotBlank(rcodes)){
                String[] arr = rcodes.split(",");
                for(String code: arr){
                    UserRole ur = new UserRole();
                    ur.setUser(nuser);
                    ur.setRole(sysService.getRoleByCode(code));
                    userRoles.add(ur);
                }
            }
            userService.updateSelect(nuser);
            return ResultObject.newOk("修改成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统内部错误！");
        }
    }

    public void selectUser(User nuser, User us){
        if(us.getStatus()!= null){
            nuser.setStatus(us.getStatus());
        }
        if(StringUtil.isNotBlank(us.getUsername())){
            nuser.setUsername(us.getUsername());
        }
        if(StringUtil.isNotBlank(us.getPhone())){
            nuser.setPhone(us.getPhone());
        }
        if(us.getDeptid() != null){
            nuser.setDept(sysService.getDepartmentById(us.getDeptid()));
        }
        if(StringUtil.isNotBlank(us.getRemark())){
            nuser.setRemark(us.getRemark());
        }
        if(us.getSeq() != null){
            nuser.setSeq(us.getSeq());
        }
        if(StringUtil.isNotBlank(us.getEmail())){
            nuser.setEmail(us.getEmail());
        }
        if(StringUtil.isNotBlank(us.getPassword())){
            nuser.setPassword(DigestUtils.md5Hex(us.getPassword()));
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        }
    }

    /**
     * 上传头像
     * @param userid
     * @param request
     * @return
     * @throws IOException
     * @throws JSONException
     */
    @PostMapping(value = "/user/updateHead")
    public ResultObject editHead(Integer userid,HttpServletRequest request) throws IOException {

        String re = fileuploadController.uploadFile(request);
        JSONObject reobj = new JSONObject(re);
        String filepath = reobj.getString("fileurl");

        User user = userService.getById(userid);
        if(user==null){
            return ResultObject.newError("未找到数据");
        }
        try {
            user.setImgurl(filepath);
            userService.updateSelect(user);
            return ResultObject.newOk("保存成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统内部错误！");
        }
    }

    @PerResource(url = "/sys/user/delmany",name = "批量删除用户",roles = "sysgly",fun = FunEnum.DELETE)
    @PostMapping(value = "/user/delmany")
    public ResultObject deleteMany(String ids){
        try{
            String[] arr = ids.split(",");
            for(String id: arr){
                userService.changeStatusById(User.STATUS_DEL_ED,Integer.valueOf(id));
            }
            return ResultObject.newOk("已删除！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统内部错误！");
        }
    }

    @PerResource(url = "/sys/user/del",name = "删除用户",roles = "sysgly",fun = FunEnum.DELETE)
    @GetMapping("/user/del")
    public ResultObject delete(Integer id){
        try{
            userService.changeStatusById(User.STATUS_DEL_ED,id);
            return ResultObject.newOk("已删除！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统内部错误！");
        }
    }


    /**
     * 重置密码
     * @param ids
     * @return
     */
    @PerResource(url = "/sys/user/resetpsd",name = "重置密码",roles = "sysgly",fun = FunEnum.UPDATE)
    @PostMapping(value = "/user/resetpsd")
    public ResultObject resetPasswordn(String ids){
        try{
            String[] arr = ids.split(",");
            for(String id: arr){
                User user = userService.getById(Integer.valueOf(id));
                if(user!=null){
                    user.setPassword(DigestUtils.md5Hex(StaticConstans.PASSWORD_INIT));
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    userService.updateSelect(user);
                }
            }
            return ResultObject.newOk("重置成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统内部错误！");
        }
    }


    /**
     * 室办列表
     * @return
     * @throws JSONException
     */
    @GetMapping("/department/list")
    public String getDepartmentList() throws JSONException {
        //科室显示顺序排序
        try {
            List<Department> items = sysService.getAllDepartmentByParentid(Department.ROOT_ID);
            JSONArray array = new JSONArray();
            for(Department d: items){
                JSONObject obj = new JSONObject();
                //如果是大队，就把id置为空
                obj.put("id",d.getId());
                obj.put("name",d.getName());
                array.put(obj);
            }

            return ResultObject.newJSONRows(array).toString();
        }catch (JSONException e){
            e.printStackTrace();
            return ResultObject.newError("系统错误").toString();
        }
    }

    @GetMapping("/department/listTree")
    public String getDepartmentList(Integer deptid,Integer flag) throws IOException, JSONException {

        JSONArray array = new JSONArray();
        Department rd = sysService.getDepartmentById(Department.ROOT_ID);
        if(deptid != null){
            rd = sysService.getDepartmentById(deptid);
        }
        JSONObject o = new JSONObject();
        o.put("id", rd.getId());
        o.put("name", rd.getName());

        List<Department> childList = sysService.getAllDepartmentByParentid(rd.getId());
        o.put("havechild",childList.size()>0?1:0);
//        o.put("havechild",Department.HAVECHILD_YES.equals(rd.getHavechild())?1:0);
        o.put("orgs",getDeptArray(childList));
        array.put(o);
        return ResultObject.newJSONRows(array).toString();
    }

    public JSONArray getDeptArray(List<Department> deptList) throws JSONException {
        JSONArray array = new JSONArray();
        for (Department dep : deptList) {
            JSONObject o = new JSONObject();
            o.put("id", dep.getId());
            o.put("name", dep.getName());
            List<Department> childList = sysService.getAllDepartmentByParentid(dep.getId());
            o.put("havechild",childList.size()>0?1:0);
            o.put("orgs",getDeptArray(childList));
            array.put(o);
        }
        return array;
    }

    @PerResource(url = "/sys/department/create",name = "新增部门",roles = "sysgly",fun = FunEnum.CREATE)
    @PostMapping(value = "/department/create")
    public ResultObject departmentCreate(Department department){
        if(StringUtil.isBlank(department.getName())){
            return ResultObject.newError("参数错误");
        }
        try {
            if(department.getParentid()== null){
                department.setParentid(Department.ROOT_ID);
            }
            department.setFlag(Department.FLAG_INIT);
            sysService.insertDepartment(department);
            return ResultObject.newOk("新增成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统错误");
        }
    }

    @PerResource(url = "/sys/department/update",name = "修改部门",roles = "sysgly",fun = FunEnum.UPDATE)
    @PostMapping(value = "/department/update")
    public ResultObject departmentUpdate(@ApiIgnore Department department){
        if(department.getId() == null){
            return ResultObject.newError("参数错误");
        }
        Department odepartment = sysService.getDepartmentById(department.getId());
        if(odepartment== null){
            return ResultObject.newError("未找到组织");
        }
        try {
            if(StringUtil.isNotBlank(department.getName())){
                odepartment.setName(department.getName());
            }
            if(StringUtil.isNotBlank(department.getRemark())){
                odepartment.setRemark(department.getRemark());
            }
            if(department.getParentid()!=null){
                odepartment.setParentid(department.getParentid());
            }
            sysService.updateDepartment(odepartment);
            return ResultObject.newOk("修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统错误");
        }
    }

    @PerResource(url = "/sys/department/del",name = "删除部门",roles = "sysgly",fun = FunEnum.DELETE)
    @PostMapping("/department/del")
    public ResultObject departmentDel(Integer did){
        Department department = sysService.getDepartmentById(did);
        if(department== null){
            return ResultObject.newError("未找到组织");
        }
        List<User> userList = userService.selectByDeptidAndStr(did,null);
        if(userList.size()>0){
            return ResultObject.newError("组织下面存在用户，不能删除！");
        }
        try {
            sysService.deleteDepartmentById(did);
            return ResultObject.newOk("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统错误");
        }
    }

    @GetMapping("/department/get")
    public String departmentGet(Integer id) throws JSONException, IOException {
        Department department = sysService.getDepartmentById(id);
        if(department==null){
            return ResultObject.newError("未找到部门").toString();
        }
        JSONObject obj = new JSONObject();
        obj.put("id",department.getId());
        obj.put("name",department.getName());
        obj.put("code",department.getName());
        obj.put("parentid",department.getParentid());
        obj.put("code",department.getCode());
        obj.put("flag",department.getFlag());
        obj.put("icon",department.getIcon());
        obj.put("remark",department.getRemark()!=null?department.getRemark():"");
        return ResultObject.newJSONData(obj).toString();
    }


    /**
     * 角色列表
     * @return
     * @throws JSONException
     */
    @GetMapping("/role/list")
    public String roleList() throws JSONException, IOException {
        List<Role> roles = sysService.findRoleAll();
        JSONArray array = new JSONArray();
        for(Role r: roles){
            JSONObject obj = new JSONObject();
            obj.put("code",r.getCode());
            obj.put("name",r.getName());
            obj.put("description",r.getDescription());
            obj.put("level",r.getLevel());
            array.put(obj);
        }
        return ResultObject.newJSONRows(array).toString();
    }

    /**
     * 角色级别列表
     * @return
     * @throws IOException
     * @throws JSONException
     */
    @GetMapping("/role/levels")
    public String levels() throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        String levels[] = {"超级管理员","管理员","普通用户"};
        for(int i=0; i<levels.length; i++){
            JSONObject obj = new JSONObject();
            obj.put("level",i+1);
            obj.put("name",levels[i]);
            array.put(obj);
        }
        object.put("statusCode", ResultObject.STATUS_CODE_SUCCESS);
        object.put("rows",array);
        return object.toString();
    }

    @GetMapping("/role/get")
    public String getRole(String rcode) throws JSONException, IOException {
        Role role = sysService.getRoleByCode(rcode);
        if(role==null){
            return ResultObject.newError("未找到角色").toString();
        }
        JSONObject obj = new JSONObject();
        obj.put("code",role.getCode());
        obj.put("level",role.getLevel());
        obj.put("description",role.getDescription());
        obj.put("name",role.getName());
        return ResultObject.newJSONData(obj).toString();
    }


    /**
     * 角色新增
     * @param role
     * @param pcodes
     * @return
     */
    @com.h3w.annotation.Log(action = "角色新增",optype = Log.OPTYPE_SYS_MANAGE)
    @PerResource(url = "/sys/role/create",name = "角色新增",roles = "sysgly",fun = FunEnum.CREATE)
    @NoRepeatSubmit(param = "#role.code")
    @GetMapping("/role/create")
    public ResultObject createRole(Role role, String pcodes){
        if(StringUtil.isBlank(role.getCode())){
            role.setCode(StringUtil.getCharAndNumr(6));
        }
        Role r = sysService.getRoleByCode(role.getCode());
        if(r!=null){
            return ResultObject.newError("角色代码已存在");
        }
        try {
            String[] arr = pcodes.split(",");
            for(String percode: arr){
                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(sysService.getPermissionByCode(percode));
                sysService.insertRolePermission(rp);
            }
            sysService.insertRole(role);
            return ResultObject.newOk("保存成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("保存错误");
        }
    }
    /**
     * 角色修改
     * @param or
     * @param pcodes
     * @return
     */
    @com.h3w.annotation.Log(action = "角色修改",optype = Log.OPTYPE_SYS_MANAGE)
    @PerResource(url = "/sys/role/update",name = "角色修改",roles = "sysgly",fun = FunEnum.UPDATE)
    @PostMapping("/role/update")
    public ResultObject updateRole(Role or, String pcodes){

        Role role = sysService.getRoleByCode(or.getCode());
        if(role== null){
            return ResultObject.newError("未找到角色");
        }
        if(StringUtil.isNotBlank(or.getName())){
            role.setName(or.getName());
        }
        if(StringUtil.isNotBlank(or.getDescription())){
            role.setDescription(or.getDescription());
        }
        if(or.getLevel()!=null){
            role.setLevel(or.getLevel());
        }
        try {
            if(StringUtil.isNotBlank(pcodes)){
                //先清空之前的数据
                sysService.deleteRolePermissionByRolecode(role.getCode());
                //新建数据
                String[] arr = pcodes.split(",");
                for(String percode: arr){
                    RolePermission rp = new RolePermission();
                    rp.setRole(role);
                    rp.setPermission(sysService.getPermissionByCode(percode));
                    sysService.insertRolePermission(rp);
                }
            }
            sysService.updateRole(role);
            return ResultObject.newOk("保存成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("保存错误");
        }
    }

    @com.h3w.annotation.Log(action = "删除角色",dataid = "#rcode",optype = Log.OPTYPE_SYS_MANAGE)
    @PerResource(url = "/sys/role/del",name = "角色删除",roles = "sysgly",fun = FunEnum.DELETE)
    @PostMapping("/role/del")
    public ResultObject delRole(String rcode) {
        Role role = sysService.getRoleByCode(rcode);
        if(role==null){
            return ResultObject.newError("未找到数据");
        }
        sysService.deleteRoleByCode(rcode);
        return ResultObject.newOk("删除成功");
    }

    @PerResource(url = "/sys/role/permissions",name = "角色权限列表",roles = "sysgly",fun = FunEnum.SELECT)
    @GetMapping("/role/permissions")
    public String rolePermissions(String rcode) throws JSONException {

        JSONArray array = new JSONArray();
        List<String> idlist = new ArrayList<>();
        List<RolePermission> rolePermissions = sysService.selectByRolecode(rcode);
        for(RolePermission rp: rolePermissions){
            idlist.add(rp.getPermission().getCode());
        }

        List<Permission> permissions = sysService.getPermissionList(Permission.TYPE_1);
        for(Permission p: permissions){
            //obj菜单权限，pers按钮/功能权限
            JSONObject obj = new JSONObject();
            obj.put("code",p.getCode());
            obj.put("name",p.getName());
            obj.put("pname",p.getPname());
            obj.put("icon",p.getIcon());
            List<Permission> childPermissions = sysService.selectPermissionByParentcode(p.getCode());
            obj.put("rows", getResourcePermissions(childPermissions,idlist));
            obj.put("status",!idlist.contains(p.getCode())?0:1);
            array.put(obj);
        }
        return ResultObject.newJSONRows(array).toString();
    }

    public JSONArray getResourcePermissions(List<Permission> permissions, List<String> idlist) throws JSONException {
        JSONArray row = new JSONArray();
        for(Permission p: permissions){
            JSONObject obj = new JSONObject();
            obj.put("code",p.getCode());
            obj.put("name",p.getName());
            obj.put("pname",p.getPname());
            List<Permission> childPermissions = sysService.selectPermissionByParentcode(p.getCode());
            obj.put("rows", getResourcePermissions(childPermissions,idlist));
            obj.put("status",!idlist.contains(p.getCode())?0:1);
            row.put(obj);
        }
        return row;
    }

    @com.h3w.annotation.Log(action = "设置角色权限",dataid = "#rcode")
    @PerResource(url = "/sys/role/addpermissions",name = "设置角色权限",roles = "sysgly",fun = FunEnum.CREATE)
    @NoRepeatSubmit(param = "#rcode")
    @PostMapping("/role/addpermissions")
    public ResultObject addPermissions(String rcode,String pcodes){
        //先清空之前的数据
        List<RolePermission> rolePermissions = sysService.selectByRolecode(rcode);
        for(RolePermission rp: rolePermissions){
            sysService.deleteRolePermission(rp);
        }
        //新建数据
        String[] arr = pcodes.split(",");
        for(String percode: arr){
            RolePermission rp = new RolePermission();
            rp.setRole(sysService.getRoleByCode(rcode));
            rp.setPermission(sysService.getPermissionByCode(percode));
            sysService.insertRolePermission(rp);
        }
        return ResultObject.newOk("授权成功！");
    }

    /**
     * 一级菜单
     * @return
     * @throws IOException
     * @throws JSONException
     */
    @GetMapping("/permission/headnav")
    public String headnav() throws IOException, JSONException {
        List<Permission> resourceList = sysService.getPermissionList(Permission.TYPE_1);
        JSONArray array = new JSONArray();
        for(Permission p: resourceList){
            JSONObject obj = new JSONObject();
            obj.put("code",p.getCode());
            obj.put("url",p.getUrl());
            obj.put("name",p.getName());
            obj.put("parentcode",p.getParentcode());
            obj.put("seq",p.getSeq());
            obj.put("type",p.getType());
            array.put(obj);
        }
        return ResultObject.newJSONRows(array).toString();
    }

    /**
     * 查询所有权限树形
     * @param request
     * @return
     */
    @GetMapping(value = "/permission/list")
    public String permissionsTree(HttpServletRequest request){
        try{
            JSONArray array = new JSONArray();
            List<Permission> list = sysService.getPermissionList(Permission.TYPE_1);
            for(Permission p:list){
                JSONObject obj = new JSONObject();
                obj.put("seq",p.getSeq());
                obj.put("code",p.getCode());
                obj.put("name",p.getName());
                obj.put("pname",p.getPname());
                obj.put("icon",p.getIcon());
                obj.put("url",p.getUrl());
                obj.put("parentcode",p.getParentcode());
                List<Permission> childPermissions = sysService.selectPermissionByParentcode(p.getCode());
                obj.put("rows", getPermissions(childPermissions));
                array.put(obj);
            }
            return ResultObject.newJSONRows(array).toString();
        }catch(Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统异常").toString();
        }
    }

    public JSONArray getPermissions(List<Permission> permissions) throws JSONException {
        JSONArray row = new JSONArray();
        for(Permission p: permissions){
            JSONObject obj = new JSONObject();
            obj.put("seq",p.getSeq());
            obj.put("code",p.getCode());
            obj.put("name",p.getName());
            obj.put("icon",p.getIcon());
            obj.put("url",p.getUrl());
            obj.put("parentcode",p.getParentcode());
            List<Permission> childPermissions = sysService.selectPermissionByParentcode(p.getCode());
            obj.put("rows", getPermissions(childPermissions));
            row.put(obj);
        }
        return row;
    }

    @PerResource(url = "/sys/permission/create",name = "保存菜单",roles = "sysgly",fun = FunEnum.CREATE)
    @PostMapping("/permission/create")
    public ResultObject createPermissson(Permission permission){
        return sysService.savePermission(permission);
    }

    @PerResource(url = "/sys/permission/update",name = "修改菜单",roles = "sysgly",fun = FunEnum.UPDATE)
    @PostMapping("/permission/update")
    public ResultObject updatePermission(@ApiIgnore Permission permission){
        return sysService.savePermission(permission);
    }

    @GetMapping("/permission/get")
    public String getPermission(String code) throws JSONException, IOException {
        Permission permission = sysService.getPermissionByCode(code);
        if(permission==null){
            return ResultObject.newError("未找到权限").toString();
        }
        JSONObject obj = new JSONObject();
        obj.put("code",permission.getCode());
        obj.put("type",permission.getType());
        obj.put("seq",permission.getSeq());
        obj.put("parentcode",permission.getParentcode());
        obj.put("name",permission.getName());
        obj.put("url",permission.getUrl());
        return ResultObject.newJSONData(obj).toString();
    }

    @com.h3w.annotation.Log(action = "删除菜单")
    @PerResource(url = "/sys/permission/del",name = "删除菜单",roles = "sysgly",fun = FunEnum.DELETE)
    @RequestMapping("/permission/del")
    public ResultObject deletePermission(String code){

        try {
            sysService.deletePermissionByCode(code);
            return ResultObject.newOk("已删除！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统内部错误！");
        }
    }


    /**
     * 初始用户数据
     * @return
     */
    @GetMapping("/initUserRole")
    public String initUsers(){
        List<User> items = userService.findAll();
        for(User u: items){
            List<UserRole> userRoles = u.getUserRoles();
            userRoles.clear();
            UserRole ur = new UserRole();
            ur.setRole(sysService.getRoleByCode(Role.CODE_BASE));
            ur.setUser(u);
            userRoles.add(ur);
            userService.updateSelect(u);
        }
        return ResultObject.newOk("数据更新完成").toString();
    }

    /**
     * 日志查询
     * @param page
     * @return
     */
    @PostMapping(value = "logList")
    public String logList(Integer optype, Integer userid,String logtime, String name, Page<com.h3w.entity.Log> page) {
        try{
            JSONObject result = new JSONObject();
            result.put("statusCode", 200);
            result.put("message", "查询成功");
            JSONArray rows = new JSONArray();
            result.put("total", 0);
            page = userService.queryLogs(page,optype,userid,logtime,name);
            if(page!=null){
                result.put("total", page.getTotalCount());
                for(com.h3w.entity.Log log:page.getResults()){
                    JSONObject node = new JSONObject();
                    node.put("logtime", org.apache.commons.httpclient.util.DateUtil.formatDate(log.getLogtime(),"yyyy-MM-dd HH:mm:ss"));
                    node.put("userid", log.getUserid());
                    User u = userService.getById(log.getUserid());
                    node.put("username", u.getRealname());
                    node.put("depname", u.getDept()!=null?u.getDept().getName():"");
                    node.put("action", log.getAction());
                    node.put("content", log.getContent());
                    node.put("typename",Log.optypes.get(log.getOptype()));
                    rows.put(node);
                }
            }
            result.put("rows", rows);
            return result.toString();

        }catch(Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统异常").toString();
        }
    }

    public int saveLog(HttpServletRequest request,Integer op,String action,String content,String dataid,String tbname){
        //保存日志
        User user = loginController.getCurrentUser();
        String[] ips = GetClientIpUtil.getRemoteIpAddr(request).split(",");
        String ip = ips[0];
        com.h3w.entity.Log log = new com.h3w.entity.Log();
        log.setOptype(op);
        log.setAction(action);
        log.setIp(ip);
        log.setLogtime(new Date());
        log.setUserid(user.getId());
        log.setDeptid(user.getDept().getId());
        log.setRealname(user.getRealname());
        log.setDataid(dataid);
        log.setTbname(tbname);
        log.setContent(content);
        userService.saveLog(log);
        return 0;
    }


    public JSONArray getDepts(List<Department> items){
        JSONArray array = new JSONArray();
        for(Department d: items){
            JSONObject obj = new JSONObject();
            //如果是大队，就把id置为空
            obj.put("id",d.getId());
            obj.put("name",d.getName());
            array.put(obj);
        }
        return array;
    }

//    @RequestMapping("/dictionary/root")
//    @ResponseBody
//    public String getDicByRoot() throws JSONException {
//        List<Dictionary> items = dictionaryService.findByParentcode(Dictionary.ROOT);
//        return CommonUtil.getJsonResult(items).toString();
//    }
//
//    @RequestMapping("/dictionary/getparents")
//    @ResponseBody
//    public String getParents(String pcode) throws JSONException {
//        List<Dictionary> items = new ArrayList<>();
//        if(StringUtil.isNotBlank(pcode)){
//            Dictionary pd = dictionaryService.getByCode(pcode);
//            items.add(pd);
//        }else {
//            items = dictionaryService.findByParentcode(Dictionary.ROOT);
//        }
//        return CommonUtil.getJsonResult(items).toString();
//    }
//
//    @RequestMapping("/dictionary/byvalue")
//    @ResponseBody
//    public String getDicByValue(String value) throws JSONException {
//        if(StringUtil.isBlank(value)){
//            return ResultObject.newJsonError("参数错误").toString();
//        }
//        List<Dictionary> dictionary = dictionaryService.getByValue(value);
//        return CommonUtil.getJsonResult(dictionary).toString();
//    }
//
//    @RequestMapping("/dictionary/children")
//    @ResponseBody
//    public String getDicByParentcode(String pcode,Integer pid) throws JSONException {
//        List<Dictionary> items = new ArrayList<>();
//        if(StringUtil.isNotBlank(pcode)){
//            items = dictionaryService.findByParentcode(pcode);
//        }else if(pid != null){
//            Dictionary pd = dictionaryService.getById(pid);
//            items = dictionaryService.findByParentcode(pd.getCode());
//        }
//        for(Dictionary dic: items){
//            List<Dictionary> children = dictionaryService.findByParentcode(dic.getCode());
//            dic.setChildren(children);
//        }
//        return CommonUtil.getJsonResult(items).toString();
//    }
//
//    @RequestMapping("/dictionary/situationlist")
//    @ResponseBody
//    public String getDicsituationlist() throws JSONException {
//        List<Dictionary> items = dictionaryService.findByParentcodeAndStauts(Dictionary.SITUATION_CODE, StaticConstans.STATUS_0);
//        for(Dictionary dic: items){
//            List<Dictionary> children = dictionaryService.findByParentcodeAndStauts(dic.getCode(), StaticConstans.STATUS_0);
//            for (Dictionary dic2: children){
//                List<Dictionary> children2 = dictionaryService.findByParentcodeAndStauts(dic2.getCode(), StaticConstans.STATUS_0);
//                dic2.setChildren(children2);
//            }
//            dic.setChildren(children);
//        }
//        return CommonUtil.getJsonResult(items).toString();
//    }
//
//    @RequestMapping("/dictionary/create")
//    @ResponseBody
//    public ResultObject createDictionary(Dictionary dictionary){
//
//        String code = dictionary.getParentcode();
//        if(StringUtil.isBlank(code)){
////            if(StringUtil.isBlank(dictionary.getValue())){
////                return ResultObject.newError("value值不能为空");
////            }
//            code = StringUtil.getCharAndNumr(6);
//            dictionary.setParentcode(Dictionary.ROOT);
//        }else {
//            code += "-"+ StringUtil.getCharAndNumr(5);
//        }
//        dictionary.setCode(code);
//        dictionary.setDelflag(StaticConstans.STATUS_0);
//        try {
//            dictionaryService.insertSelect(dictionary);
//            return ResultObject.newOk("添加成功！");
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResultObject.newError("保存错误！");
//        }
//    }
//
//    @RequestMapping("/dictionary/get")
//    @ResponseBody
//    public String getDictionary(Integer id) throws JSONException {
//        Dictionary dictionary = dictionaryService.getById(id);
//
//        Dictionary parent = dictionaryService.getByCode(dictionary.getParentcode());
//        dictionary.setParent(parent);
//        return CommonUtil.getJsonResult(dictionary);
//    }
//
//    @RequestMapping("/dictionary/update")
//    @ResponseBody
//    public ResultObject updateDictionary(Dictionary dictionary){
//        Dictionary dic = dictionaryService.getById(dictionary.getId());
//        if(dictionary.getName() != null){
//            dic.setName(dictionary.getName());
//        }
//        if(dictionary.getParentcode() != null){
//            dic.setParentcode(dictionary.getParentcode());
//        }
//        if(dictionary.getValue() != null){
//            dic.setValue(dictionary.getValue());
//        }
//        try {
//            dictionaryService.updateSelect(dic);
//            return ResultObject.newOk("修改成功！");
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResultObject.newError("保存错误！");
//        }
//    }
//
//    @RequestMapping("/dictionary/del")
//    @ResponseBody
//    public ResultObject delDictionary(Integer did){
//        try {
//            dictionaryService.deleteById(did);
//            return ResultObject.newOk("删除成功！");
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResultObject.newError("删除错误！");
//        }
//    }

}
