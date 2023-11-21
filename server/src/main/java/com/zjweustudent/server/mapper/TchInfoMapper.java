package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.TchInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 教师信息表(TchInfo)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-25 09:18:01
 */

@Mapper
public interface TchInfoMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `tch_info` (number, name, sex, phone, department,  avatar ) " +
            "values (#{number}, #{name}, #{sex}, #{phone}, #{department}, #{avatar} )")
    void insert(TchInfo tchInfo);


    /**
     * 修改数据
     */
    @Update("update `tch_info` set  number = #{number} ,  name = #{name} ,  sex = #{sex} ,  phone = #{phone} ,  department = #{department} ,    avatar = #{avatar}  " +
            "where id = #{id}")
    void update(TchInfo tchInfo);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `tch_info` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `tch_info`")
    List<TchInfo> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `tch_info` where id = #{id}")
    TchInfo selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select tch_info.id, number, name, sex, phone, tch_info.department as department, department.departname as departname, avatar " +
            "from `tch_info`,`department` " +
            "where tch_info.department = department.departid " +
            "and number like concat('%', #{tchInfo.number}, '%') " +
            "and name like concat('%', #{tchInfo.name}, '%') " +
            "and sex like concat('%', #{tchInfo.sex}, '%') " +
            "and phone like concat('%', #{tchInfo.phone}, '%') " +
            "and tch_info.department like concat('%', #{tchInfo.department}, '%') " +
            "and department.departname like concat('%', #{tchInfo.departname}, '%') " +
            "and avatar like concat('%', #{tchInfo.avatar}, '%')   ")
    List<TchInfo> select(Integer temp, TchInfo tchInfo);

    /**
     * 多条件分页查询
     */
    @Select("select tch_info.id, number, name, sex, phone, tch_info.department as department, department.departname as departname, avatar " +
            "from `tch_info`,`department` " +
            "where tch_info.department = department.departid " +
            "and number like concat('%', #{tchInfo.number}, '%') " +
            "and name like concat('%', #{tchInfo.name}, '%') " +
            "and sex like concat('%', #{tchInfo.sex}, '%') " +
            "and phone like concat('%', #{tchInfo.phone}, '%') " +
            "and tch_info.department like concat('%', #{tchInfo.department}, '%') " +
            "and department.departname like concat('%', #{tchInfo.departname}, '%') " +
            "and avatar like concat('%', #{tchInfo.avatar}, '%') " +
            "limit #{skipNum}, #{pageSize} ")
    List<TchInfo> pageSelect(Integer skipNum, Integer pageSize, TchInfo tchInfo);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(tch_info.id) " +
            "from `tch_info`,`department` " +
            "where tch_info.department = department.departid " +
            "and name like concat('%', #{tchInfo.name}, '%') " +
            "and sex like concat('%', #{tchInfo.sex}, '%') " +
            "and phone like concat('%', #{tchInfo.phone}, '%') " +
            "and tch_info.department like concat('%', #{tchInfo.department}, '%') " +
            "and department.departname like concat('%', #{tchInfo.departname}, '%') " +
            "and avatar like concat('%', #{tchInfo.avatar}, '%')   ")
    Integer pageSelectCount(Integer temp, TchInfo tchInfo);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `tch_info` where number = #{number}")
    TchInfo selectByNumber(String number);

}
