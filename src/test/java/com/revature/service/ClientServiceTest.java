package com.revature.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.dao.ClientRepository;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;
import com.revature.model.Client;

public class ClientServiceTest {
	
	private static ClientRepository mockClientRepository;
	
	public ClientService clientService;

	@BeforeClass
	public static void setUp() {
		mockClientRepository = mock(ClientRepository.class);
		
		// Defining to the mock object what to respond with when it receives a parameter of 1 for int id
		when(mockClientRepository.getClientById(eq(1))).thenReturn(new Client("Michael", "Womack", 36));
		
		when(mockClientRepository.getClientById(eq(2))).thenReturn(new Client("John", "Doe", 100));
		
		when(mockClientRepository.getAllClients()).thenReturn(new ArrayList<Client>());
	}
	
	// For before every single test we get a fresh clientService with our mock object being passed to it
	@Before
	public void beforeEachTest() {
		clientService = new ClientService(mockClientRepository);
	}
		
	
	@Test
	public void test_getClientById_validIdOf1() throws BadParameterException, ClientNotFoundException {
		Client actual = clientService.getClientById("1");
		
		Client expected = new Client("Michael", "Womack", 36);
		
		assertEquals(expected, actual);
	}
	
	@Test(expected=ClientNotFoundException.class)
	public void test_getClientById_idNotFoundOf3() throws BadParameterException, ClientNotFoundException {
		Client actual = clientService.getClientById("3");
	}
	
	@Test(expected=BadParameterException.class) 
	public void test_getClientById_idNotFormattedCorrectly() throws BadParameterException, ClientNotFoundException{
		Client actual = clientService.getClientById("abc");
	}
	
	@Test
	public void test_exceptionMessageFor_getClientById_idNotFormattedCorrectly() throws ClientNotFoundException {
		try {
			clientService.getClientById("abc");
			
			fail("Exception did not occur");
		} catch (BadParameterException e) {
			assertEquals("Client id must be an int. User provided abc", e.getMessage());
		}
	}
	

	

}
