package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 学生信息表(StuInfo)实体类
 * Author: MOUJITX
 * Date: 2023-09-22 13:17:00
 */
@Data
public class StuInfo {
    /* 序号 */
    private Integer id;

    /* 学号工号 */
    private String number;

    /* 姓名 */
    private String name;

    /* 性别 */
    private String sex;

    /* 联系电话 */
    private String phone;

    /* 身份证号 */
    private String idcard;

    /* 班级 */
    private String classnum;
    private String classname;

    /* 专业 */
    private String major;
    private String majorname;

    /*学院*/
    private String college;
    private String collegename;

    /* 校区 */
    private String campus;

    /* 年级 */
    private String nj;

    private StuInfo stuInfo;
}

