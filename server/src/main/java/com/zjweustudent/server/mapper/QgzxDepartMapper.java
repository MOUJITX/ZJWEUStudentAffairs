package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.QgzxDepart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 用工部门信息(QgzxDepart)表数据库接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:34
 */

@Mapper
public interface QgzxDepartMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `qgzx_depart` (name, number, detail, qtlxr, qtdh, qtbgdz, qtrs, nxlxr, nxdh, nxbgdz, nxrs, gwsm, pytj ) " +
            "values (#{name}, #{number}, #{detail}, #{qtlxr}, #{qtdh}, #{qtbgdz}, #{qtrs}, #{nxlxr}, #{nxdh}, #{nxbgdz}, #{nxrs}, #{gwsm}, #{pytj} )")
    void insert(QgzxDepart qgzxDepart);


    /**
     * 修改数据
     */
    @Update("update `qgzx_depart` set  name = #{name} ,  number = #{number} ,  detail = #{detail} ,  qtlxr = #{qtlxr} ,  qtdh = #{qtdh} ,  qtbgdz = #{qtbgdz} ,  qtrs = #{qtrs} ,  nxlxr = #{nxlxr} ,  nxdh = #{nxdh} ,  nxbgdz = #{nxbgdz} ,  nxrs = #{nxrs} ,  gwsm = #{gwsm} ,  pytj = #{pytj}   " +
            "where id = #{id}")
    void update(QgzxDepart qgzxDepart);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `qgzx_depart` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `qgzx_depart`")
    List<QgzxDepart> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `qgzx_depart` where id = #{id}")
    QgzxDepart selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `qgzx_depart` " +
            "where name like concat('%', #{qgzxDepart.name}, '%') and detail like concat('%', #{qgzxDepart.detail}, '%') ")
    List<QgzxDepart> select(Integer temp, QgzxDepart qgzxDepart);

    /**
     * 多条件分页查询
     */
    @Select("select * from `qgzx_depart` " +
            "where name like concat('%', #{qgzxDepart.name}, '%') and detail like concat('%', #{qgzxDepart.detail}, '%') " +
            "limit #{skipNum}, #{pageSize}")
    List<QgzxDepart> pageSelect(Integer skipNum, Integer pageSize, QgzxDepart qgzxDepart);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `qgzx_depart` " +
            "where name like concat('%', #{qgzxDepart.name}, '%') and detail like concat('%', #{qgzxDepart.detail}, '%') ")
    Integer pageSelectCount(Integer temp, QgzxDepart qgzxDepart);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `qgzx_depart` where name = #{name} ")
    QgzxDepart selectByName(String name);

}
