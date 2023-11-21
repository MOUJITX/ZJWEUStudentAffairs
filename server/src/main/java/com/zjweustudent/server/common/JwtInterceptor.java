package com.zjweustudent.server.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.zjweustudent.server.entity.Users;
import com.zjweustudent.server.exception.ServiceException;
import com.zjweustudent.server.mapper.UsersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Function:token验证及加密
 * Author: MOUJITX
 * Date: 2023/9/18 8:52
 */

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");  //header中
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");  //url中 ?token=***
        }

        //跳过拦截器
        if (handler instanceof HandlerMethod){
            AuthAccess annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthAccess.class);
            if (annotation != null){
                return true;
            }
        }

        // 执行认证
        if (StrUtil.isBlank(token)) {
            throw new ServiceException("401", "无权限，请登录账号");
        }
        // 获取 token 中的user Id
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException("401","验证失败，请重新登录");
        }
        //根据token中的userid查询数据库
        if (userId == null || userId.equals("null")) {
            throw new ServiceException("401", "无权限，请登录账号");
        }
        Users users = usersMapper.selectById(Integer.valueOf(userId));
        if (users == null) {
            throw new ServiceException("401", "用户不存在，请重新登录");
        }

        // 用户密码加签验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(users.getPassword())).build();
        try {
            jwtVerifier.verify(token); // 验证token
        } catch (JWTVerificationException e) {
            throw new ServiceException("401", "验证失败，请重新登录");
        }
        return true;
    }
}