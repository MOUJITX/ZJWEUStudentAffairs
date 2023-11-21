package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.StuInfo;
import com.zjweustudent.server.mapper.StuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 学生信息表(StuInfo)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-22 13:17:02
 */

@Service
public class StuInfoService {

    @Autowired
    StuInfoMapper stuInfoMapper;

    /**
     * 新增数据
     */
    public void insert(StuInfo stuInfo) {
        stuInfoMapper.insert(stuInfo);
    }


    /**
     * 修改数据
     */
    public void update(StuInfo stuInfo) {
        stuInfoMapper.update(stuInfo);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        stuInfoMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            stuInfoMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<StuInfo> selectAll() {
        return stuInfoMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public StuInfo selectById(Integer id) {
        return stuInfoMapper.selectById(id);
    }

    /**
     * 通过Number查询单条数据
     */
    public StuInfo selectByNumber(String number) {
        return stuInfoMapper.selectByNumber(number);
    }

    /**
     * 多条件查询
     */
    public List<StuInfo> select(StuInfo stuInfo) {
        return stuInfoMapper.select(1, stuInfo);
    }

    /**
     * 多条件分页
     */
    public List<StuInfo> pageSelect(Integer pageNum, Integer pageSize, StuInfo stuInfo) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return stuInfoMapper.pageSelect(skipNum, pageSize, stuInfo);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(StuInfo stuInfo) {
        return stuInfoMapper.pageSelectCount(1, stuInfo);
    }

    public List<StuInfo> selectByCMC(String collegeNum, String majorNum, String classNum) {
        return stuInfoMapper.selectByCMC(collegeNum,majorNum,classNum);
    }
}
