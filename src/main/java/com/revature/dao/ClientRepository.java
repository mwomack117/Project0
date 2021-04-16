package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.revature.dto.PostClientDTO;
import com.revature.exceptions.AddClientException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.Client;
import com.revature.util.ConnectionUtil;

public class ClientRepository {
	
	public Client getClientById(String clientId) throws DatabaseException  {
		Client client = null;
		try (Connection connection = ConnectionUtil.getConnection()) {
			String sql = "SELECT * FROM clients c WHERE c.id = ?";
			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			
			pstmt.setString(1, clientId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				int id = rs.getInt("id");
				String retrievedFirstName = rs.getString("first_name");
				String retrievedLastName = rs.getString("last_name");
				client = new Client(id, retrievedFirstName, retrievedLastName);		
			}
			
			return client;
			
			
		} catch (SQLException e) {
			throw new DatabaseException("Something went wrong when trying to get a connection. "
					+ "Exception message: " + e.getMessage());
		}
	}

	public ArrayList<Client> getAllClients() throws DatabaseException {
		ArrayList<Client> clients = new ArrayList<Client>();
		try (Connection connection = ConnectionUtil.getConnection()) {
			String sql = "SELECT * from clients c";
			
			PreparedStatement pstmt = connection.prepareStatement(sql);
			
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int clientId = rs.getInt("id");
				String retrievedFirstName = rs.getString("first_name");
				String retrievedLastName = rs.getString("last_name");
				Client client = new Client(clientId, retrievedFirstName, retrievedLastName);
				clients.add(client);
			}
			
		} catch (SQLException e) {
			throw new DatabaseException("Something went wrong wehen trying to get a connection. "
					+ "Exception message: " + e.getMessage());
		}
		return clients;
	}
	
	public Client addClient(PostClientDTO clientDTO) throws DatabaseException, AddClientException {
		try (Connection connection = ConnectionUtil.getConnection()) {
			String sql = "INSERT INTO clients (first_name, last_name) VALUES (?, ?)";
			
			PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setString(1, clientDTO.getFirstName());
			pstmt.setString(2, clientDTO.getLastName());
			
			int recordsAdded = pstmt.executeUpdate();
			
			if (recordsAdded != 1) {
				throw new DatabaseException("Couldn't add client to the database");
			}
			
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				int id = rs.getInt(1);
				Client newClient = new Client(id, clientDTO.getFirstName(), clientDTO.getLastName());
				newClient.setAccounts(new ArrayList<>());
				return newClient;
			} else {
				throw new DatabaseException("Client id was not generated, and therefore adding a client failed");
			}
			
		} catch (SQLException e) {
			throw new DatabaseException("Something went wrong when trying to get a connection. "
					+ "Exception message: " + e.getMessage());
		}
	}

//	public Ship getShipByName(String shipName) throws DatabaseException {
//		Ship ship = null;
//		try (Connection connection = ConnectionUtil.getConnection()) {
//			String sql = "SELECT * FROM ships s WHERE s.ship_name = ?";
//			
//			PreparedStatement pstmt = connection.prepareStatement(sql);
//			
//			pstmt.setString(1, shipName);
//			
//			ResultSet rs = pstmt.executeQuery();
//			
//			// Using if (rs.next()) instead of while, since we are retrieving only 1 result
//			if(rs.next()) {
//				int id = rs.getInt("id");
//				String retrievedShipName = rs.getString("ship_name");
//				ship = new Ship(id, retrievedShipName);
//			}
//			
//			return ship;
//			
//		} catch (SQLException e) {
//			throw new DatabaseException("Something went wrong when trying to get a connection. "
//					+ "Exception message: " + e.getMessage());
//		}
//	}
	
}


//	public Client getClientById(int id) {
//		
//		if (id == 1) {
//			return new Client("Michael", "Womack", 36);
//		}
//		return null;
//		
//	}
	// Perform database operations such as retrieving a client, updating a client's data, 
	// adding a new client, etc..

