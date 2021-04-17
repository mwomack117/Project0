package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.revature.dto.PostAccountDTO;
import com.revature.exceptions.DatabaseException;
import com.revature.model.Account;
import com.revature.model.Client;
import com.revature.util.ConnectionUtil;

public class AccountRepository {

	private Connection connection;

	public AccountRepository() {
		super();
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Account addAccount(int clientId, PostAccountDTO accountDTO) throws DatabaseException {

		try {
			String sql = "INSERT INTO accounts " + "(account_name, balance, client_id) VALUES (?, ?, ?)";

			PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, accountDTO.getAccountName());
			pstmt.setFloat(2, accountDTO.getBalance());
			pstmt.setInt(3, clientId);

			int recordAdded = pstmt.executeUpdate();

			if (recordAdded != 1) {
				throw new DatabaseException("Couldn't add an account to the database");
			}

			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
				Account newAccount = new Account(id, accountDTO.getAccountName(), accountDTO.getBalance());
				return newAccount;
			} else {
				throw new DatabaseException("Account id was not generated, and therefore adding an account failed");
			}

		} catch (SQLException e) {
			throw new DatabaseException(
					"Something went wrong with the database. " + "Exception message: " + e.getMessage());
		}
	}

	public ArrayList<Account> getAccountsByClientId(String clientId) throws SQLException, DatabaseException {
		ArrayList<Account> accounts = new ArrayList<Account>();
		try (Connection connection = ConnectionUtil.getConnection()) {

			String sql = "select a.id, a.account_name, a.balance from clients c "
					+ "inner join accounts a on c.id = a.client_id where c.id = ?";

			PreparedStatement pstmt = connection.prepareStatement(sql);

			pstmt.setString(1, clientId);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int accountId = rs.getInt("id");
				String retrievedAccountName = rs.getString("account_name");
				float retrievedBalance = rs.getFloat("balance");
				Account account = new Account(accountId, retrievedAccountName, retrievedBalance);
				accounts.add(account);
			}

		} catch (SQLException e) {
			throw new DatabaseException(
					"Something went wrong wehen trying to get a connection. " + "Exception message: " + e.getMessage());
		}

		return accounts;
	}

}
