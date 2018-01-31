package org.o7planning.springbootjdbc.dao;

import java.util.List;

import javax.sql.DataSource;

import org.o7planning.springbootjdbc.exception.BankTransactionException;
import org.o7planning.springbootjdbc.mapper.BankAccountMapper;
import org.o7planning.springbootjdbc.model.BankAccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BankAccountDAO extends JdbcDaoSupport{
    @Autowired
    public BankAccountDAO(DataSource dataSource) {
        this.setDataSource(dataSource);
    }
    public List<BankAccountInfo> getBankAccounts(){
        String sql = BankAccountMapper.BASE_SQL;
        Object[] params = new Object[] {};
        BankAccountMapper mapper = new BankAccountMapper();
        List<BankAccountInfo> list = this.getJdbcTemplate().query(sql,params,mapper);
        return list;
    }
    public BankAccountInfo findBankAccount(Long id) {
        String sql = BankAccountMapper.BASE_SQL+" where ba.ID = ? ";
        Object[] params = new Object[] { id };
        BankAccountMapper mapper = new BankAccountMapper();
        try {
            BankAccountInfo bankAccount = this.getJdbcTemplate().queryForObject(sql, params, mapper);
            return bankAccount;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }
    @Transactional(propagation = Propagation.MANDATORY)
    public void addAmount(Long id, double amount) throws BankTransactionException {
        BankAccountInfo accountInfo = this.findBankAccount(id);
        if (accountInfo == null) {
            throw new BankTransactionException("Account not found " + id);
        }
        double newBalance = accountInfo.getBalance() + amount;
        if (accountInfo.getBalance() + amount < 0) {
            throw new BankTransactionException(
                    "The money in the account '" + id + "' is not enough (" + accountInfo.getBalance() + ")");
        }
        accountInfo.setBalance(newBalance);
        // Update to DB
        String sqlUpdate = "Update BANK_ACCOUNT set BALANCE = ? where ID = ?";
        this.getJdbcTemplate().update(sqlUpdate, accountInfo.getBalance(), accountInfo.getId());
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = BankTransactionException.class)
    public void sendMoney(Long fromAccountId, Long toAccountId, double amount) throws BankTransactionException {

        addAmount(toAccountId, amount);
        addAmount(fromAccountId, -amount);
    }
}
