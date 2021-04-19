package com.revature.controller;

import java.util.ArrayList;

import com.revature.app.Application;
import com.revature.dto.PostClientDTO;
import com.revature.model.Client;
import com.revature.service.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class ClientController implements Controller {
	
	private static Logger logger = LoggerFactory.getLogger(ClientController.class);

	private ClientService clientService;

	public ClientController() {
		this.clientService = new ClientService();
	}

	private Handler getClients = ctx -> {
		ArrayList<Client> clients = clientService.getAllClients();

		ctx.json(clients);
		ctx.status(200);
	};
	
	private Handler getClientById = ctx -> {
		String clientId = ctx.pathParam("clientId");
		
		Client client = clientService.getClientById(clientId);
		
		logger.info("A client with id of: " + clientId + " was retrieved from the database");
		ctx.json(client);
		ctx.status(200);
	};

	private Handler addClient = ctx -> {
		PostClientDTO clientDTO = ctx.bodyAsClass(PostClientDTO.class);

		Client client = clientService.addClient(clientDTO);
		
		logger.info("A client was added to the database with id of: " + client.getId());
		ctx.json(client);
		ctx.status(201);
	};
	
	private Handler updateClientById = ctx -> {
		String clientId = ctx.pathParam("clientId");
		
		PostClientDTO clientDTO = ctx.bodyAsClass(PostClientDTO.class);
		
		Client client = clientService.updateClient(clientId, clientDTO);
		
		logger.info("Client with id of " + client.getId() + " was updated in the database");
		ctx.json(client);
		ctx.status(201);
	};
	
	private Handler deleteClientById = ctx -> {
		String clientId = ctx.pathParam("clientId");
		
		clientService.deleteClient(clientId);
		
		logger.info("Client with id of " + clientId + " successfully deleted in the database");
		ctx.result("Client with Id of " + clientId + " successfully deleted from database");
		ctx.status(201);
	};
	
	

	@Override
	public void mapEndpoints(Javalin app) {
		app.get("/clients", getClients);
		app.get("/clients/:clientId", getClientById); 
		app.post("/clients", addClient);
		app.put("/clients/:clientId", updateClientById);
		app.delete("/clients/:clientId", deleteClientById);
		
	}

}

//package com.revature.controller;
//
//import java.util.ArrayList;
//
//import com.revature.model.Client;
//import com.revature.service.ClientService;
//
//import io.javalin.Javalin;
//import io.javalin.http.ExceptionHandler;
//import io.javalin.http.Handler;
//
//public class ClientController implements Controller {
//	
//	private ClientService clientService;
//	
//	public ClientController() {
//		this.clientService = new ClientService();
//	}
//	
//	private Handler getClients = ctx -> {
//		ArrayList<Client> clients = clientService.getAllClients();
//		
//		ctx.json(clients);
//		ctx.status(200);
//	};
//	
//	private Handler getClientsById = ctx -> {
//		String id = ctx.pathParam("id");
//		
//		Client client = clientService.getClientById(id);
//		
//		ctx.json(client); // Takes any Java Object and serializes it into JSON
//		// Serialization means taking an object and converting it into some sendable data.
//		ctx.status(200);
//		
//	};
//	
//	
//
//	@Override
//	public void mapEndpoints(Javalin app) {
//		
//		app.get("/clients", getClients);
//		app.get("/clients/:id", getClientsById); // id is a path parameter 
//		
//		
//	}
//}
