package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.TchInfo;
import com.zjweustudent.server.mapper.TchInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 教师信息表(TchInfo)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-25 09:18:01
 */

@Service
public class TchInfoService {

    @Autowired
    TchInfoMapper tchInfoMapper;

    /**
     * 新增数据
     */
    public void insert(TchInfo tchInfo) {
        tchInfoMapper.insert(tchInfo);
    }


    /**
     * 修改数据
     */
    public void update(TchInfo tchInfo) {
        tchInfoMapper.update(tchInfo);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        tchInfoMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            tchInfoMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<TchInfo> selectAll() {
        return tchInfoMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public TchInfo selectById(Integer id) {
        return tchInfoMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<TchInfo> select(TchInfo tchInfo) {
        return tchInfoMapper.select(1, tchInfo);
    }

    /**
     * 多条件分页
     */
    public List<TchInfo> pageSelect(Integer pageNum, Integer pageSize, TchInfo tchInfo) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return tchInfoMapper.pageSelect(skipNum, pageSize, tchInfo);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(TchInfo tchInfo) {
        return tchInfoMapper.pageSelectCount(1, tchInfo);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public TchInfo selectByNumber(String number) {
        return tchInfoMapper.selectByNumber(number);
    }

}
