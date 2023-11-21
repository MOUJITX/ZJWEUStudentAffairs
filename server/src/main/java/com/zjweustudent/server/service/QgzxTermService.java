package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.QgzxTerm;
import com.zjweustudent.server.mapper.QgzxTermMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 勤工助学批次(QgzxTerm)表服务接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:35
 */

@Service
public class QgzxTermService {

    @Autowired
    QgzxTermMapper qgzxTermMapper;

    /**
     * 新增数据
     */
    public void insert(QgzxTerm qgzxTerm) {
        qgzxTermMapper.insert(qgzxTerm);
    }


    /**
     * 修改数据
     */
    public void update(QgzxTerm qgzxTerm) {
        qgzxTermMapper.update(qgzxTerm);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        qgzxTermMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            qgzxTermMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<QgzxTerm> selectAll() {
        return qgzxTermMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public QgzxTerm selectById(Integer id) {
        return qgzxTermMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<QgzxTerm> select(QgzxTerm qgzxTerm) {
        return qgzxTermMapper.select(1, qgzxTerm);
    }

    /**
     * 多条件分页
     */
    public List<QgzxTerm> pageSelect(Integer pageNum, Integer pageSize, QgzxTerm qgzxTerm) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return qgzxTermMapper.pageSelect(skipNum, pageSize, qgzxTerm);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(QgzxTerm qgzxTerm) {
        return qgzxTermMapper.pageSelectCount(1, qgzxTerm);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public QgzxTerm selectByPcdm(String pcdm) {
        return qgzxTermMapper.selectByPcdm(pcdm);
    }

}
