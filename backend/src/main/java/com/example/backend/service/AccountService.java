package com.example.backend.service;

import com.example.backend.domain.Account;
import com.example.backend.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;

    public void insertAccount(Account account) {
        accountMapper.insertAccount(account);
    }

    public List<Account> getAccountList(Account account) {
        return accountMapper.getAccountList(account);
    }

    public void deleteAccount(String date){
        accountMapper.deleteAccount(date);
    };

    public List<Integer> getAccountYears(String userId) {
        return accountMapper.getAccountYears(userId);
    }

}
