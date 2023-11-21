package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.Notices;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 通知公告(Notices)表数据库接口
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:32
 */

@Mapper
public interface NoticesMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `notices` (title, top, publish, date, source, author, detail, simple, adduser, edituser ) " +
            "values (#{title}, #{top}, #{publish}, #{date}, #{source}, #{author}, #{detail}, #{simple}, #{adduser}, #{edituser} )")
    void insert(Notices notices);


    /**
     * 修改数据
     */
    @Update("update `notices` set  title = #{title} ,  top = #{top} ,  publish = #{publish} ,  date = #{date} ,  source = #{source} ,  author = #{author} ,  detail = #{detail} ,  simple = #{simple} ,  adduser = #{adduser} ,  edituser = #{edituser}   " +
            "where id = #{id}")
    void update(Notices notices);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `notices` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `notices` order by id desc ")
    List<Notices> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `notices` where id = #{id}")
    Notices selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `notices` " +
            "where title like concat('%', #{notices.title}, '%') and top like concat('%', #{notices.top}, '%') and publish like concat('%', #{notices.publish}, '%') and date like concat('%', #{notices.date}, '%') and source like concat('%', #{notices.source}, '%') and author like concat('%', #{notices.author}, '%') and detail like concat('%', #{notices.detail}, '%') and simple like concat('%', #{notices.simple}, '%') and adduser like concat('%', #{notices.adduser}, '%') and edituser like concat('%', #{notices.edituser}, '%')  order by id desc   ")
    List<Notices> select(Integer temp, Notices notices);

    /**
     * 多条件分页查询
     */
    @Select("select * from `notices` " +
            "where title like concat('%', #{notices.title}, '%') and top like concat('%', #{notices.top}, '%') and publish like concat('%', #{notices.publish}, '%') and date like concat('%', #{notices.date}, '%') and source like concat('%', #{notices.source}, '%') and author like concat('%', #{notices.author}, '%') and detail like concat('%', #{notices.detail}, '%') and simple like concat('%', #{notices.simple}, '%') and adduser like concat('%', #{notices.adduser}, '%') and edituser like concat('%', #{notices.edituser}, '%')  order by id desc   " +
            "limit #{skipNum}, #{pageSize}")
    List<Notices> pageSelect(Integer skipNum, Integer pageSize, Notices notices);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `notices` " +
            "where title like concat('%', #{notices.title}, '%') and top like concat('%', #{notices.top}, '%') and publish like concat('%', #{notices.publish}, '%') and date like concat('%', #{notices.date}, '%') and source like concat('%', #{notices.source}, '%') and author like concat('%', #{notices.author}, '%') and detail like concat('%', #{notices.detail}, '%') and simple like concat('%', #{notices.simple}, '%') and adduser like concat('%', #{notices.adduser}, '%') and edituser like concat('%', #{notices.edituser}, '%')   ")
    Integer pageSelectCount(Integer temp, Notices notices);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */
     /*
    @Select("select * from `notices` where xxx = #{xxx}")
    Notices selectByXXX(String XXX);
    */
}
