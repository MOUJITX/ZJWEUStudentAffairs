package com.zjweustudent.server.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Function: 学年学期设置(Semesters)实体类
 * Author: MOUJITX
 * Date: 2023-09-25 10:32:42
 */
@Data
public class Semesters {

    private Integer id;

    /* 开始周日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date starttime;

    /* 结束周日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endtime;

    /* 学年 */
    private String schoolyear;

    /* 学期 */
    private String semester;

    private Semesters semesters;
}

