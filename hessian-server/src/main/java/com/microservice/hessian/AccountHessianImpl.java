package com.microservice.hessian;

import com.google.common.collect.Lists;
import com.microservice.beans.Account;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zw
 * @date 2022-01-14
 * <p>
 */
@Service
public class AccountHessianImpl implements AccountHessian {

    @Override
    public List<Account> getAccountByAccountName(Account account) {
        return Lists.newArrayList(account);
    }
}
