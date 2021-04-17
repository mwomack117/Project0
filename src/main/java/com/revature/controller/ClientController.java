package com.revature.controller;

import java.util.ArrayList;

import com.revature.dto.PostClientDTO;
import com.revature.model.Client;
import com.revature.service.ClientService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class ClientController implements Controller {

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
		String id = ctx.pathParam("id");
		
		Client client = this.clientService.getClientById(id);
		
		ctx.json(client);
		ctx.status(200);
	};

	private Handler addClient = ctx -> {
		PostClientDTO clientDTO = ctx.bodyAsClass(PostClientDTO.class);

		Client client = this.clientService.addClient(clientDTO);

		ctx.json(client);
		ctx.status(201);
	};
	
	//not working
	private Handler updateClientById = ctx -> {
		String id = ctx.pathParam("id");
		
		PostClientDTO clientDTO = ctx.bodyAsClass(PostClientDTO.class);
		
		Client client = this.clientService.updateClient(id, clientDTO);
		
		ctx.json(client);
		ctx.status(201);
	};

	@Override
	public void mapEndpoints(Javalin app) {
		app.get("/clients", getClients);
		app.get("/clients/:id", getClientById); 
		app.post("/clients", addClient);
		app.put("/clients/:id", updateClientById);
		
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
