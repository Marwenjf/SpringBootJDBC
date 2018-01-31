package org.o7planning.springbootjdbc.mapper;

import org.o7planning.springbootjdbc.model.BankAccountInfo;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAccountMapper implements RowMapper<BankAccountInfo>{
    public static final String BASE_SQL = "Select ba.ID, ba.FULL_NAME, ba.BALANCE From BANK_ACCOUNT ba ";
    public BankAccountInfo mapRow(ResultSet rs,int rowNum) throws SQLException{
     Long id = rs.getLong("ID");
     String fullName = rs.getString("FULL_NAME");
     double balance = rs.getDouble("BALANCE");
     return new BankAccountInfo(id,fullName,balance);
    }

}
