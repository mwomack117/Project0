package com.revature.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;

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
		
	};

	private ExceptionHandler<ClientNotFoundException> clientNotFoundExceptionHandler = (e, ctx) -> {
		logger.warn("A user tried to retrieve a client but it was not found. Exception message is: \n " + e.getMessage());
		ctx.status(404);
	};
	
	
	@Override
	public void mapEndpoints(Javalin app) {
		app.exception(BadParameterException.class, badParameterExceptionHandler);
		app.exception(ClientNotFoundException.class, clientNotFoundExceptionHandler);
		
	}
}
