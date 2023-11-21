package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 用工部门信息(QgzxDepart)实体类
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:34
 */
@Data
public class QgzxDepart {
    /* id */
    private Integer id;

    /* 部门名称 */
    private String name;

    /* 部门编号 */
    private String number;

    /* 部门说明 */
    private String detail;

    /* 钱塘校区联系人 */
    private String qtlxr;

    /* 钱塘校区联系电话 */
    private String qtdh;

    /* 钱塘校区办公地址 */
    private String qtbgdz;

    /* 钱塘校区岗位人数 */
    private Integer qtrs;

    /* 南浔校区联系人 */
    private String nxlxr;

    /* 南浔校区联系电话 */
    private String nxdh;

    /* 南浔校区办公地址 */
    private String nxbgdz;

    /* 南浔校区岗位人数 */
    private Integer nxrs;

    /* 岗位说明 */
    private String gwsm;

    /* 聘用条件 */
    private String pytj;

    private QgzxDepart qgzxDepart;
}

