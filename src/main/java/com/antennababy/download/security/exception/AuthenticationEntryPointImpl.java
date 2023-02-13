package com.antennababy.download.security.exception;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.antennababy.download.app.Res;
import com.antennababy.download.security.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("AuthenticationEntryPointImpl.commence :",authException);
        Res fail = Res.fail(HttpStatus.UNAUTHORIZED.value()+"", "认证失败请重新登录");
        String json = JSONUtil.toJsonStr(fail);
        WebUtils.renderString(response,json);
    }
}

