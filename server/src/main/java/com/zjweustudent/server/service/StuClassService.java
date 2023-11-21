package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.StuClass;
import com.zjweustudent.server.mapper.StuClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 班级表(StuClass)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-24 22:31:48
 */

@Service
public class StuClassService {

    @Autowired
    StuClassMapper stuClassMapper;

    /**
     * 新增数据
     */
    public void insert(StuClass stuClass) {
        stuClassMapper.insert(stuClass);
    }


    /**
     * 修改数据
     */
    public void update(StuClass stuClass) {
        stuClassMapper.update(stuClass);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        stuClassMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            stuClassMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<StuClass> selectAll() {
        return stuClassMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public StuClass selectById(Integer id) {
        return stuClassMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<StuClass> select(StuClass stuClass) {
        return stuClassMapper.select(1, stuClass);
    }

    /**
     * 多条件分页
     */
    public List<StuClass> pageSelect(Integer pageNum, Integer pageSize, StuClass stuClass) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return stuClassMapper.pageSelect(skipNum, pageSize, stuClass);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(StuClass stuClass) {
        return stuClassMapper.pageSelectCount(1, stuClass);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public StuClass selectByBjh(String bjh) {
        return stuClassMapper.selectByBjh(bjh);
    }

    public List<StuClass> selectByCM(String collegeNum, String majorNum) {
        return stuClassMapper.selectByCM(collegeNum,majorNum);
    }
}
