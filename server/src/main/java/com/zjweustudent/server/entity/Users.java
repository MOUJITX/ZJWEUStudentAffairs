package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 用户信息(Users)实体类
 * Author: MOUJITX
 * Date: 2023-09-22 14:46:03
 */
@Data
public class Users {

    private Integer id;

    /* 昵称 */
    private String nickname;

    /* 账号 */
    private String username;

    /* 密码 */
    private String password;

    /* 说明 */
    private String note;

    /* 用户组 */
    private String usergroup;

    private Users users;

    private String token;
}

