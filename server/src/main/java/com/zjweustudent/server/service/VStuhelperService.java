package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.VStuhelper;
import com.zjweustudent.server.mapper.VStuhelperMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: (VStuhelper)表服务接口
 * Author: MOUJITX
 * Date: 2023-11-07 19:53:38
 */

@Service
public class VStuhelperService {

    @Autowired
    VStuhelperMapper vStuhelperMapper;

    /**
     * 新增数据
     */
    public void insert(VStuhelper vStuhelper) {
        vStuhelperMapper.insert(vStuhelper);
    }


    /**
     * 修改数据
     */
    public void update(VStuhelper vStuhelper) {
        vStuhelperMapper.update(vStuhelper);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        vStuhelperMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            vStuhelperMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<VStuhelper> selectAll() {
        return vStuhelperMapper.selectAll();
    }

    public String getType(String xh,String xn){
        String type = vStuhelperMapper.getType(xh, xn);;
        if (type == null) type = "0";
        return type;
    }

    /**
     * 通过ID查询单条数据
     */
    public VStuhelper selectById(Integer id) {
        return vStuhelperMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<VStuhelper> select(VStuhelper vStuhelper) {
        return vStuhelperMapper.select(1, vStuhelper);
    }

    /**
     * 多条件分页
     */
    public List<VStuhelper> pageSelect(Integer pageNum, Integer pageSize, VStuhelper vStuhelper) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return vStuhelperMapper.pageSelect(skipNum, pageSize, vStuhelper);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(VStuhelper vStuhelper) {
        return vStuhelperMapper.pageSelectCount(1, vStuhelper);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public VStuhelper selectByStuInfo(String xh, String xn) {
        return vStuhelperMapper.selectByStuInfo(xh,xn);
    }

}
