package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 用户组(UserGroup)实体类
 * Author: MOUJITX
 * Date: 2023-09-22 14:38:24
 */
@Data
public class UserGroup {
    /* 用户组id */
    private Integer id;

    /* 用户组名称 */
    private String name;

    /* 说明 */
    private String note;

    /* 可访问目录 */
    private String accessmenu;

    private UserGroup userGroup;
}

