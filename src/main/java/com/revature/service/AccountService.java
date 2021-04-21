package com.revature.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.revature.dao.AccountRepository;
import com.revature.dao.ClientRepository;
import com.revature.dto.PostAccountDTO;
import com.revature.exceptions.AddAccountException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;
import com.revature.exceptions.DatabaseException;
import com.revature.exceptions.NoAccountsException;
import com.revature.model.Account;
import com.revature.model.Client;
import com.revature.util.ConnectionUtil;

public class AccountService {

	private AccountRepository accountRepository;

	// test
	private ClientRepository clientRepository;

	public AccountService() {
		this.accountRepository = new AccountRepository();
		// test
		// this.clientRepository = new ClientRepository();
	}

	// Constructor for testing.."inject" mock object into this service
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	// Test
	public Client getClientById(String clientId)
			throws DatabaseException, BadParameterException, ClientNotFoundException {

		try {
			int id = Integer.parseInt(clientId);

			Client client = clientRepository.getClientById(clientId);

			if (client == null) {
				throw new ClientNotFoundException("Client with id of " + id + " was not found");
			}

			return client;
		} catch (NumberFormatException e) {
			throw new BadParameterException("Client id must be an int. User provided " + clientId);
		}
	}

	public Account addAccount(String stringId, PostAccountDTO accountDTO)
			throws AddAccountException, BadParameterException, DatabaseException, InvalidFormatException, NullPointerException{
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
				throw new BadParameterException("Account id must be an int. User provided " + stringId);
			} catch (NullPointerException e) {
				throw new NullPointerException("Cannot be blank account");
			}

		} catch (SQLException e) {
			throw new DatabaseException(
					"Something went wrong when trying to get a connection. " + "Exception message: " + e.getMessage());
		}

	}

	public List<Account> getAccountsByClientId(String clientId) throws SQLException, DatabaseException,
			BadParameterException, ClientNotFoundException, NoAccountsException {

		List<Account> accounts = accountRepository.getAccountsByClientId(clientId);

		if (accounts.isEmpty()) {
			throw new NoAccountsException("This client has no accounts yet");
		}

		return accounts;
	}

	public List<Account> getAccountsByClientId(String clientId, String amountLessThan, String amountGreaterThan)
			throws SQLException, DatabaseException, BadParameterException, ClientNotFoundException,
			NoAccountsException {

		List<Account> accounts = accountRepository.getAccountsByClientId(clientId, amountLessThan, amountGreaterThan);

		if (accounts.isEmpty()) {
			throw new NoAccountsException("This client has no accounts yet");
		}

		return accounts;
	}

	public Account getAccountById(String accountId)
			throws NoAccountsException, BadParameterException, DatabaseException {
		try {
			//int id = Integer.parseInt(accountId);

			Account account = accountRepository.getAccountByAccountId(accountId);

			if (account == null) {
				throw new NoAccountsException("Account with id of " + accountId + " was not found");
			}

			return account;
		} catch (NumberFormatException e) {
			throw new BadParameterException("Account id must be an int. User provided " + accountId);
		}
	}

	// not catching numberFormatException badParameter exception
	public Account updateAccount(String accountId, PostAccountDTO accountDTO)
			throws BadParameterException, DatabaseException, NoAccountsException {
		try {
			Integer.parseInt(accountId);
			getAccountById(accountId);// check that account id exists

			Account account = accountRepository.updateAccountById(accountId, accountDTO);

			return account;
		} catch (NumberFormatException e) {
			throw new BadParameterException("Account id must be an int. User provided " + accountId);
		}
	}

	public void deleteAccount(String accountId)
			throws NoAccountsException, BadParameterException, DatabaseException, SQLException {
		try {
			Integer.parseInt(accountId);
			getAccountById(accountId);// check that account id exists

			accountRepository.deleteAccountById(accountId);

		} catch (NumberFormatException e) {
			throw new BadParameterException("Account id must be an int. User provided " + accountId);
		}

	}


}
