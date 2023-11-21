package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.Image;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 轮播图(Image)表数据库接口
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:12
 */

@Mapper
public interface ImageMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `image` (title, url, img ) " +
            "values (#{title}, #{url}, #{img})")
    void insert(Image image);


    /**
     * 修改数据
     */
    @Update("update `image` set  title = #{title} ,  url = #{url} ,  img = #{img}  " +
            "where id = #{id}")
    void update(Image image);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `image` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `image`")
    List<Image> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `image` where id = #{id}")
    Image selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `image` " +
            "where title like concat('%', #{image.title}, '%') and url like concat('%', #{image.url}, '%') and img like concat('%', #{image.img}, '%')")
    List<Image> select(Integer temp, Image image);

    /**
     * 多条件分页查询
     */
    @Select("select * from `image` " +
            "where title like concat('%', #{image.title}, '%') and url like concat('%', #{image.url}, '%') and img like concat('%', #{image.img}, '%')" +
            "limit #{skipNum}, #{pageSize}")
    List<Image> pageSelect(Integer skipNum, Integer pageSize, Image image);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `image` " +
            "where title like concat('%', #{image.title}, '%') and url like concat('%', #{image.url}, '%') and img like concat('%', #{image.img}, '%') ")
    Integer pageSelectCount(Integer temp, Image image);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */
     /*
    @Select("select * from `image` where xxx = #{xxx}")
    Image selectByXXX(String XXX);
    */
}
