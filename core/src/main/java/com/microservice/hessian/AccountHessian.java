package com.microservice.hessian;

import com.microservice.beans.Account;

import java.util.List;

/**
 * @author zw
 * @date 2022-01-14
 * <p>
 */
public interface AccountHessian {

    /**
     * 通过用户名获取用户信息
     * @param account
     * @return
     */
    List<Account> getAccountByAccountName(Account account);
}
