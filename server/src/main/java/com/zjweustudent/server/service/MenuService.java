package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.Menu;
import com.zjweustudent.server.entity.Users;
import com.zjweustudent.server.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 目录(Menu)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:19:04
 */

@Service
public class MenuService {

    @Autowired
    MenuMapper menuMapper;

    /**
     * 新增数据
     */
    public void insert(Menu menu) {
        menuMapper.insert(menu);
    }


    /**
     * 修改数据
     */
    public void update(Menu menu) {
        menuMapper.update(menu);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        menuMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            menuMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<Menu> selectAll() {
        return menuMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public Menu selectById(Integer id) {
        return menuMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<Menu> select(Menu menu) {
        return menuMapper.select(1, menu);
    }

    /**
     * 多条件分页
     */
    public List<Menu> pageSelect(Integer pageNum, Integer pageSize, Menu menu) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return menuMapper.pageSelect(skipNum, pageSize, menu);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(Menu menu) {
        return menuMapper.pageSelectCount(1, menu);
    }

    public Menu selectByPath(String path) {
        return menuMapper.selectByPath(path);
    }

    public List<Menu> selectByFather(String id) {
        return menuMapper.selectByFather(id);
    }

    /**
     * 根据ids（如：1,2,3,4）返回查询值
     */
    public List<Menu> selectByIds(String ids) {
        return menuMapper.selectByIds(ids);
    }
}
