package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 班级表(StuClass)实体类
 * Author: MOUJITX
 * Date: 2023-09-24 22:31:48
 */
@Data
public class StuClass {

    private Integer id;

    /* 学院号 */
    private String xyh;

    /* 学院名 */
    private String xym;

    /* 专业号 */
    private String zyh;

    /* 年级 */
    private String nj;

    /* 班级号 */
    private String bjh;

    /* 班级名 */
    private String bm;

    /* 专业名 */
    private String zym;

    /* 毕业 */
    private String by;

    private StuClass stuClass;
}

