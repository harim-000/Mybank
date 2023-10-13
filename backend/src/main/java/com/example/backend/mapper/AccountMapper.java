package com.example.backend.mapper;

import com.example.backend.domain.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * The interface Account mapper.
 */
@Mapper
public interface AccountMapper {

    /**
     * Insert account.
     *
     * @param account the account
     */
    void insertAccount(Account account);

    /**
     * Gets account list.
     *
     * @return the account list
     */
    List<Account> getAccountList(Account account);

    /**
     * Delete account.
     *
     * @param date the date
     */
    void deleteAccount(String date);

    List<Integer> getAccountYears(String userId);
}
