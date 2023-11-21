package com.zjweustudent.server.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Function: 轮播图(Image)实体类
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:10
 */
@Data
public class Image {
    /* 轮播图ID */
    private Integer id;

    /* 标题 */
    private String title;

    /* 链接 */
    private String url;

    /* 轮播图 */
    private String img;

    /* 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createtime;

    /* 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatetime;

    private Image image;
}

