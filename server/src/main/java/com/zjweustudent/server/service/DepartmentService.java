package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.Department;
import com.zjweustudent.server.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 学院/部门表(Department)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-24 21:26:00
 */

@Service
public class DepartmentService {

    @Autowired
    DepartmentMapper departmentMapper;

    /**
     * 新增数据
     */
    public void insert(Department department) {
        departmentMapper.insert(department);
    }


    /**
     * 修改数据
     */
    public void update(Department department) {
        departmentMapper.update(department);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        departmentMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            departmentMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<Department> selectAll() {
        return departmentMapper.selectAll();
    }

    public List<Department> selectAllAvailable() {
        return departmentMapper.selectAllAvailable();
    }

    /**
     * 通过ID查询单条数据
     */
    public Department selectById(Integer id) {
        return departmentMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<Department> select(Department department) {
        return departmentMapper.select(1, department);
    }

    /**
     * 多条件分页
     */
    public List<Department> pageSelect(Integer pageNum, Integer pageSize, Department department) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return departmentMapper.pageSelect(skipNum, pageSize, department);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(Department department) {
        return departmentMapper.pageSelectCount(1, department);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public Department selectByDepartid(String departid) {
        return departmentMapper.selectByDepartid(departid);
    }

    /*教学机构*/
    public List<Department> selectCollege() {
        return departmentMapper.selectCollege();
    }
}
