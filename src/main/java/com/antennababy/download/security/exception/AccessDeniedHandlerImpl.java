package com.antennababy.download.security.exception;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.antennababy.download.app.Res;
import com.antennababy.download.security.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Res res = Res.fail(HttpStatus.FORBIDDEN.value(), "权限不足");
        String json = JSONUtil.toJsonStr(res);
        WebUtils.renderString(response,json);
    }
}

