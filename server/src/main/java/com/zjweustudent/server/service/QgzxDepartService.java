package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.QgzxDepart;
import com.zjweustudent.server.mapper.QgzxDepartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 用工部门信息(QgzxDepart)表服务接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:34
 */

@Service
public class QgzxDepartService {

    @Autowired
    QgzxDepartMapper qgzxDepartMapper;

    /**
     * 新增数据
     */
    public void insert(QgzxDepart qgzxDepart) {
        qgzxDepartMapper.insert(qgzxDepart);
    }


    /**
     * 修改数据
     */
    public void update(QgzxDepart qgzxDepart) {
        qgzxDepartMapper.update(qgzxDepart);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        qgzxDepartMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            qgzxDepartMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<QgzxDepart> selectAll() {
        return qgzxDepartMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public QgzxDepart selectById(Integer id) {
        return qgzxDepartMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<QgzxDepart> select(QgzxDepart qgzxDepart) {
        return qgzxDepartMapper.select(1, qgzxDepart);
    }

    /**
     * 多条件分页
     */
    public List<QgzxDepart> pageSelect(Integer pageNum, Integer pageSize, QgzxDepart qgzxDepart) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return qgzxDepartMapper.pageSelect(skipNum, pageSize, qgzxDepart);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(QgzxDepart qgzxDepart) {
        return qgzxDepartMapper.pageSelectCount(1, qgzxDepart);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public QgzxDepart selectByName(String name) {
        return qgzxDepartMapper.selectByName(name);
    }

}
