package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.Semesters;
import com.zjweustudent.server.mapper.SemestersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 学年学期设置(Semesters)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-25 10:32:42
 */

@Service
public class SemestersService {

    @Autowired
    SemestersMapper semestersMapper;

    /**
     * 新增数据
     */
    public void insert(Semesters semesters) {
        semestersMapper.insert(semesters);
    }


    /**
     * 修改数据
     */
    public void update(Semesters semesters) {
        semestersMapper.update(semesters);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        semestersMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            semestersMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<Semesters> selectAll() {
        return semestersMapper.selectAll();
    }

    public List<Semesters> selectAllYear() {
        return semestersMapper.selectAllYear();
    }

    /**
     * 通过ID查询单条数据
     */
    public Semesters selectById(Integer id) {
        return semestersMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<Semesters> select(Semesters semesters) {
        return semestersMapper.select(1, semesters);
    }

    /**
     * 多条件分页
     */
    public List<Semesters> pageSelect(Integer pageNum, Integer pageSize, Semesters semesters) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return semestersMapper.pageSelect(skipNum, pageSize, semesters);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(Semesters semesters) {
        return semestersMapper.pageSelectCount(1, semesters);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */
    /*
    public Semesters selectByXXX(String XXX) {
        return semestersMapper.selectByXXX(XXX);
    }
    */
}
