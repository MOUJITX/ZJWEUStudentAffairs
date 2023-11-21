package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.QgzxTerm;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 勤工助学批次(QgzxTerm)表数据库接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:35
 */

@Mapper
public interface QgzxTermMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `qgzx_term` (pcmc, pcdm, dqzt, zssm, xmbz ) " +
            "values (#{pcmc}, #{pcdm}, #{dqzt}, #{zssm}, #{xmbz} )")
    void insert(QgzxTerm qgzxTerm);


    /**
     * 修改数据
     */
    @Update("update `qgzx_term` set  pcmc = #{pcmc} ,  pcdm = #{pcdm} ,  dqzt = #{dqzt} ,  zssm = #{zssm} ,  xmbz = #{xmbz}   " +
            "where id = #{id}")
    void update(QgzxTerm qgzxTerm);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `qgzx_term` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `qgzx_term` order by id desc ")
    List<QgzxTerm> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `qgzx_term` where id = #{id}")
    QgzxTerm selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `qgzx_term` " +
            "where pcmc like concat('%', #{qgzxTerm.pcmc}, '%')")
    List<QgzxTerm> select(Integer temp, QgzxTerm qgzxTerm);

    /**
     * 多条件分页查询
     */
    @Select("select * from `qgzx_term` " +
            "where pcmc like concat('%', #{qgzxTerm.pcmc}, '%')" +
            "limit #{skipNum}, #{pageSize}")
    List<QgzxTerm> pageSelect(Integer skipNum, Integer pageSize, QgzxTerm qgzxTerm);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `qgzx_term` " +
            "where pcmc like concat('%', #{qgzxTerm.pcmc}, '%')")
    Integer pageSelectCount(Integer temp, QgzxTerm qgzxTerm);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `qgzx_term` where pcdm = #{pcdm}")
    QgzxTerm selectByPcdm(String pcdm);

}
