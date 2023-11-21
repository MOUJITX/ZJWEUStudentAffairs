package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.Users;
import com.zjweustudent.server.exception.ServiceException;
import com.zjweustudent.server.mapper.UsersMapper;
import com.zjweustudent.server.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 用户信息(Users)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:46:04
 */

@Service
public class UsersService {

    @Autowired
    UsersMapper usersMapper;

    /**
     * 新增数据
     */
    public void insert(Users users) {
        usersMapper.insert(users);
    }


    /**
     * 修改数据
     */
    public void update(Users users) {
        usersMapper.update(users);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        usersMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            usersMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<Users> selectAll() {
        return usersMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public Users selectById(Integer id) {
        return usersMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<Users> select(Users users) {
        return usersMapper.select(1, users);
    }

    /**
     * 多条件分页
     */
    public List<Users> pageSelect(Integer pageNum, Integer pageSize, Users users) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return usersMapper.pageSelect(skipNum, pageSize, users);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(Users users) {
        return usersMapper.pageSelectCount(1, users);
    }


    /**
     * 验证帐号密码
     */
    public Users login(Users users) {
        Users dUser = usersMapper.selectByUsername(users.getUsername());
        if (dUser == null){
            throw new ServiceException("账号不存在");
        }
        if (!users.getPassword().equals(dUser.getPassword())){
            throw new ServiceException("密码错误");
        }
        //生成token
        String token = TokenUtils.genToken(String.valueOf(dUser.getId()),users.getPassword());
        dUser.setToken(token);
        return dUser;
    }

    public Users selectByUsername(String username) {
        return usersMapper.selectByUsername(username);
    }

    public List<Users> selectDepartName() {
        return usersMapper.selectDepartName();
    }
}
