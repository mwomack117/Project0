package com.revature.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.dto.PostAccountDTO;
import com.revature.model.Account;
import com.revature.model.Client;
import com.revature.service.AccountService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class AccountController implements Controller{

	private Logger logger = LoggerFactory.getLogger(AccountController.class);
	private AccountService accountService;
	
	public AccountController() {
		this.accountService = new AccountService();
	}
	
	private Handler addAccount = ctx -> {
		PostAccountDTO accountDTO = ctx.bodyAsClass(PostAccountDTO.class);
		String clientId = ctx.pathParam("id");
		
		Account addedAccount = accountService.addAccount(clientId, accountDTO);
		
		ctx.status(201); // 201 CREATED
		ctx.json(addedAccount);
		
	};
	
	private Handler getAccounts = ctx -> {
		String id = ctx.pathParam("id");
		ArrayList<Account> accounts = this.accountService.getAccountsByClientId(id);
		
		ctx.json(accounts);
	};
	
	

	@Override
	public void mapEndpoints(Javalin app) {
		app.post("clients/:id/accounts", addAccount);
		app.get("clients/:id/accounts", getAccounts);
	}

}
