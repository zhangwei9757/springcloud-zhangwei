package com.microservice.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;

import java.io.IOException;

@Slf4j
public class Oauth2WebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

        Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(e);
        OAuth2Exception ase = (OAuth2Exception) this.throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
        log.error(">>>>>> Oauth2.0 客户端认证失败, {}:{}", ase.getHttpErrorCode(), ase.getMessage(), ase);
        return handleOAuth2Exception(ase);
    }

    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) throws IOException {
        int status = e.getHttpErrorCode();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        if (status == HttpStatus.UNAUTHORIZED.value() || e instanceof InsufficientScopeException) {
            headers.set("WWW-Authenticate", String.format("%s %s", "Bearer", e.getSummary()));
        }

        // TODO 自定义错误实体信息, 替换下行 e 参数位置变量
        ResponseEntity<OAuth2Exception> response = new ResponseEntity(e, headers, HttpStatus.valueOf(status));
        return response;
    }

//    public static void main(String[] args) {
//        System.out.println(new BCryptPasswordEncoder().encode("41bRMoacoPW43P9zd0CjzRrFxAcQaTpA"));
//    }
}