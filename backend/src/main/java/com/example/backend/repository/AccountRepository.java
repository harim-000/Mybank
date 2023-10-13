package com.example.backend.repository;

import com.example.backend.domain.Account;
import com.example.backend.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The type Account repository.
 */
@Repository
@RequiredArgsConstructor
public class AccountRepository implements AccountMapper {

    private final SqlSessionTemplate sqlSessionTemplate;
    private final String NAME_SPACE = "com.example.backend.mapper.account.";


    @Override
    public void insertAccount(Account account) {
        sqlSessionTemplate.insert(NAME_SPACE + "insertAccount", account);
    }

    @Override
    public List<Account> getAccountList(Account account) {
       return sqlSessionTemplate.selectList(NAME_SPACE + "getAccountList", account);
    }

    @Override
    public void deleteAccount(String date) {
        sqlSessionTemplate.delete(NAME_SPACE + "deleteAccount", date);
    }

    @Override
    public List<Integer> getAccountYears(String userId) {
        return sqlSessionTemplate.selectList(NAME_SPACE+"getAccountYears", userId);
    }
}
