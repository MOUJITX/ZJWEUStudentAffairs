package com.zjweustudent.server.common;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

/**
 * Function:
 * Author: MOUJITX
 * Date: 2023/11/9 13:22
 */

public class GlobalSet {

    ApplicationHome ah = new ApplicationHome(SpringBootApplication.class);  //获取jar存储位置

    @Getter
    private String docStorePath = ah.getSource().getParentFile().toString();  //获取相对于jar存储位置的上级目录（服务器部署后）
    //private String docStorePath = System.getProperty("user.dir");   //获取文件存储目录（本地测试）

}
