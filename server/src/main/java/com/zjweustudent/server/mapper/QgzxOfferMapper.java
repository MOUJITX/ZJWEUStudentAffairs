package com.zjweustudent.server.mapper;

import com.zjweustudent.server.entity.QgzxDepart;
import com.zjweustudent.server.entity.QgzxOffer;
import com.zjweustudent.server.entity.QgzxOfferCount;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Function: (QgzxOffer)表数据库接口
 * Author: MOUJITX
 * Date: 2023-11-13 14:46:23
 */

@Mapper
public interface QgzxOfferMapper {

    /**
     * 新增数据
     */
    @Insert("insert into `qgzx_offer` (xq, nj, xy, bj, xh, xm, bm, xn, pc,  zt, applyid ) " +
            "values (#{xq}, #{nj}, #{xy}, #{bj}, #{xh}, #{xm}, #{bm}, #{xn}, #{pc},  #{zt}, #{applyid} )")
    void insert(QgzxOffer qgzxOffer);


    /**
     * 修改数据
     */
    @Update("update `qgzx_offer` set  xq = #{xq} ,  nj = #{nj} ,  xy = #{xy} ,  bj = #{bj} ,  xh = #{xh} ,  xm = #{xm} ,  bm = #{bm} ,  xn = #{xn} ,  pc = #{pc} ,  time = #{time} ,  zt = #{zt} ,  applyid = #{applyid}   " +
            "where id = #{id}")
    void update(QgzxOffer qgzxOffer);

    /**
     * 通过主键删除数据
     */
    @Delete("delete from `qgzx_offer` where id = #{id}")
    void delete(Integer id);

    /**
     * 查询全部数据
     */
    @Select("select * from `qgzx_offer`")
    List<QgzxOffer> selectAll();

    /**
     * 通过ID查询单条数据
     */
    @Select("select * from `qgzx_offer` where id = #{id}")
    QgzxOffer selectById(Integer id);

    /**
     * 多条件查询
     */
    @Select("select * from `qgzx_offer` " +
            "where xy like concat('%', #{qgzxOffer.xy}, '%') " +
            "and bj like concat('%', #{qgzxOffer.bj}, '%') " +
            "and xh like concat('%', #{qgzxOffer.xh}, '%') " +
            "and xm like concat('%', #{qgzxOffer.xm}, '%') " +
            "and bm like concat('%', #{qgzxOffer.bm}, '%') " +
            "and xq like concat('%', #{qgzxOffer.xq}, '%') " +
            "and nj like concat('%', #{qgzxOffer.nj}, '%') " +
            "and pc = #{qgzxOffer.pc} " +
            "and zt like concat('%', #{qgzxOffer.zt}, '%')  ")
    List<QgzxOffer> select(Integer temp, QgzxOffer qgzxOffer);

    /**
     * 多条件分页查询
     */
    @Select("select * from `qgzx_offer` " +
            "where xy like concat('%', #{qgzxOffer.xy}, '%') " +
            "and bj like concat('%', #{qgzxOffer.bj}, '%') " +
            "and xh like concat('%', #{qgzxOffer.xh}, '%') " +
            "and xm like concat('%', #{qgzxOffer.xm}, '%') " +
            "and bm like concat('%', #{qgzxOffer.bm}, '%') " +
            "and xq like concat('%', #{qgzxOffer.xq}, '%') " +
            "and nj like concat('%', #{qgzxOffer.nj}, '%') " +
            "and pc = #{qgzxOffer.pc} " +
            "and zt like concat('%', #{qgzxOffer.zt}, '%') " +
            "limit #{skipNum}, #{pageSize}")
    List<QgzxOffer> pageSelect(Integer skipNum, Integer pageSize, QgzxOffer qgzxOffer);

    /**
     * 多条件查询返回数据数量
     */
    @Select("select count(id) from `qgzx_offer` " +
            "where xy like concat('%', #{qgzxOffer.xy}, '%') " +
            "and bj like concat('%', #{qgzxOffer.bj}, '%') " +
            "and xh like concat('%', #{qgzxOffer.xh}, '%') " +
            "and xm like concat('%', #{qgzxOffer.xm}, '%') " +
            "and bm like concat('%', #{qgzxOffer.bm}, '%') " +
            "and xq like concat('%', #{qgzxOffer.xq}, '%') " +
            "and nj like concat('%', #{qgzxOffer.nj}, '%') " +
            "and pc = #{qgzxOffer.pc} " +
            "and zt like concat('%', #{qgzxOffer.zt}, '%') " )
    Integer pageSelectCount(Integer temp, QgzxOffer qgzxOffer);

    /**
     * 修改参照
     * 通过（唯一字段）查询单条数据
     */

    @Select("select * from `qgzx_offer` where xh = #{xh} and pc = #{pc} and zt = '确认录用'")
    QgzxOffer selectByXHPC(String xh,String pc);


    @Select("select * from `qgzx_offer` where xh = #{xh} and zt = '确认录用' order by id desc ")
    List<QgzxOffer> selectByXH(String xh);

    @Select("SELECT qtrs,nxrs FROM qgzx_depart WHERE `name` = #{bm} ")
    QgzxDepart queryDepartNum(String bm);

    @Select("select xq,COUNT(xh) AS xh from qgzx_offer WHERE pc = #{pc} AND bm = #{bm} AND zt = '确认录用' group by xq ORDER BY xq DESC ")
    List<QgzxOffer> queryOfferNum(String pc,String bm);

    @Select("SELECT `name`,qtrs,nxrs," +
            "IFNULL( qtlq, 0 ) AS qtlq," +
            "IFNULL( nxlq, 0 ) AS nxlq," +
            "IFNULL(qtrs - qtlq,qtrs) AS qtsy," +
            "IFNULL(nxrs - nxlq,nxrs) AS nxsy," +
            "IF(qtrs-qtlq<0,1,0) AS qtcb," +
            "IF(nxrs-nxlq<0,1,0) AS nxcb " +
            "FROM qgzx_depart " +
            "LEFT JOIN v_qgzxoffer ON v_qgzxoffer.bm = qgzx_depart.`name` AND pc = #{pc}")
    List<QgzxOfferCount> countpc(String pc);
}
