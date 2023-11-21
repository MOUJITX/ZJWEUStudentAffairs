package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.StuMajor;
import com.zjweustudent.server.mapper.StuMajorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 专业表(StuMajor)表服务接口
 * Author: MOUJITX
 * Date: 2023-09-24 21:50:37
 */

@Service
public class StuMajorService {

    @Autowired
    StuMajorMapper stuMajorMapper;

    /**
     * 新增数据
     */
    public void insert(StuMajor stuMajor) {
        stuMajorMapper.insert(stuMajor);
    }


    /**
     * 修改数据
     */
    public void update(StuMajor stuMajor) {
        stuMajorMapper.update(stuMajor);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        stuMajorMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            stuMajorMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<StuMajor> selectAll() {
        return stuMajorMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public StuMajor selectById(Integer id) {
        return stuMajorMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<StuMajor> select(StuMajor stuMajor) {
        return stuMajorMapper.select(1, stuMajor);
    }

    /**
     * 多条件分页
     */
    public List<StuMajor> pageSelect(Integer pageNum, Integer pageSize, StuMajor stuMajor) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return stuMajorMapper.pageSelect(skipNum, pageSize, stuMajor);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(StuMajor stuMajor) {
        return stuMajorMapper.pageSelectCount(1, stuMajor);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public StuMajor selectByZyh(String zyh) {
        return stuMajorMapper.selectByZyh(zyh);
    }

    public List<StuMajor> selectByCollege(Integer id) {
        return stuMajorMapper.selectByCollege(id);
    }
}
