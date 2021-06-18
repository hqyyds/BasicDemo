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
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
@Controller
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
    @ResponseBody
    @RequestMapping("/user/list")
    public String getUserlist(@ParamCheck(length = 2) Integer deptid, HttpServletRequest request,String name) throws JSONException {

        String path = request.getContextPath();
        String urlpath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        List<User> items = userService.selectByDeptidAndStr(deptid,null);
        JSONArray array = new JSONArray();
        for(User user : items){
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
            obj.put("statusstr",user.getStatus()==User.STATUS_ENABLE?"启用":(user.getStatus()==User.STATUS_DISABLE?"禁用":"删除"));
            obj.put("depid",user.getDept()!=null?user.getDept().getId():null);
            array.put(obj);
        }
        return ResultObject.newJSONRows(array).toString();
    }

    /**
     * 查询部门用户树
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/tree", method = RequestMethod.GET)
    public String depUserData(HttpServletRequest request){
        List<Department> list = sysService.findAllDep();
        JSONObject result = new JSONObject();
        result.put("statusCode", 200);
        result.put("message", "查询成功");
        JSONArray data = new JSONArray();
        for(Department dep:list){
            JSONObject node = new JSONObject();
            node.put("deptid", dep.getId());
            node.put("deptname", dep.getName());
            JSONArray users = new JSONArray();
            List<User> userList = userService.findUserByDep(dep.getId());
            for(User u:userList){
                JSONObject user = new JSONObject();
                user.put("userid", u.getId());
                user.put("realname", u.getRealname());
                users.put(user);
            }
            node.put("users", users);
            data.put(node);
        }
        result.put("data", data);
        return result.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/user/create",method = RequestMethod.POST)
    public ResultObject create(User user,String[] rcodes){


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
            if(rcodes.length>0){
                for(int i=0;i<rcodes.length;i++){
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(sysService.getRoleByCode(rcodes[i]));
                    userRoles.add(userRole);
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

    @ResponseBody
    @RequestMapping("/user/get")
    public String getUser(Integer id) throws IOException, JSONException {

        User user = userService.getById(id);
        List<UserRole> userRoles = user.getUserRoles();
        String roles = "";
        String rcodes = "";
        for(UserRole ur: userRoles){
            Role role = ur.getRole();
            roles += (role!=null?role.getName():"") +" ";
            rcodes = (role!=null?role.getCode():"");
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
        obj.put("statusstr",user.getStatus()==User.STATUS_ENABLE?"启用":(user.getStatus()==User.STATUS_DISABLE?"禁用":"删除"));
        obj.put("depid",user.getDept()!=null?user.getDept().getId():null);
        obj.put("roles",roles);
        obj.put("rcodes",rcodes);

        return ResultObject.newJSONData(obj).toString();
    }

    @ResponseBody
    @RequestMapping(value = "/user/edit",method = RequestMethod.POST)
    public ResultObject edit(User user,String[] rcodes){

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

            if(rcodes.length>0){
                for(int i=0;i<rcodes.length;i++){
                    UserRole userRole = new UserRole();
                    userRole.setUser(nuser);
                    userRole.setRole(sysService.getRoleByCode(rcodes[i]));
                    userRoles.add(userRole);
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
    @ResponseBody
    @RequestMapping(value = "/user/updateHead",method = RequestMethod.POST)
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

    @ResponseBody
    @RequestMapping(value = "/user/delmany",method = RequestMethod.POST)
    public ResultObject deleteMany(Integer[] ids){
        try{
            for(int i=0; i<ids.length; i++){
                userService.changeStatusById(User.STATUS_DEL_ED,ids[i]);
            }
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
    @ResponseBody
    @RequestMapping(value = "/user/resetpsd",method = RequestMethod.POST)
    public ResultObject resetPasswordn(Integer[] ids){
        try{
            for(int i=0; i<ids.length; i++){

                User user = userService.getById(ids[i]);
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
    @RequestMapping("/department/list")
    @ResponseBody
    public String getDepartmentList() throws JSONException {
        //科室显示顺序排序
        try {
            List<Department> items = sysService.getAllDepartmentByParentid(Department.ROOT_ID);
//            for(Department item: items){
//                if(item.getName().equals("大队")){
//                    item.setSort(1);
//                }else if(item.getName().equals("计划室")) {
//                    item.setSort(2);
//                }else {
//                    item.setSort(9);
//                }
//            }
//            Collections.sort(items, new Comparator<Department>() {
//                public int compare(Department arg1, Department arg0) {
//                    return arg1.getSort().compareTo(arg0.getSort());
//                }
//            });
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

    @RequestMapping("/department/listTree")
    @ResponseBody
    public String getDepartmentList(Integer deptid) throws JSONException {

        List<Department> deptList = sysService.getAllDepartmentByParentid(Department.ROOT_ID);
        JSONArray array = new JSONArray();
        array = getDeptArray(deptList);
        return array.toString();
    }

    public JSONArray getDeptArray(List<Department> deptList) throws JSONException {
        JSONArray array = new JSONArray();
        for (Department dep : deptList) {
            JSONObject o = new JSONObject();
            o.put("id", dep.getId());
            o.put("name", dep.getName());
            o.put("parentid", dep.getParentid());
            o.put("remark",dep.getRemark());

            List<Department> childList = sysService.getAllDepartmentByParentid(dep.getId());
            o.put("orgs",getDeptArray(childList));
            array.put(o);
        }
        return array;
    }

    @RequestMapping(value = "/department/create",method = RequestMethod.POST)
    @ResponseBody
    public ResultObject departmentCreate(Department department){
        if(StringUtil.isBlank(department.getName())){
            return ResultObject.newError("参数错误");
        }
        try {
            department.setParentid(Department.ROOT_ID);
            sysService.insertDepartment(department);
            return ResultObject.newOk("新增成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统错误");
        }
    }

    @RequestMapping(value = "/department/update",method = RequestMethod.POST)
    @ResponseBody
    public ResultObject departmentUpdate(Department department){
        if(StringUtil.isBlank(department.getName())){
            return ResultObject.newError("参数错误");
        }
        Department odepartment = sysService.getDepartmentById(department.getId());
        if(odepartment== null){
            return ResultObject.newError("未找到组织");
        }
        try {
            odepartment.setName(department.getName());
            odepartment.setRemark(department.getRemark());
            sysService.updateDepartment(odepartment);
            return ResultObject.newOk("修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统错误");
        }
    }

    @RequestMapping("/department/del")
    @ResponseBody
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

    @RequestMapping("/department/get")
    @ResponseBody
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
    @ResponseBody
    @RequestMapping("/role/list")
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
    @ResponseBody
    @RequestMapping("/role/levels")
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

    @ResponseBody
    @RequestMapping("/role/get")
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
     * @param pids
     * @return
     */
    @ResponseBody
    @RequestMapping("/role/create")
    public ResultObject createRole(Role role, String[] pids){
        if(StringUtil.isBlank(role.getCode())){
            role.setCode(StringUtil.getCharAndNumr(6));
        }
        Role r = sysService.getRoleByCode(role.getCode());
        if(r!=null){
            return ResultObject.newError("角色代码已存在");
        }
        if(pids==null){
            return ResultObject.newError("未选择权限");
        }
        try {
            //新建数据
            for(String pid: pids){
                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(sysService.getPermissionByCode(pid));
                sysService.insertRolePermission(rp);
            }
            sysService.updateRole(role);
            return ResultObject.newOk("保存成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("保存错误");
        }
    }
    /**
     * 角色修改
     * @param or
     * @param pids
     * @return
     */
    @ResponseBody
    @RequestMapping("/role/update")
    public ResultObject updateRole(Role or, String[] pids){

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
        if(pids==null){
            return ResultObject.newError("未选择权限");
        }
        try {
            //先清空之前的数据
            sysService.deleteRolePermissionByRolecode(role.getCode());
            //新建数据
            for(String pid: pids){
                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(sysService.getPermissionByCode(pid));
                sysService.insertRolePermission(rp);
            }
            sysService.updateRole(role);
            return ResultObject.newOk("保存成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultObject.newError("保存错误");
        }
    }

    @ResponseBody
    @RequestMapping("/role/del")
    public ResultObject delRole(String rcode) {
        Role role = sysService.getRoleByCode(rcode);
        if(role==null){
            return ResultObject.newError("未找到数据");
        }
        sysService.deleteRoleByCode(rcode);
        return ResultObject.newOk("删除成功");
    }

    @PerResource(url = "/sys/role/permissions",name = "角色列表",roles = "sysgly",fun = FunEnum.SELECT)
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
    @ResponseBody
    @RequestMapping("/permission/headnav")
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
    @ResponseBody
    @RequestMapping(value = "/permission/tree", method = RequestMethod.GET)
    public String permissionsTree(HttpServletRequest request){
        try{
            JSONArray array = new JSONArray();
            List<Permission> list = sysService.getPermissionList(Permission.TYPE_1);
            for(Permission p:list){
                JSONArray rows = new JSONArray();
                JSONObject node = new JSONObject();
                String parentcode = p.getCode();
                node.put("code",p.getCode());
                node.put("name",p.getName());
                node.put("url", p.getUrl()==null?"":p.getUrl());
                node.put("parentcode", p.getParentcode()==null?"":p.getParentcode());
                node.put("seq", p.getSeq()==null?"":p.getSeq());
                node.put("type", p.getType()==null?"":p.getType());

                if(StringUtil.isNotBlank(parentcode)){
                    List<Permission> children = sysService.selectPermissionByParentcode(parentcode);
                    for(Permission p2:children){
                        JSONObject node2 = new JSONObject();
                        node2.put("code",p2.getCode());
                        node2.put("name",p2.getName());
                        node2.put("url", p2.getUrl()==null?"":p2.getUrl());
                        node2.put("parentcode", p2.getParentcode()==null?"":p2.getParentcode());
                        node2.put("seq", p2.getSeq()==null?"":p2.getSeq());
                        node2.put("type", p2.getType()==null?"":p2.getType());
                        rows.put(node2);
                    }
                }

                node.put("rows",rows);
                array.put(node);
            }
            return ResultObject.newJSONRows(array).toString();
        }catch(Exception e){
            e.printStackTrace();
            return ResultObject.newError("系统异常").toString();
        }
    }

    /**
     * 添加修改权限
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/savePermission",method = {RequestMethod.GET,RequestMethod.POST})
    public String savePermission(HttpServletRequest request,Permission per){
        return sysService.savePermission(per);
    }


    @RequestMapping("/permission/get")
    @ResponseBody
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

    @ResponseBody
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
     * 更新用户数据
     * @return
     */
    @ResponseBody
    @RequestMapping("/initUserRole")
    public String initUsers(){
        List<User> items = userService.findAll();
        for(User u: items){
            List<UserRole> userRoles = u.getUserRoles();
            userRoles.clear();
            UserRole ur = new UserRole();
            ur.setRole(sysService.getRoleByCode("member"));
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
    @ResponseBody
    @RequestMapping(value = "logList",method = RequestMethod.POST)
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
