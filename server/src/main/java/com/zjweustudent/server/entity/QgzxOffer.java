package com.zjweustudent.server.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Function: (QgzxOffer)实体类
 * Author: MOUJITX
 * Date: 2023-11-13 14:46:23
 */
@Data
public class QgzxOffer {

    private Integer id;

    /* 校区 */
    private String xq;

    /* 年级 */
    private String nj;

    /* 学院 */
    private String xy;

    /* 班级 */
    private String bj;

    /* 学号 */
    private String xh;

    /* 姓名 */
    private String xm;

    /* 录用部门 */
    private String bm;

    /* 录用学年 */
    private String xn;

    /* 招聘批次 */
    private String pc;

    /* 录用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    /* 录用结果 */
    private String zt;

    /* 申请表id */
    private Integer applyid;

    private QgzxOffer qgzxOffer;
}

