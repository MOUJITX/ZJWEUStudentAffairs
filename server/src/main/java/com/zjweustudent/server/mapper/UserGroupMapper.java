package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.UserGroup;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 用户组(UserGroup)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:38:25
 */

@Mapper
public interface UserGroupMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `user_group` (name, note, accessmenu ) " +
            "values (#{name}, #{note}, #{accessmenu} )")
    void insert(UserGroup userGroup);


    /**
     * 修改数据
     */
    @Update("update `user_group` set  name = #{name} ,  note = #{note} ,  accessmenu = #{accessmenu}   " +
            "where id = #{id}")
    void update(UserGroup userGroup);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `user_group` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `user_group`")
    List<UserGroup> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `user_group` where id = #{id}")
    UserGroup selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `user_group` " +
            "where name like concat('%', #{userGroup.name}, '%') and note like concat('%', #{userGroup.note}, '%') and accessmenu like concat('%', #{userGroup.accessmenu}, '%')   ")
    List<UserGroup> select(Integer temp, UserGroup userGroup);

    /**
     * 多条件分页查询
     */
    @Select("select * from `user_group` " +
            "where name like concat('%', #{userGroup.name}, '%') and note like concat('%', #{userGroup.note}, '%') and accessmenu like concat('%', #{userGroup.accessmenu}, '%')   " +
            "limit #{skipNum}, #{pageSize}")
    List<UserGroup> pageSelect(Integer skipNum, Integer pageSize, UserGroup userGroup);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `user_group` " +
            "where name like concat('%', #{userGroup.name}, '%') and note like concat('%', #{userGroup.note}, '%') and accessmenu like concat('%', #{userGroup.accessmenu}, '%')   ")
    Integer pageSelectCount(Integer temp, UserGroup userGroup);

    @Select("select * from `user_group` where name = #{name}")
    UserGroup selectByName(String name);
}
