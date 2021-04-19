package com.revature.service;

import java.util.ArrayList;

import com.revature.dao.ClientRepository;
import com.revature.dto.PostClientDTO;
import com.revature.exceptions.AddClientException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.Client;

public class ClientService {

	private ClientRepository clientRepository;

	public ClientService() {
		super();
		this.clientRepository = new ClientRepository();
	}

	// for mocking
	public ClientService(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	// Get ALL clients
	public ArrayList<Client> getAllClients() throws DatabaseException {
		ArrayList<Client> clients = clientRepository.getAllClients();

		return clients;
	}

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

	// Add a client
	public Client addClient(PostClientDTO clientDTO) throws DatabaseException, AddClientException {

		if (clientDTO.getFirstName().trim().equals("") || clientDTO.getLastName().trim().equals("")) {
			throw new AddClientException("User tried to add a client with a blank firstname and/or lastname");
		}

		return clientRepository.addClient(clientDTO);
	}

	// Update a client if they exist
	public Client updateClient(String clientId, PostClientDTO clientDTO)
			throws DatabaseException, BadParameterException, ClientNotFoundException {
		try {
			
			getClientById(clientId); // check that client id exists first
			
			Client client = clientRepository.updateClientById(clientId, clientDTO);
			
			return client;
		} catch (NumberFormatException e) {
			throw new BadParameterException("Client id must be an int. User provided " + clientId);
		}

	}

	public void deleteClient(String clientId) throws DatabaseException, BadParameterException, ClientNotFoundException {
		getClientById(clientId); // check that client id exists first
		clientRepository.deleteClientById(clientId);
	}

}

//package com.revature.service;
//
//import java.util.ArrayList;
//
//import com.revature.dao.ClientRepository;
//import com.revature.eceptions.NoClientsFoundException;
//import com.revature.exceptions.BadParameterException;
//import com.revature.exceptions.ClientNotFoundException;
//import com.revature.model.Client;
//
//public class ClientService {
//	
//	private ClientRepository clientRepository;
//	
//	public ClientService() {
//		this.clientRepository = new ClientRepository();
//	}
//	
//	// Normally we use the no-args in the normal functioning of our application
//	// But when we're testing, we need this constructor to "inject" our mock object into this service
//	public ClientService(ClientRepository clientRepository) {
//		this.clientRepository = clientRepository;
//	}
//	
//	// get all clients //
//	public ArrayList<Client> getAllClients() {
//		
//			ArrayList<Client> clients = clientRepository.getAllClients();
//			
//			// what to do if repository is empty??
////			if(clients.isEmpty()) {		
////			}
//	
//		return clients;
//		
//	}
//
//	public Client getClientById(String stringId) throws BadParameterException, ClientNotFoundException {
//		
//		try {
//			int id = Integer.parseInt(stringId);
//			
//			Client client = clientRepository.getClientById(id);
//			
//			if (client == null) {
//				throw new ClientNotFoundException("Client with id of " + id + " was not found");
//			}
//			
//			return client;
//		} catch (NumberFormatException e) {
//			throw new BadParameterException("Client id must be an int. User provided " + stringId);
//		}
//	}
//	
//}
