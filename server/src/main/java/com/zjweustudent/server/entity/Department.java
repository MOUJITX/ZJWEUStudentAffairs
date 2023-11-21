package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 学院/部门表(Department)实体类
 * Author: MOUJITX
 * Date: 2023-09-24 21:26:00
 */
@Data
public class Department {

    private Integer id;

    /* 部门编号 */
    private String departid;

    /* 部门名称 */
    private String departname;

    /* 类型 */
    private String type;

    /* 状态 */
    private String state;

    /* 教学机构 */
    private String college;

    /* 显示顺序 */
    private Integer ranknum;

    private Department department;
}

