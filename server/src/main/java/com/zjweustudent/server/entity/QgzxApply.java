package com.zjweustudent.server.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Function: 勤工申请(QgzxApply)实体类
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:31
 */
@Data
public class QgzxApply {
    /* id */
    private Integer id;

    /* 申请学年 */
    private String sqxn;

    /* 当前进度：暂存save/待审批doing/审批驳回refuse/审批通过finish */
    private String speed;

    /* 姓名 */
    private String name;

    /* 学号 */
    private String username;

    /* 性别 */
    private String male;

    /* 学院 */
    private String college;

    /* 班级 */
    private String classname;

    /* 联系电话 */
    private String phone;

    /* qq */
    private String qq;

    /* 资助对象 */
    private String type;

    /* 个人技能 */
    private String skill;

    /* 申请岗位 */
    private String depart;

    /* 申请岗位2 */
    private String departb;

    /* 是否调剂 */
    private String choosetj;

    /* 上学年勤工经历 */
    private String lastdepart;

    /* 课余时间 */
    private Integer timea;


    private Integer timeb;


    private Integer timec;


    private Integer timed;


    private Integer timee;


    private Integer timef;


    private Integer timeg;

    /* 提交时间 */
    private String uptime;

    /* 年级 */
    private String grade;

    /* 校区 */
    private String campus;

    /* 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replytime;

    /* 审核人 */
    private String replyname;

    /* 审核人工号 */
    private String replynum;

    private QgzxApply qgzxApply;
}

