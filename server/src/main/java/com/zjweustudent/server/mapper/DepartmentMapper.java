package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.Department;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 学院/部门表(Department)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-24 21:26:00
 */

@Mapper
public interface DepartmentMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `department` (departid, departname, type, state, college, ranknum ) " +
            "values (#{departid}, #{departname}, #{type}, #{state}, #{college}, #{ranknum} )")
    void insert(Department department);


    /**
     * 修改数据
     */
    @Update("update `department` set  departid = #{departid} ,  departname = #{departname} ,  type = #{type} ,  state = #{state} ,  college = #{college} ,  ranknum = #{ranknum}   " +
            "where id = #{id}")
    void update(Department department);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `department` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `department` order by departid")
    List<Department> selectAll();

    @Select("select * from `department` where state = 1 order by departid")
    List<Department> selectAllAvailable();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `department` where id = #{id}")
    Department selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `department` " +
            "where departid like concat('%', #{department.departid}, '%') and departname like concat('%', #{department.departname}, '%') and type like concat('%', #{department.type}, '%') and state like concat('%', #{department.state}, '%') and college like concat('%', #{department.college}, '%') " +
            "order by departid ")
    List<Department> select(Integer temp, Department department);

    /**
     * 多条件分页查询
     */
    @Select("select * from `department` " +
            "where departid like concat('%', #{department.departid}, '%') and departname like concat('%', #{department.departname}, '%') and type like concat('%', #{department.type}, '%') and state like concat('%', #{department.state}, '%') and college like concat('%', #{department.college}, '%')   " +
            "order by departid limit #{skipNum}, #{pageSize}")
    List<Department> pageSelect(Integer skipNum, Integer pageSize, Department department);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `department` " +
            "where departid like concat('%', #{department.departid}, '%') and departname like concat('%', #{department.departname}, '%') and type like concat('%', #{department.type}, '%') and state like concat('%', #{department.state}, '%') and college like concat('%', #{department.college}, '%')  ")
    Integer pageSelectCount(Integer temp, Department department);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `department` where departid = #{departid}")
    Department selectByDepartid(String departid);

    /*教学机构*/
    @Select("select * from `department` where college = '1' order by ranknum")
    List<Department> selectCollege();
}
