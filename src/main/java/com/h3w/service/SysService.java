package com.h3w.service;

import com.h3w.entity.*;

import java.util.List;

public interface SysService {

    void insertDepartment(Department department);

    Department getDepartmentById(Integer id);

    Department getDepartmentByName(String name);

    void updateDepartment(Department department);

    void deleteDepartmentById(Integer id);

    String getDepartmentNameById(Integer deptid);

    List<Department> findAllDep();

    List<Department> getAllDepartmentByParentid(Integer pid);

    Permission getPermissionByCode(String code);

    void insertPermission(Permission permission);

    String savePermission(Permission permission);

    void deletePermissionById(Integer id);

    void deletePermissionByCode(String code);

    List<Permission> getPermissionByResourcecode(String code);

    List<Permission> selectPermissionByParentcode(String code);

    List<Permission> getPermissionList(Integer type);

    Role getRoleByCode(String code);

    List<Role> findRoleAll();

    void insertRole(Role role);

    void updateRole(Role role);

    void deleteRoleByCode(String code);

    List<Role> selectRoleByLevel(Integer level);

    List<Permission> findPermissionListByRoleId(String rcode);

    List<Object> findPermissionIDListByRolecode(String rcode);

    RolePermission getRolePermissionById(Integer id);

    void insertRolePermission(RolePermission rp);

    void updateRolePermission(RolePermission rp);

    void deleteRolePermissionById(Integer id);

    void deleteRolePermissionByRolecode(String rolecode);

    List<Dictionary> getDictionaryListByType(String type);
}
