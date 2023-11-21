package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.StuMajor;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 专业表(StuMajor)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-24 21:50:37
 */

@Mapper
public interface StuMajorMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `stu_major` (xyh, zyh, zym, cc, lx, xz, zt ) " +
            "values (#{xyh}, #{zyh}, #{zym}, #{cc}, #{lx}, #{xz}, #{zt} )")
    void insert(StuMajor stuMajor);


    /**
     * 修改数据
     */
    @Update("update `stu_major` set  xyh = #{xyh} ,  zyh = #{zyh} ,  zym = #{zym} ,  cc = #{cc} ,  lx = #{lx} ,  xz = #{xz}, zt = #{zt}   " +
            "where id = #{id}")
    void update(StuMajor stuMajor);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `stu_major` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `stu_major`")
    List<StuMajor> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `stu_major` where id = #{id}")
    StuMajor selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select stu_major.id,xyh,departname AS xym,zyh,zym,cc,lx,xz,zt from `stu_major`,`department` " +
            "where xyh = departid and xyh like concat('%', #{stuMajor.xyh}, '%') and departname like concat('%', #{stuMajor.xym}, '%') and zyh like concat('%', #{stuMajor.zyh}, '%') and zym like concat('%', #{stuMajor.zym}, '%') and cc like concat('%', #{stuMajor.cc}, '%') and lx like concat('%', #{stuMajor.lx}, '%') and xz like concat('%', #{stuMajor.xz}, '%') and zt like concat('%', #{stuMajor.zt}, '%')  ")
    List<StuMajor> select(Integer temp, StuMajor stuMajor);

    /**
     * 多条件分页查询
     */
    @Select("select stu_major.id,xyh,departname AS xym,zyh,zym,cc,lx,xz,zt from `stu_major`,`department` " +
            "where xyh = departid and xyh like concat('%', #{stuMajor.xyh}, '%') and departname like concat('%', #{stuMajor.xym}, '%') and zyh like concat('%', #{stuMajor.zyh}, '%') and zym like concat('%', #{stuMajor.zym}, '%') and cc like concat('%', #{stuMajor.cc}, '%') and lx like concat('%', #{stuMajor.lx}, '%') and xz like concat('%', #{stuMajor.xz}, '%') and zt like concat('%', #{stuMajor.zt}, '%')  " +
            "limit #{skipNum}, #{pageSize}")
    List<StuMajor> pageSelect(Integer skipNum, Integer pageSize, StuMajor stuMajor);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(stu_major.id) from `stu_major`,`department` " +
            "where xyh = departid and xyh like concat('%', #{stuMajor.xyh}, '%') and departname like concat('%', #{stuMajor.xym}, '%') and zyh like concat('%', #{stuMajor.zyh}, '%') and zym like concat('%', #{stuMajor.zym}, '%') and cc like concat('%', #{stuMajor.cc}, '%') and lx like concat('%', #{stuMajor.lx}, '%') and xz like concat('%', #{stuMajor.xz}, '%') and zt like concat('%', #{stuMajor.zt}, '%')  ")
    Integer pageSelectCount(Integer temp, StuMajor stuMajor);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `stu_major` where zyh = #{zyh}")
    StuMajor selectByZyh(String zyh);

    @Select("select * from `stu_major` where xyh = #{id}")
    List<StuMajor> selectByCollege(Integer id);
}
