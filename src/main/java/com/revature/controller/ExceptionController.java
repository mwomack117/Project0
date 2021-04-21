package com.revature.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.revature.dto.MessageDTO;
import com.revature.exceptions.AddAccountException;
import com.revature.exceptions.AddClientException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;
import com.revature.exceptions.NoAccountsException;

import io.javalin.Javalin;
import io.javalin.http.ExceptionHandler;

public class ExceptionController implements Controller {

	private Logger logger = LoggerFactory.getLogger(ExceptionController.class);

	/*
	 * Exception handler
	 */

	private ExceptionHandler<BadParameterException> badParameterExceptionHandler = (e, ctx) -> {
		logger.warn("A user provided a bad parameter. Exception message is: \n " + e.getMessage());
		ctx.status(400); // Provide an appropriate status code, such as 400
		ctx.json(new MessageDTO(e.getMessage()));
	};

	private ExceptionHandler<ClientNotFoundException> clientNotFoundExceptionHandler = (e, ctx) -> {
		logger.warn(
				"A user tried to retrieve a client but it was not found. Exception message is: \n " + e.getMessage());
		ctx.status(404);
		ctx.json(new MessageDTO(e.getMessage()));
	};

	private ExceptionHandler<AddClientException> addClientExceptionHandler = (e, ctx) -> {
		logger.warn("Could not add client. Exception message is \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};

	private ExceptionHandler<AddAccountException> addAccountExceptionHandler = (e, ctx) -> {
		logger.warn("Could not add account. Exception message is \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};

	private ExceptionHandler<NoAccountsException> noAccountsExceptionHandler = (e, ctx) -> {
		logger.warn("Could not retrieve accounts. Exception message is \n" + e.getMessage());
		ctx.status(404);
		ctx.json(new MessageDTO(e.getMessage()));
	};

	private ExceptionHandler<InvalidFormatException> notFloatExceptionHandler = (e, ctx) -> {
		logger.warn("Could not add account balance of type string. Must Be number. Exception message is \n" + e.getMessage());
		ctx.status(400);
		ctx.json(new MessageDTO(e.getMessage()));
	};
	

	@Override
	public void mapEndpoints(Javalin app) {
		app.exception(BadParameterException.class, badParameterExceptionHandler);
		app.exception(ClientNotFoundException.class, clientNotFoundExceptionHandler);
		app.exception(AddClientException.class, addClientExceptionHandler);
		app.exception(AddAccountException.class, addAccountExceptionHandler);
		app.exception(NoAccountsException.class, noAccountsExceptionHandler);
		app.exception(InvalidFormatException.class, notFloatExceptionHandler);
	}
}
