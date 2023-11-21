package com.zjweustudent.server.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Function: (QgzxOfferCount)实体类
 * Author: MOUJITX
 * Date: 2023-11-13 14:46:23
 */
@Data
public class QgzxOfferCount {

    private String name;
    private Integer qtrs;
    private Integer nxrs;
    private Integer qtlq;
    private Integer nxlq;
    private Integer qtsy;
    private Integer nxsy;
    private Integer qtcb;
    private Integer nxcb;

    private QgzxOfferCount qgzxOfferCount;
}

