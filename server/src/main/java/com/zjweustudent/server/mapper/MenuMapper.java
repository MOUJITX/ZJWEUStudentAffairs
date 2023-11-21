package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.Menu;
import com.zjweustudent.server.entity.Users;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 目录(Menu)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:22:48
 */

@Mapper
public interface MenuMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `menu` (title, path, icon, father, note,ranknum, `show` ) " +
            "values (#{title}, #{path}, #{icon}, #{father}, #{note}, #{ranknum}, #{show} )")
    void insert(Menu menu);


    /**
     * 修改数据
     */
    @Update("update `menu` set  title = #{title} ,  path = #{path} ,  icon = #{icon} ,  father = #{father} ,  note = #{note} ,  ranknum = #{ranknum}, `show` = #{show}  " +
            "where id = #{id}")
    void update(Menu menu);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `menu` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `menu` order by ranknum")
    List<Menu> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `menu` where id = #{id}")
    Menu selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `menu` " +
            "where title like concat('%', #{menu.title}, '%') and path like concat('%', #{menu.path}, '%') and icon like concat('%', #{menu.icon}, '%') and father = #{menu.father} and note like concat('%', #{menu.note}, '%') and `show` like concat('%', #{menu.show}, '%')" +
            "order by ranknum,father ")
    List<Menu> select(Integer temp, Menu menu);

    /**
     * 多条件分页查询
     */
    @Select("select * from `menu` " +
            "where title like concat('%', #{menu.title}, '%') and path like concat('%', #{menu.path}, '%') and icon like concat('%', #{menu.icon}, '%') and father = #{menu.father} and note like concat('%', #{menu.note}, '%') and `show` like concat('%', #{menu.show}, '%')  " +
            "order by ranknum,father limit #{skipNum}, #{pageSize}")
    List<Menu> pageSelect(Integer skipNum, Integer pageSize, Menu menu);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `menu` " +
            "where title like concat('%', #{menu.title}, '%') and path like concat('%', #{menu.path}, '%') and icon like concat('%', #{menu.icon}, '%') and father = #{menu.father} and note like concat('%', #{menu.note}, '%') and `show` like concat('%', #{menu.show}, '%') ")
    Integer pageSelectCount(Integer temp, Menu menu);

    @Select("select * from `menu` where path = #{path}")
    Menu selectByPath(String path);

    @Select("select * from `menu` where father = #{id} order by ranknum")
    List<Menu> selectByFather(String id);

    @Select("SELECT * FROM menu WHERE (FIND_IN_SET(id, #{id}) > 0) order by ranknum")
    List<Menu> selectByIds(String ids);
}
