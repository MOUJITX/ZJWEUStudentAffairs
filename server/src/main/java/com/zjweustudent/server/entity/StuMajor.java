package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 专业表(StuMajor)实体类
 * Author: MOUJITX
 * Date: 2023-09-24 21:50:37
 */
@Data
public class StuMajor {

    private Integer id;

    /* 学院号 */
    private String xyh;
    private String xym;

    /* 专业号 */
    private String zyh;

    /* 专业名 */
    private String zym;

    /* 层次 */
    private String cc;

    /* 类型 */
    private String lx;

    /* 学制 */
    private String xz;

    /* 状态 */
    private String zt;

    private StuMajor stuMajor;
}

