package com.revature.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.dao.ClientRepository;
import com.revature.dto.PostClientDTO;
import com.revature.exceptions.AddClientException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.Client;

public class ClientServiceTest {
	
	// Fake repository dependency (mocked w/ Mockito)
	private static ClientRepository mockClientRepository;
	
	// The system under test, our ShipService instance
	public ClientService clientService;

	@BeforeClass
	public static void setUp() throws DatabaseException, AddClientException {
		mockClientRepository = mock(ClientRepository.class);
		
		when(mockClientRepository.getClientById(eq("1000"))).thenReturn(null);
		
		// Client that doesn't exist
		Client fakeClient = new Client(1, "Fake", "Client");
		fakeClient.setAccounts(new ArrayList<>());
		when(mockClientRepository.addClient(new PostClientDTO("Fake", "Client"))).thenReturn(fakeClient);
	
		// Real Client
		Client realClient = new Client(2, "John", "Smith");
		realClient.setAccounts(new ArrayList<>());
		when(mockClientRepository.getClientById(eq("2"))).thenReturn(realClient);
		
	}
	
	// For before every single test we get a fresh clientService with our mock object being passed to it
	@Before
	public void beforeTest() {
		clientService = new ClientService(mockClientRepository); // Before each test, instantiate a new client service,
		// passing in our fake ship repository object
	}
	
	@Test
	public void test_BlankFirstNameAndBlankLastName() throws  DatabaseException, AddClientException {
		PostClientDTO blankNameDTO = new PostClientDTO("", "");	
		try {
			clientService.addClient(blankNameDTO);
			fail("Add client exception did not occur");
		} catch (AddClientException e) {
			assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
		}
	}	
	
	@Test
	public void test_BlankFirstNameAndBlankLastNameWithSpaces() throws  DatabaseException, AddClientException {
		PostClientDTO blankNameDTO = new PostClientDTO("   ", "  ");	
		try {
			clientService.addClient(blankNameDTO);
			fail("Add client exception did not occur");
		} catch (AddClientException e) {
			assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
		}
	}
	
	@Test
	public void test_BlankFirstNameNonBlankLastName() throws  DatabaseException, AddClientException {
		PostClientDTO blankNameDTO = new PostClientDTO("", "smith");	
		try {
			clientService.addClient(blankNameDTO);
			fail("Add client exception did not occur");
		} catch (AddClientException e) {
			assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
		}
	}
	
	@Test
	public void test_BlankLastNameNonBlankFirstName() throws  DatabaseException, AddClientException {
		PostClientDTO blankNameDTO = new PostClientDTO("John", "");	
		try {
			clientService.addClient(blankNameDTO);
			fail("Add client exception did not occur");
		} catch (AddClientException e) {
			assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
		}
	}
	
	@Test
	public void test_BlankFirstNameWithSpacesAndNonBlankLastName() throws  DatabaseException, AddClientException {
		PostClientDTO blankNameDTO = new PostClientDTO("     ", "smith");	
		try {
			clientService.addClient(blankNameDTO);
			fail("Add client exception did not occur");
		} catch (AddClientException e) {
			assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
		}
	}
	
	@Test
	public void test_NonBlankFirstNameAndBlankLastNameWithSpaces() throws  DatabaseException, AddClientException {
		PostClientDTO blankNameDTO = new PostClientDTO("John", "     ");	
		try {
			clientService.addClient(blankNameDTO);
			fail("Add client exception did not occur");
		} catch (AddClientException e) {
			assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
		}
	}
	
	@Test
	public void test_BlankLastName_BlankFirstNameWithSpacesFor() throws  DatabaseException, AddClientException {
		PostClientDTO blankNameDTO = new PostClientDTO("John", "");	
		try {
			clientService.addClient(blankNameDTO);
			fail("Add client exception did not occur");
		} catch (AddClientException e) {
			assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
		}
	}
	
	@Test(expected=ClientNotFoundException.class)
	public void test_getClientById_idNotFoundOf99() throws DatabaseException, BadParameterException, ClientNotFoundException {
		Client actual = clientService.getClientById("99");	
	}
	
	@Test(expected=BadParameterException.class) 
	public void test_getClientById_idNotFormattedCorrectly() throws BadParameterException, ClientNotFoundException, DatabaseException{
		Client actual = clientService.getClientById("abc");
	}
	
	@Test
	public void test_exceptionMessageFor_getClientById_idNotFormattedCorrectly() throws ClientNotFoundException, DatabaseException {
		try {
			clientService.getClientById("abc");
			
			fail("Exception did not occur");
		} catch (BadParameterException e) {
			assertEquals("Client id must be an int. User provided abc", e.getMessage());
		}
	}
	

	

}
