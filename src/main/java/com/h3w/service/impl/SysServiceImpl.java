package com.h3w.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3w.ResultObject;
import com.h3w.controller.LoginController;
import com.h3w.controller.SysController;
import com.h3w.dao.*;
import com.h3w.entity.*;
import com.h3w.service.SysService;
import com.h3w.utils.BeanUtil;
import com.h3w.utils.NumUtil;
import com.h3w.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class SysServiceImpl implements SysService {

    @Autowired
    private HttpServletRequest request; //自动注入request
    @Autowired
    LoginController loginController;
    @Autowired
    SysController sysController;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    RolePermissionDao rolePermissionDao;
    @Autowired
    PermissionDao permissionDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private DictionaryDao dictionaryDao;
    @Autowired
    ResourceDao resourceDao;

    @Override
    public List<Permission> getPermissionList(Integer type) {
        String hql = "from Permission where 1=1";
        List<Object> param = new ArrayList<>();
        if(type!=null){
            hql+= " and type=?"+(param.size()+1);
            param.add(type);
        }
        return permissionDao.getListByHQL(hql,param.toArray());
//        JSONArray array = new JSONArray();
//        List<Object> list = permissionDao.getListBySQL(hql,type);
//        for(Object o: list){
//            JSONObject obj = new JSONObject();
//            Object[] d = (Object[])o;
//            obj.put("id",d[0]);
//            array.add(obj);
//        }
    }
    @Override
    public void insertDepartment(Department department) {
        departmentDao.save(department);
    }

    @Override
    public Department getDepartmentById(Integer id) {
        return departmentDao.getByHQL("from Department where id=?1",id);
    }

    @Override
    public Department getDepartmentByName(String name) {
        return departmentDao.getByHQL("from Department where name=?1",name);
    }

    @Override
    public void updateDepartment(Department department) {
        departmentDao.saveOrUpdate(department);
    }

    @Override
    public void deleteDepartmentById(Integer id) {
        departmentDao.deleteById(id);
    }

    @Override
    public String getDepartmentNameById(Integer deptid) {
        Object o = departmentDao.getBySQL("select name from sys_department where id=?1", deptid);
        return o != null?o.toString():"";
    }

    @Override
    public List<Department> findAllDep() {
        Integer rootid = Department.ROOT_ID;
        String hql = "from Department where id!=?1 order by seq";
        return departmentDao.getListByHQL(hql,rootid);
    }

    @Override
    public List<Department> getAllDepartmentByParentid(Integer pid) {
        String hql = "from Department where parentid=?1 and code is not null";
        return departmentDao.getListByHQL(hql,pid);
    }

    @Override
    public Permission getPermissionByCode(String code) {
        return permissionDao.getByHQL("from Permission where code=?1",code);
    }

    @Override
    public void insertPermission(Permission permission) {
        permissionDao.save(permission);
    }

    @Override
    public String savePermission(Permission permission) {
        if(StringUtil.isBlank(permission.getCode())){
            String code = getPermissionMaxCode(permission.getParentcode());
            //此父级下没有数据的时候
            if(StringUtil.isBlank(code) &&  StringUtil.isNotBlank(permission.getParentcode())){
                Permission pp = getPermissionByCode(permission.getParentcode());
                code = pp.getCode()+"00";
            }
            code = NumUtil.autoGenericCode(StringUtil.isBlank(code)?"00":code,1);
            permission.setCode(code);
            permissionDao.saveOrUpdate(permission);
            sysController.saveLog(request,1,"新增权限模块","",permission.getCode(),"ass_permission");

        }else {
            Permission op = getPermissionByCode(permission.getCode());
            BeanUtil.beanCopy(permission,op);
            permissionDao.saveOrUpdate(op);
            sysController.saveLog(request,1,"修改权限模块","",permission.getCode(),"ass_permission");
        }
        return ResultObject.newOk("保存成功").toString();
    }

    @Override
    public void deletePermissionById(Integer id) {
        permissionDao.deleteById(id);
    }

    @Override
    public void deletePermissionByCode(String code) {
        permissionDao.queryHql("delete from Permission where code=?1",code);
    }

    @Override
    public List<Permission> getPermissionByResourcecode(String code) {
        return permissionDao.getListByHQL("from Permission where resourcecode=?1 order by fun",code);
    }

    @Override
    public List<Permission> selectPermissionByParentcode(String code) {
        return permissionDao.getListByHQL("from Permission where parentcode=?1",code);
    }

    @Override
    public Role getRoleByCode(String code) {
        return roleDao.getByHQL("from Role where code=?1",code);
    }

    @Override
    public List<Role> findRoleAll() {
        return roleDao.getListByHQL("from Role where delflag!=1");
    }

    @Override
    public void insertRole(Role role) {
        roleDao.save(role);
    }

    @Override
    public void updateRole(Role role) {
        roleDao.saveOrUpdate(role);
    }

    @Override
    public void deleteRoleByCode(String code) {
        roleDao.queryHql("update Role set delflag=1 where code=?1",code);
    }

    @Override
    public List<Role> selectRoleByLevel(Integer level) {
        return roleDao.getListByHQL("from Role where level=?1",level);
    }

    @Override
    public List<Permission> findPermissionListByRoleId(String rcode) {
        List<Permission> permissions = new ArrayList<>();
        List<RolePermission> rolePermissions = rolePermissionDao.getListByHQL("from RolePermission where rolecode=?1",rcode);
        for(RolePermission rp: rolePermissions){
            permissions.add(rp.getPermission());
        }
        return permissions;
    }

    @Override
    public List<Object> findPermissionIDListByRolecode(String rcode) {
        String sql = "SELECT rp.permissioncode FROM pro_role r INNER JOIN pro_role_permission rp ON r.`code`=rp.rolecode AND r.`code`=?1";
        return rolePermissionDao.getListBySQL(sql,rcode);
    }

    @Override
    public List<RolePermission> selectByRolecode(String rolecode) {
        return rolePermissionDao.getListByHQL("from RolePermission where rolecode=?1",rolecode);
    }

    @Override
    public RolePermission getRolePermissionById(Integer id) {
        return rolePermissionDao.getByHQL("from RolePermission where id=?1",id);
    }

    @Override
    public void insertRolePermission(RolePermission rp) {
        rolePermissionDao.save(rp);
    }

    @Override
    public void updateRolePermission(RolePermission rp) {
        rolePermissionDao.saveOrUpdate(rp);
    }

    @Override
    public void deleteRolePermission(RolePermission rp) {
        rolePermissionDao.delete(rp);
    }

    @Override
    public void deleteRolePermissionByRolecode(String rolecode) {
        String hql = "delete from RolePermission where rolecode=?1";
        rolePermissionDao.queryHql(hql,rolecode);
    }

    @Override
    public List<Dictionary> getDictionaryListByType(String type) {
        String hql = "from Dictionary where dictype=?1 and type=?2";
        Dictionary pd = dictionaryDao.getByHQL(hql,type, Dictionary.TYPE_TYPE);
        List<Dictionary> items = new ArrayList<>();
        if(pd!=null){
            hql = "from Dictionary where parentid=?1 and status!=?2 order by seq";
            items = dictionaryDao.getListByHQL(hql,pd.getId(),Dictionary.STATUS_DEL);
        }
        return items;
    }

    @Override
    public void saveResource(Resource resource) {
        resourceDao.saveOrUpdate(resource);
    }

    @Override
    public Resource getResourceByUrl(String url) {
        String hql = "from Resource where url=?1";
        return resourceDao.getByHQL(hql,url);
    }

    @Override
    public List<Resource> findResourceAll() {
        String hql = "from Resource where status=1";
        return resourceDao.getListByHQL(hql);
    }

    public String getPermissionMaxCode(String parentcode){
        String sql = "select MAX(code) from ass_permission where 1=1";
        List<Object> param = new ArrayList<>();
        if(StringUtil.isNotBlank(parentcode)){
            sql+= " and parentcode=?"+(param.size()+1);
            param.add(parentcode);
        }else {
            sql+= " and parentcode is null";
        }
        Object o = permissionDao.getBySQL(sql,param.toArray());
        return o!=null?o.toString():"";
    }
}
