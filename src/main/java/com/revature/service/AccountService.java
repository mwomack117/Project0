package com.revature.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.revature.dao.AccountRepository;
import com.revature.dto.PostAccountDTO;
import com.revature.exceptions.AddAccountException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.Account;
import com.revature.model.Client;
import com.revature.util.ConnectionUtil;

public class AccountService {

	private AccountRepository accountRepository;

	public AccountService() {
		this.accountRepository = new AccountRepository();
	}

	// Constructor for testing.."inject" mock object into this service
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Account addAccount(String stringId, PostAccountDTO accountDTO)
			throws AddAccountException, BadParameterException, DatabaseException {
		try {
			Connection connection = ConnectionUtil.getConnection();// Have control over a connection object here
			this.accountRepository.setConnection(connection); // pass the connection into the DAO
			connection.setAutoCommit(false); // Turn off autocommit so we have control over commit v. not committing

			if (accountDTO.getAccountName().trim().equals("")) {
				throw new AddAccountException("Account name cannot be blank");
			}
			if (accountDTO.getBalance() < 0) {
				throw new AddAccountException("Account balance cannot be a negative number");
			}

			try {
				int clientId = Integer.parseInt(stringId);

				Account account = accountRepository.addAccount(clientId, accountDTO);

				connection.commit(); // When changes will actually be persisted
				return account;
			} catch (NumberFormatException e) {
				throw new BadParameterException("Client id must be an int. User provided " + stringId);
			}

		} catch (SQLException e) {
			throw new DatabaseException(
					"Something went wrong when trying to get a connection. " + "Exception message: " + e.getMessage());
		}

	}

	public ArrayList<Account> getAccountsByClientId(String clientId) throws SQLException, DatabaseException {

		ArrayList<Account> accounts = accountRepository.getAccountsByClientId(clientId);

		return accounts;
	}

}
