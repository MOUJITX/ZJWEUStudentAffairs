package com.zjweustudent.server.common;

import lombok.*;

/**
 * Function:消息统一返回接口
 * Author: MOUJITX
 * Date: 2023/9/17 21:59
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {
    public static final String CODE_SUCCESS = "200";
    public static final String CODE_AUTH_ERROR = "401";
    public static final String CODE_SYS_ERROR = "500";

    private String code;
    private String msg;
    private Object data;

    public static Result success(){
        return new Result(CODE_SUCCESS,"Request Success",null);
    }
    public static Result success(Object data){
        return new Result(CODE_SUCCESS,"Request Success",data);
    }
    public static Result success(String title, Object data){
        return new Result(CODE_SUCCESS,title,data);
    }
    public static Result error(String msg){
        return new Result(CODE_SYS_ERROR,msg,null);
    }
    public static Result error(String code, String msg){
        return new Result(code,msg,null);
    }

    public static Result errorWithTitle(String title, String msg){
        return new Result(CODE_SYS_ERROR,title,msg);
    }
    public static Result error(){
        return new Result(CODE_SYS_ERROR,"System Error",null);
    }
}
