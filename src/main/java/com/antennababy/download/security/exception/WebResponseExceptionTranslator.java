package com.antennababy.download.security.exception;

import cn.hutool.json.JSONUtil;
import com.antennababy.download.app.Res;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class WebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {
    @Override
    public ResponseEntity translate(Exception e) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        log.error("WebResponseExceptionTranslator.translate :",e);
        ResponseEntity<Object> response = new ResponseEntity<>(Res.fail(401,"认证失败."), headers,HttpStatus.valueOf(200));

        return response;
    }
}
