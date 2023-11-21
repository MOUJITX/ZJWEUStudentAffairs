package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 目录(Menu)实体类
 * Author: MOUJITX
 * Date: 2023-09-22 14:19:03
 */
@Data
public class Menu {

    private Integer id;

    /* 名称 */
    private String title;

    /* 路径 */
    private String path;

    /* 图标 */
    private String icon;

    /* 父级目录id */
    private String father;

    /* 说明 */
    private String note;

    private Menu menu;

    private Integer ranknum;

    private String show;
}

