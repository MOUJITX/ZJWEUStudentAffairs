package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.Semesters;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 学年学期设置(Semesters)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-25 10:32:42
 */

@Mapper
public interface SemestersMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `semesters` (starttime, endtime, schoolyear, semester ) " +
            "values (#{starttime}, #{endtime}, #{schoolyear}, #{semester} )")
    void insert(Semesters semesters);


    /**
     * 修改数据
     */
    @Update("update `semesters` set  starttime = #{starttime} ,  endtime = #{endtime} ,  schoolyear = #{schoolyear} ,  semester = #{semester}   " +
            "where id = #{id}")
    void update(Semesters semesters);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `semesters` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `semesters` order by id desc")
    List<Semesters> selectAll();

    /*全部学年*/
    @Select("select DISTINCT schoolyear from `semesters` order by `schoolyear` DESC ")
    List<Semesters> selectAllYear();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `semesters` where id = #{id}")
    Semesters selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `semesters` " +
            "where schoolyear like concat('%', #{semesters.schoolyear}, '%') and semester like concat('%', #{semesters.semester}, '%') " +
            "order by id desc  ")
    List<Semesters> select(Integer temp, Semesters semesters);

    /**
     * 多条件分页查询
     */
    @Select("select * from `semesters` " +
            "where schoolyear like concat('%', #{semesters.schoolyear}, '%') and semester like concat('%', #{semesters.semester}, '%')   " +
            "order by id desc " +
            "limit #{skipNum}, #{pageSize}")
    List<Semesters> pageSelect(Integer skipNum, Integer pageSize, Semesters semesters);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `semesters` " +
            "where schoolyear like concat('%', #{semesters.schoolyear}, '%') and semester like concat('%', #{semesters.semester}, '%')   ")
    Integer pageSelectCount(Integer temp, Semesters semesters);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */
     /*
    @Select("select * from `semesters` where xxx = #{xxx}")
    Semesters selectByXXX(String XXX);
    */
}
