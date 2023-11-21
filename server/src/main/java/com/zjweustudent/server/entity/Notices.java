package com.zjweustudent.server.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Function: 通知公告(Notices)实体类
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:32
 */
@Data
public class Notices {

    private Integer id;

    /* 标题 */
    private String title;

    /* 置顶 */
    private String top;

    /* 发布状态 */
    private String publish;

    /* 发布日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    /* 发布位置 */
    private String source;

    /* 作者 */
    private String author;

    /* 内容 */
    private String detail;

    /* 概要 */
    private String simple;

    /* 发布人 */
    private String adduser;

    /* 最后修改人 */
    private String edituser;

    private Notices notices;
}

