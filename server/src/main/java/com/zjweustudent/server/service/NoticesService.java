package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.Notices;
import com.zjweustudent.server.mapper.NoticesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 通知公告(Notices)表服务接口
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:32
 */

@Service
public class NoticesService {

    @Autowired
    NoticesMapper noticesMapper;

    /**
     * 新增数据
     */
    public void insert(Notices notices) {
        noticesMapper.insert(notices);
    }


    /**
     * 修改数据
     */
    public void update(Notices notices) {
        noticesMapper.update(notices);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        noticesMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            noticesMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<Notices> selectAll() {
        return noticesMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public Notices selectById(Integer id) {
        return noticesMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<Notices> select(Notices notices) {
        return noticesMapper.select(1, notices);
    }

    /**
     * 多条件分页
     */
    public List<Notices> pageSelect(Integer pageNum, Integer pageSize, Notices notices) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return noticesMapper.pageSelect(skipNum, pageSize, notices);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(Notices notices) {
        return noticesMapper.pageSelectCount(1, notices);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */
    /*
    public Notices selectByXXX(String XXX) {
        return noticesMapper.selectByXXX(XXX);
    }
    */
}
