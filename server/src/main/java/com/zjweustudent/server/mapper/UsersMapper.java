package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.Users;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 用户信息(Users)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:46:04
 */

@Mapper
public interface UsersMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `users` (nickname, username, password, note, usergroup ) " +
            "values (#{nickname}, #{username}, #{password}, #{note}, #{usergroup} )")
    void insert(Users users);


    /**
     * 修改数据
     */
    @Update("update `users` set  nickname = #{nickname} ,  username = #{username} ,  password = #{password} ,  note = #{note} ,  usergroup = #{usergroup}   " +
            "where id = #{id}")
    void update(Users users);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `users` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `users`")
    List<Users> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `users` where id = #{id}")
    Users selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `users` " +
            "where nickname like concat('%', #{users.nickname}, '%') and username like concat('%', #{users.username}, '%') and password like concat('%', #{users.password}, '%') and note like concat('%', #{users.note}, '%') and usergroup like concat('%', #{users.usergroup}, '%')   ")
    List<Users> select(Integer temp, Users users);

    /**
     * 多条件分页查询
     */
    @Select("select * from `users` " +
            "where nickname like concat('%', #{users.nickname}, '%') and username like concat('%', #{users.username}, '%') and password like concat('%', #{users.password}, '%') and note like concat('%', #{users.note}, '%') and usergroup like concat('%', #{users.usergroup}, '%')   " +
            "limit #{skipNum}, #{pageSize}")
    List<Users> pageSelect(Integer skipNum, Integer pageSize, Users users);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `users` " +
            "where nickname like concat('%', #{users.nickname}, '%') and username like concat('%', #{users.username}, '%') and password like concat('%', #{users.password}, '%') and note like concat('%', #{users.note}, '%') and usergroup like concat('%', #{users.usergroup}, '%')   ")
    Integer pageSelectCount(Integer temp, Users users);

    /**
     * 通过用户名查询单条数据
     */
    @Select("select * from `users` where username = #{username}")
    Users selectByUsername(String username);

    @Select("select username from `users` where usergroup = 13")
    List<Users> selectDepartName();
}
