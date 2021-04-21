package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

	public List<Account> getAccountsByClientId(String clientId) throws SQLException, DatabaseException {

		List<Account> accounts = new ArrayList<Account>();
		try (Connection connection = ConnectionUtil.getConnection()) {

			//String sql = "SELECT a.id, a.account_name, a.balance FROM clients c "
			//		+ "INNER JOIN accounts a ON c.id = a.client_id WHERE c.id = ?";
			
			String sql = "SELECT * FROM accounts WHERE client_id=?";

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
	
	public List<Account> getAccountsByClientId(String clientId, String amountLessThan, String amountGreaterThan) throws DatabaseException {
		List<Account> accounts = new ArrayList<Account>();
		try (Connection connection = ConnectionUtil.getConnection()) {

			String sql = "SELECT * FROM accounts WHERE client_id=? AND accounts.balance BETWEEN ? AND ?";

			PreparedStatement pstmt = connection.prepareStatement(sql);

			pstmt.setString(1, clientId);
			pstmt.setString(2, amountGreaterThan);
			pstmt.setString(3, amountLessThan);

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

	public Account getAccountByAccountId(String accountId) throws DatabaseException {
		Account account = null;

		try (Connection connection = ConnectionUtil.getConnection()) {
//			String sql = "SELECT a.id, a.account_name, a.balance FROM clients c "
//					+ "INNER JOIN accounts a ON c.id = a.client_id WHERE a.id = ?";
			String sql =  "SELECT * FROM accounts WHERE accounts.id=?";

			PreparedStatement pstmt = connection.prepareStatement(sql);

			pstmt.setString(1, accountId);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String retrievedAccountName = rs.getString("account_name");
				float retrievedBalance = rs.getFloat("balance");
				account = new Account(id, retrievedAccountName, retrievedBalance);
			}

			return account;

		} catch (SQLException e) {
			throw new DatabaseException(
					"Something went wrong when trying to get a connection. " + "Exception message: " + e.getMessage());
		}
	}

	public Account updateAccountById(String accountId, PostAccountDTO accountDTO) throws DatabaseException {
		Account updatedAccount = null;
		try (Connection connection = ConnectionUtil.getConnection()) {
			String sql = "UPDATE accounts a SET account_name = ?, balance = ? WHERE a.id = ?";

			PreparedStatement pstmt = connection.prepareStatement(sql);

			pstmt.setString(1, accountDTO.getAccountName());
			pstmt.setFloat(2, accountDTO.getBalance());
			pstmt.setString(3, accountId);

			int recordsAdded = pstmt.executeUpdate();

			if (recordsAdded != 1) {
				throw new DatabaseException("Couldn't update account in the database");
			}

			if (recordsAdded > 0) {
				int id = Integer.parseInt(accountId);
				updatedAccount = new Account(id, accountDTO.getAccountName(), accountDTO.getBalance());
				return updatedAccount;
			}

		} catch (SQLException e) {
			throw new DatabaseException(
					"Something went wrong when trying to get a connection. " + "Exception message: " + e.getMessage());
		}
		return null;
	}

	public void deleteAccountById(String accountId) throws SQLException, DatabaseException {
		try(Connection connection = ConnectionUtil.getConnection()) {
			String sql = "DELETE FROM accounts WHERE id=?";
			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			
			pstmt.setString(1, accountId);
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DatabaseException(
					"Something went wrong when trying to get a connection. " + "Exception message: " + e.getMessage());
		}
		
	}

	

}
