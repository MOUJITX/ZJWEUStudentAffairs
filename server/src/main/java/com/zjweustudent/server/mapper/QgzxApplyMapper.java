package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.QgzxApply;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Function: 勤工申请(QgzxApply)表数据库接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:31
 */

@Mapper
public interface QgzxApplyMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `qgzx_apply` (sqxn, speed, name, username, male, college, classname, " +
            "phone, qq, type, skill, depart, departb, choosetj, lastdepart, timea, timeb, timec, " +
            "timed, timee, timef, timeg, uptime, grade, campus, replytime, replyname, replynum ) " +
            "values (#{sqxn}, #{speed}, #{name}, #{username}, #{male}, #{college}, #{classname}, " +
            "#{phone}, #{qq}, #{type}, #{skill}, #{depart}, #{departb}, #{choosetj}, #{lastdepart}, #{timea}, #{timeb}, #{timec}, " +
            "#{timed}, #{timee}, #{timef}, #{timeg}, #{uptime}, #{grade}, #{campus}, #{replytime}, #{replyname}, #{replynum} )")
    void insert(QgzxApply qgzxApply);


    /**
     * 修改数据
     */
    @Update("update `qgzx_apply` set  sqxn = #{sqxn} ,  speed = #{speed} ,  name = #{name} ,  username = #{username} ,  " +
            "male = #{male} ,  college = #{college} ,  classname = #{classname} ,  phone = #{phone} ,  qq = #{qq} ,  " +
            "type = #{type} ,  skill = #{skill} ,  depart = #{depart} ,  departb = #{departb} ,  choosetj = #{choosetj} ,  " +
            "lastdepart = #{lastdepart} ,  timea = #{timea} ,  timeb = #{timeb} ,  timec = #{timec} ,  timed = #{timed} ,  " +
            "timee = #{timee} ,  timef = #{timef} ,  timeg = #{timeg} ,  uptime = #{uptime} ,  grade = #{grade} ,  " +
            "campus = #{campus} ,  replytime = #{replytime} ,  replyname = #{replyname} ,  replynum = #{replynum}   " +
            "where id = #{id}")
    void update(QgzxApply qgzxApply);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `qgzx_apply` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `qgzx_apply`  order by id desc ")
    List<QgzxApply> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `qgzx_apply` where id = #{id}")
    QgzxApply selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `qgzx_apply` " +
            "where name like concat('%', #{qgzxApply.name}, '%') and username like concat('%', #{qgzxApply.username}, '%') " +
            "and sqxn = #{qgzxApply.sqxn} and speed like concat('%',#{qgzxApply.speed}, '%') and type like concat('%', #{qgzxApply.type}, '%') " +
            "and college like concat('%', #{qgzxApply.college}, '%') " +
            "and campus like concat('%', #{qgzxApply.campus}, '%')  " +
            "and grade like concat('%', #{qgzxApply.grade}, '%')  " +
            "order by id desc ")
    List<QgzxApply> select(Integer temp, QgzxApply qgzxApply);

    /**
     * 多条件分页查询
     */
    @Select("select * from `qgzx_apply` " +
            "where name like concat('%', #{qgzxApply.name}, '%') and username like concat('%', #{qgzxApply.username}, '%') " +
            "and sqxn = #{qgzxApply.sqxn} and speed like concat('%',#{qgzxApply.speed}, '%') and type like concat('%', #{qgzxApply.type}, '%') " +
            "and college like concat('%', #{qgzxApply.college}, '%')  " +
            "and campus like concat('%', #{qgzxApply.campus}, '%')  " +
            "and grade like concat('%', #{qgzxApply.grade}, '%')  " +
            "order by id desc " +
            "limit #{skipNum}, #{pageSize}")
    List<QgzxApply> pageSelect(Integer skipNum, Integer pageSize, QgzxApply qgzxApply);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `qgzx_apply` " +
            "where name like concat('%', #{qgzxApply.name}, '%') and username like concat('%', #{qgzxApply.username}, '%') " +
            "and sqxn = #{qgzxApply.sqxn} and speed like concat('%',#{qgzxApply.speed}, '%') and type like concat('%', #{qgzxApply.type}, '%') " +
            "and college like concat('%', #{qgzxApply.college}, '%') " +
            "and campus like concat('%', #{qgzxApply.campus}, '%')  " +
            "and grade like concat('%', #{qgzxApply.grade}, '%')  " +
            "order by id desc ")
    Integer pageSelectCount(Integer temp, QgzxApply qgzxApply);

    @Update("update `qgzx_apply` set speed = #{speed}, replytime = #{rtime}, replyname = #{rname}, replynum = #{rnum} where id = #{id}")
    void setSpeed(Integer id, String speed, String rtime, String rname, String rnum);

    @Select("select * from qgzx_apply where username = #{number} " +
            "order by id desc ")
    List<QgzxApply> selectByNum(String number);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `qgzx_apply` where username = #{username} and sqxn = #{sqxn}")
    QgzxApply selectByStuApplyInfo(String username, String sqxn);

    @Select("select id, speed, sqxn, college, classname, username, `name` " +
            "from zjweusao.`qgzx_apply` " +
            "where sqxn = #{pcdm} and speed = 'finish' ")
    List<QgzxApply> selectFinish(String pcdm);

    @Select("SELECT id, speed, sqxn, college, classname, username, `name` " +
            "FROM qgzx_apply " +
            "WHERE sqxn = #{pcdm} " +
            "AND speed = 'finish' " +
            "AND ( depart = #{bm} OR departb = #{bm} )")
    List<QgzxApply> selectFinishByBM(String pcdm, String bm);
}
