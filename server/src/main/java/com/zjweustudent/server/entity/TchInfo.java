package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 教师信息表(TchInfo)实体类
 * Author: MOUJITX
 * Date: 2023-09-25 09:18:00
 */
@Data
public class TchInfo {

    private Integer id;

    /* 工号 */
    private String number;

    /* 姓名 */
    private String name;

    /* 性别 */
    private String sex;

    /* 联系电话 */
    private String phone;

    /* 学院 */
    private String department;

    /* 学院名称 */
    private String departname;

    /* 照片 */
    private String avatar;

    private TchInfo tchInfo;
}

