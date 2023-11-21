package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: (VStuhelper)实体类
 * Author: MOUJITX
 * Date: 2023-11-07 19:53:38
 */
@Data
public class VStuhelper {

    private Integer id;

    /* 年级 */
    private String nj;

    /* 部门名称 */
    private String xym;

    /* 班级名 */
    private String bjm;

    /* 姓名 */
    private String xm;

    /* 学工号 */
    private String xh;

    /* 认定类别 */
    private String lx;

    /* 认定学年 */
    private String xn;

    private VStuhelper vStuhelper;
}

