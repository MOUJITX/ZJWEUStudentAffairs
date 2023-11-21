package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.StuInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 学生信息表(StuInfo)表数据库接口
 * Author: MOUJITX
 * Date: 2023-09-22 13:17:02
 */

@Mapper
public interface StuInfoMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `stu_info` (number, name, sex, phone, idcard, classnum, major, campus, college ) " +
            "values (#{number}, #{name}, #{sex}, #{phone}, #{idcard}, #{classnum}, #{major}, #{campus}, #{college} )")
    void insert(StuInfo stuInfo);


    /**
     * 修改数据
     */
    @Update("update `stu_info` set  number = #{number} ,  name = #{name} ,  sex = #{sex} ,  phone = #{phone} ,  idcard = #{idcard} ,  classnum = #{classnum} ,  major = #{major} ,  campus = #{campus}, college = #{college}  " +
            "where id = #{id}")
    void update(StuInfo stuInfo);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `stu_info` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `stu_info`")
    List<StuInfo> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `stu_info` where id = #{id}")
    StuInfo selectById(Integer id);

    /**
     * 通过Number查询单条数据
     */
    @Select("select * from `v_stulist` where number = #{number}")
    StuInfo selectByNumber(String number);

    /**
     * 多条件查询
     */
    @Select("select stu_info.id,number, name, sex, phone, idcard, classnum, stu_class.bm as classname, major, zym as majorname, campus, stu_info.college as college, department.departname as collegename  " +
            "from `stu_info`,`stu_class`,`stu_major`,`department` " +
            "where stu_info.college = department.departid and stu_info.major = stu_major.zyh and stu_info.classnum = stu_class.bjh " +
            "and number like concat('%', #{stuInfo.number}, '%') " +
            "and name like concat('%', #{stuInfo.name}, '%') " +
            "and sex like concat('%', #{stuInfo.sex}, '%') " +
            "and phone like concat('%', #{stuInfo.phone}, '%') " +
            "and idcard like concat('%', #{stuInfo.idcard}, '%') " +
            "and classnum like concat('%', #{stuInfo.classnum}, '%') " +
            "and bm like concat('%', #{stuInfo.classname}, '%') " +
            "and major like concat('%', #{stuInfo.major}, '%') " +
            "and zym like concat('%', #{stuInfo.majorname}, '%') " +
            "and stu_info.college like concat('%', #{stuInfo.college}, '%') " +
            "and department.departname like concat('%', #{stuInfo.collegename}, '%') " +
            "and campus like concat('%', #{stuInfo.campus}, '%')")
    List<StuInfo> select(Integer temp, StuInfo stuInfo);

    /**
     * 多条件分页查询
     */
    @Select("select stu_info.id,number, name, sex, phone, idcard, classnum, stu_class.bm as classname, major, zym as majorname, campus, stu_info.college as college, department.departname as collegename  " +
            "from `stu_info`,`stu_class`,`stu_major`,`department` " +
            "where stu_info.college = department.departid and stu_info.major = stu_major.zyh and stu_info.classnum = stu_class.bjh " +
            "and number like concat('%', #{stuInfo.number}, '%') " +
            "and name like concat('%', #{stuInfo.name}, '%') " +
            "and sex like concat('%', #{stuInfo.sex}, '%') " +
            "and phone like concat('%', #{stuInfo.phone}, '%') " +
            "and idcard like concat('%', #{stuInfo.idcard}, '%') " +
            "and classnum like concat('%', #{stuInfo.classnum}, '%') " +
            "and bm like concat('%', #{stuInfo.classname}, '%') " +
            "and major like concat('%', #{stuInfo.major}, '%') " +
            "and zym like concat('%', #{stuInfo.majorname}, '%') " +
            "and stu_info.college like concat('%', #{stuInfo.college}, '%') " +
            "and department.departname like concat('%', #{stuInfo.collegename}, '%') " +
            "and campus like concat('%', #{stuInfo.campus}, '%')" +
            "limit #{skipNum}, #{pageSize}")
    List<StuInfo> pageSelect(Integer skipNum, Integer pageSize, StuInfo stuInfo);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(stu_info.id) " +
            "from `stu_info`,`stu_class`,`stu_major`,`department` " +
            "where stu_info.college = department.departid and stu_info.major = stu_major.zyh and stu_info.classnum = stu_class.bjh " +
            "and number like concat('%', #{stuInfo.number}, '%') " +
            "and name like concat('%', #{stuInfo.name}, '%') " +
            "and sex like concat('%', #{stuInfo.sex}, '%') " +
            "and phone like concat('%', #{stuInfo.phone}, '%') " +
            "and idcard like concat('%', #{stuInfo.idcard}, '%') " +
            "and classnum like concat('%', #{stuInfo.classnum}, '%') " +
            "and bm like concat('%', #{stuInfo.classname}, '%') " +
            "and major like concat('%', #{stuInfo.major}, '%') " +
            "and zym like concat('%', #{stuInfo.majorname}, '%') " +
            "and stu_info.college like concat('%', #{stuInfo.college}, '%') " +
            "and department.departname like concat('%', #{stuInfo.collegename}, '%') " +
            "and campus like concat('%', #{stuInfo.campus}, '%')")
    Integer pageSelectCount(Integer temp, StuInfo stuInfo);

    @Select("select * from `stu_info` " +
            "where stu_info.college like concat('%',#{collegeNum},'%') " +
            "and stu_info.major like concat('%',#{majorNum},'%') " +
            "and stu_info.classnum like concat('%',#{classNum},'%') " +
            "order by id desc ")
    List<StuInfo> selectByCMC(String collegeNum, String majorNum, String classNum);
}
