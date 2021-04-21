package com.revature.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.revature.dto.PostAccountDTO;
import com.revature.model.Account;
import com.revature.service.AccountService;
import com.revature.service.ClientService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class AccountController implements Controller {

	private Logger logger = LoggerFactory.getLogger(AccountController.class);
	private AccountService accountService;

	private ClientService clientService;

	public AccountController() {
		this.accountService = new AccountService();

		this.clientService = new ClientService();
	}

	private Handler addAccountForClient = ctx -> {
		PostAccountDTO accountDTO = ctx.bodyAsClass(PostAccountDTO.class);
		
		String clientId = ctx.pathParam("clientId");
		
		clientService.getClientById(clientId);

		Account addedAccount = accountService.addAccount(clientId, accountDTO);

		logger.info("Account: " + "'" + addedAccount.getAccountName() + "'"
				+ " was added successfully for client with id of " + clientId);
		ctx.status(201); // 201 CREATED
		ctx.json(addedAccount);

	};

	private Handler getAccountsForClient = ctx -> {
		String amountLessThan = ctx.queryParam("amountLessThan");
		String amountGreaterThan = ctx.queryParam("amountGreaterThan");

		String clientId = ctx.pathParam("clientId");
		
		clientService.getClientById(clientId);

		List<Account> accounts;

		if (amountLessThan != null && amountGreaterThan != null) {
			accounts = accountService.getAccountsByClientId(clientId, amountLessThan, amountGreaterThan);
		} else {
			accounts = accountService.getAccountsByClientId(clientId);
		}

		clientService.getClientById(clientId);

		logger.info("Successfully retrieved all account belonging to client with id of " + clientId);
		ctx.json(accounts);
	};

	private Handler getAccountByIdForClient = ctx -> {
		String clientId = ctx.pathParam("clientId");
		String accountId = ctx.pathParam("accountId");

		clientService.getClientById(clientId);

		Account account = accountService.getAccountById(accountId);

		logger.info("Successfully retrieved account with id of " + accountId + " for client with id of " + clientId);
		ctx.json(account);
		ctx.status(200);
	};

	private Handler updateAnAccountByIdForClient = ctx -> {
		String clientId = ctx.pathParam("clientId");
		String accountId = ctx.pathParam("accountId");

		clientService.getClientById(clientId);

		PostAccountDTO accountDTO = ctx.bodyAsClass(PostAccountDTO.class);

		Account account = accountService.updateAccount(accountId, accountDTO);

		logger.info("Account with id of " + account.getId() + " was updated in the database for client with id of "
				+ clientId);
		ctx.json(account);
		ctx.status(201);

	};

	private Handler deleteAnAccountForClient = ctx -> {
		String clientId = ctx.pathParam("clientId");
		String accountId = ctx.pathParam("accountId");

		clientService.getClientById(clientId);

		accountService.deleteAccount(accountId);

		logger.info(
				"Account with id of " + accountId + " was deleted in the database for client with id of " + clientId);
		ctx.result(
				"Account with id of " + accountId + " was deleted in the database for client with id of " + clientId);
		ctx.status(201);
	};

	@Override
	public void mapEndpoints(Javalin app) {
		app.post("clients/:clientId/accounts", addAccountForClient);
		app.get("clients/:clientId/accounts", getAccountsForClient);
		app.get("clients/:clientId/accounts/:accountId", getAccountByIdForClient);
		app.put("clients/:clientId/accounts/:accountId", updateAnAccountByIdForClient);
		app.delete("clients/:clientId/accounts/:accountId", deleteAnAccountForClient);
	}

}
