package com.zjweustudent.server.entity;

import lombok.Data;

/**
 * Function: 勤工助学批次(QgzxTerm)实体类
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:35
 */
@Data
public class QgzxTerm {

    private Integer id;


    private String pcmc;


    private String pcdm;


    private String dqzt;


    private String zssm;


    private String xmbz;

    private QgzxTerm qgzxTerm;
}

