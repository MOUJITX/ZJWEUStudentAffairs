package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.VStuhelper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: (VStuhelper)表数据库接口
 * Author: MOUJITX
 * Date: 2023-11-07 19:53:38
 */

@Mapper
public interface VStuhelperMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `qgzx_helper` ( xh, lx, xn ) " +
            "values ( #{xh}, #{lx}, #{xn} )")
    void insert(VStuhelper vStuhelper);


    /**
     * 修改数据
     */
    @Update("update `qgzx_helper` set    xh = #{xh} ,  lx = #{lx} ,  xn = #{xn}   " +
            "where id = #{id}")
    void update(VStuhelper vStuhelper);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `qgzx_helper` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `v_stuhelper`")
    List<VStuhelper> selectAll();

    /*返回资助类型*/
    @Select("SELECT lx FROM qgzx_helper WHERE xh=#{xh} AND xn=#{xn}")
    String getType(String xh, String xn);

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `v_stuhelper` where id = #{id}")
    VStuhelper selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `v_stuhelper` " +
            "where nj like concat('%', #{vStuhelper.nj}, '%') " +
            "and xym like concat('%', #{vStuhelper.xym}, '%') " +
            "and bjm like concat('%', #{vStuhelper.bjm}, '%') " +
            "and xm like concat('%', #{vStuhelper.xm}, '%') " +
            "and xh like concat('%', #{vStuhelper.xh}, '%') " +
            "and lx like concat('%', #{vStuhelper.lx}, '%') " +
            "and xn like concat('%', #{vStuhelper.xn}, '%') " +
            "order by xn,xym,nj,bjm,xh,lx DESC ")
    List<VStuhelper> select(Integer temp, VStuhelper vStuhelper);

    /**
     * 多条件分页查询
     */
    @Select("select * from `v_stuhelper` " +
            "where nj like concat('%', #{vStuhelper.nj}, '%') " +
            "and xym like concat('%', #{vStuhelper.xym}, '%') " +
            "and bjm like concat('%', #{vStuhelper.bjm}, '%') " +
            "and xm like concat('%', #{vStuhelper.xm}, '%') " +
            "and xh like concat('%', #{vStuhelper.xh}, '%') " +
            "and lx like concat('%', #{vStuhelper.lx}, '%') " +
            "and xn like concat('%', #{vStuhelper.xn}, '%') " +
            "order by xn,xym,nj,bjm,xh,lx DESC  " +
            "limit #{skipNum}, #{pageSize}")
    List<VStuhelper> pageSelect(Integer skipNum, Integer pageSize, VStuhelper vStuhelper);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `v_stuhelper` " +
            "where nj like concat('%', #{vStuhelper.nj}, '%') " +
            "and xym like concat('%', #{vStuhelper.xym}, '%') " +
            "and bjm like concat('%', #{vStuhelper.bjm}, '%') " +
            "and xm like concat('%', #{vStuhelper.xm}, '%') " +
            "and xh like concat('%', #{vStuhelper.xh}, '%') " +
            "and lx like concat('%', #{vStuhelper.lx}, '%') " +
            "and xn like concat('%', #{vStuhelper.xn}, '%') " +
            "order by xn,xym,nj,bjm,xh,lx DESC  ")
    Integer pageSelectCount(Integer temp, VStuhelper vStuhelper);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `v_stuhelper` where xh = #{xh} and xn = #{xn}")
    VStuhelper selectByStuInfo(String xh, String xn);

}
