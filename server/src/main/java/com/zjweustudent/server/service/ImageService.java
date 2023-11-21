package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.Image;
import com.zjweustudent.server.mapper.ImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function: 轮播图(Image)表服务接口
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:11
 */

@Service
public class ImageService {

    @Autowired
    ImageMapper imageMapper;

    /**
     * 新增数据
     */
    public void insert(Image image) {
        imageMapper.insert(image);
    }


    /**
     * 修改数据
     */
    public void update(Image image) {
        imageMapper.update(image);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        imageMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            imageMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<Image> selectAll() {
        return imageMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public Image selectById(Integer id) {
        return imageMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<Image> select(Image image) {
        return imageMapper.select(1, image);
    }

    /**
     * 多条件分页
     */
    public List<Image> pageSelect(Integer pageNum, Integer pageSize, Image image) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return imageMapper.pageSelect(skipNum, pageSize, image);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(Image image) {
        return imageMapper.pageSelectCount(1, image);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */
    /*
    public Image selectByXXX(String XXX) {
        return imageMapper.selectByXXX(XXX);
    }
    */
}
