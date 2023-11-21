package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.UserGroup;
import com.zjweustudent.server.mapper.UserGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 用户组(UserGroup)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:38:25
 */

@Service
public class UserGroupService {

    @Autowired
    UserGroupMapper userGroupMapper;

    /**
     * 新增数据
     */
    public void insert(UserGroup userGroup) {
        userGroupMapper.insert(userGroup);
    }


    /**
     * 修改数据
     */
    public void update(UserGroup userGroup) {
        userGroupMapper.update(userGroup);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        userGroupMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            userGroupMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<UserGroup> selectAll() {
        return userGroupMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public UserGroup selectById(Integer id) {
        return userGroupMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<UserGroup> select(UserGroup userGroup) {
        return userGroupMapper.select(1, userGroup);
    }

    /**
     * 多条件分页
     */
    public List<UserGroup> pageSelect(Integer pageNum, Integer pageSize, UserGroup userGroup) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return userGroupMapper.pageSelect(skipNum, pageSize, userGroup);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(UserGroup userGroup) {
        return userGroupMapper.pageSelectCount(1, userGroup);
    }

    public UserGroup selectByName(String name) {
        return userGroupMapper.selectByName(name);
    }
}
