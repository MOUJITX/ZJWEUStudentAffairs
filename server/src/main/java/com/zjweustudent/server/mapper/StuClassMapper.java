package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.StuClass;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 班级表(StuClass)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-24 22:31:48
 */

@Mapper
public interface StuClassMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `stu_class` (xyh, zyh, nj, bjh, bm, `by` ) " +
            "values (#{xyh},  #{zyh}, #{nj}, #{bjh}, #{bm},  #{by} )")
    void insert(StuClass stuClass);


    /**
     * 修改数据
     */
    @Update("update `stu_class` set  xyh = #{xyh} ,  zyh = #{zyh} ,  nj = #{nj} ,  bjh = #{bjh} ,  bm = #{bm} ,  `by` = #{by}   " +
            "where id = #{id}")
    void update(StuClass stuClass);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `stu_class` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `stu_class`")
    List<StuClass> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `stu_class` where id = #{id}")
    StuClass selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select stu_class.id, stu_class.xyh, departname as xym, stu_class.zyh, nj, bjh, bm, zym, `by` " +
            "from `stu_class`,`stu_major`,`department` " +
            "where stu_class.xyh = department.departid and stu_class.zyh = stu_major.zyh " +
            "and stu_class.xyh like concat('%', #{stuClass.xyh}, '%') " +
            "and departname like concat('%', #{stuClass.xym}, '%') " +
            "and stu_class.zyh like concat('%', #{stuClass.zyh}, '%') " +
            "and nj like concat('%', #{stuClass.nj}, '%') " +
            "and bjh like concat('%', #{stuClass.bjh}, '%') " +
            "and bm like concat('%', #{stuClass.bm}, '%') " +
            "and zym like concat('%', #{stuClass.zym}, '%') " +
            "and `by` like concat('%', #{stuClass.by}, '%')   ")
    List<StuClass> select(Integer temp, StuClass stuClass);

    /**
     * 多条件分页查询
     */
    @Select("select stu_class.id, stu_class.xyh, departname as xym, stu_class.zyh, nj, bjh, bm, zym, `by` " +
            "from `stu_class`,`stu_major`,`department` " +
            "where stu_class.xyh = department.departid and stu_class.zyh = stu_major.zyh " +
            "and stu_class.xyh like concat('%', #{stuClass.xyh}, '%') " +
            "and departname like concat('%', #{stuClass.xym}, '%') " +
            "and stu_class.zyh like concat('%', #{stuClass.zyh}, '%') " +
            "and nj like concat('%', #{stuClass.nj}, '%') " +
            "and bjh like concat('%', #{stuClass.bjh}, '%') " +
            "and bm like concat('%', #{stuClass.bm}, '%') " +
            "and zym like concat('%', #{stuClass.zym}, '%') " +
            "and `by` like concat('%', #{stuClass.by}, '%')   " +
            "limit #{skipNum}, #{pageSize}")
    List<StuClass> pageSelect(Integer skipNum, Integer pageSize, StuClass stuClass);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(stu_class.id) " +
            "from `stu_class`,`stu_major`,`department` " +
            "where stu_class.xyh = department.departid and stu_class.zyh = stu_major.zyh " +
            "and stu_class.xyh like concat('%', #{stuClass.xyh}, '%') " +
            "and departname like concat('%', #{stuClass.xym}, '%') " +
            "and stu_class.zyh like concat('%', #{stuClass.zyh}, '%') " +
            "and nj like concat('%', #{stuClass.nj}, '%') " +
            "and bjh like concat('%', #{stuClass.bjh}, '%') " +
            "and bm like concat('%', #{stuClass.bm}, '%') " +
            "and zym like concat('%', #{stuClass.zym}, '%') " +
            "and `by` like concat('%', #{stuClass.by}, '%')")
    Integer pageSelectCount(Integer temp, StuClass stuClass);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `stu_class` where bjh = #{bjh}")
    StuClass selectByBjh(String bjh);

    @Select("select * from `stu_class` where xyh like concat('%',#{collegeNum},'%') and zyh like concat('%',#{majorNum},'%') order by id desc ")
    List<StuClass> selectByCM(String collegeNum, String majorNum);
}
