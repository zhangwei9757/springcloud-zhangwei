package com.microservice.controller;

import com.microservice.beans.Account;
import com.microservice.config.HessianConfig;
import com.microservice.hessian.AccountHessian;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class HessianController {

    @Autowired
    private AccountHessian accountHessian;

    @RequestMapping("/index/{select}")
    public List<Account> test(@PathVariable("select") boolean select) {
        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountName("zhangwei");
        if (select) {
            log.info("触发注解调用 ......");
            return accountHessian.getAccountByAccountName(account);
        } else {
            log.info("触发动态调用 ......");
            AccountHessian accountHessian = HessianConfig.getHessianClientBean(AccountHessian.class, "http://localhost:9999/hessian");
            return accountHessian.getAccountByAccountName(account);
        }
    }
}