package com.revature.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
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
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.revature.dao.ClientRepository;
import com.revature.dto.PostAccountDTO;
import com.revature.dto.PostClientDTO;
import com.revature.exceptions.AddAccountException;
import com.revature.exceptions.AddClientException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;
import com.revature.exceptions.DatabaseException;
import com.revature.model.Account;
import com.revature.model.Client;
import com.revature.util.ConnectionUtil;

public class ClientServiceTest {

	// Fake repository dependency (mocked w/ Mockito)
	private static ClientRepository mockClientRepository;

	// The system under test, our ShipService instance
	private ClientService clientService;
	private static Connection mockConnection;

	@BeforeClass
	public static void setUp() throws DatabaseException, AddClientException {
		mockClientRepository = mock(ClientRepository.class);

		// Client with id of 1000 does not exist
		when(mockClientRepository.getClientById(eq("1000"))).thenReturn(null);

		// Mock client
		Client mockClient = new Client(1, "John", "Smith");
		mockClient.setAccounts(new ArrayList<>());
		when(mockClientRepository.addClient(new PostClientDTO("John", "Smith"))).thenReturn(mockClient);
		when(mockClientRepository.getClientById(eq("1"))).thenReturn(mockClient);

		Client mockClient2 = new Client(2, "Joe", "Jones");
		mockClient.setAccounts(new ArrayList<>());
		when(mockClientRepository.addClient(new PostClientDTO("Joe", "Jones"))).thenReturn(mockClient);
		when(mockClientRepository.getClientById(eq("2"))).thenReturn(mockClient2);

		// Real Client
		// Client client = new Client(2, "John", "Smith");
		// client.setAccounts(new ArrayList<>());

	}

	// For before every single test we get a fresh clientService with our mock
	// object being passed to it
	@Before
	public void beforeTest() {
		clientService = new ClientService(mockClientRepository); // Before each test, instantiate a new client service,
		// passing in our fake client repository object
	}

	@Test
	public void test_happyPath() throws BadParameterException, DatabaseException, AddAccountException,
			InvalidFormatException, NullPointerException, AddClientException {

		// try-with resources
		// Syntactic sugar for creating some sort of object, in this case,
		// mockedConnectionUtil
		// and then calling .close() on that object whenever the block of code is
		// finished
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			Client actual = clientService.addClient(new PostClientDTO("John", "Smith"));

			Client expected = new Client(1, "John", "Smith");

			assertEquals(expected, actual);
		}

	}

	@Test
	public void test_getAllClients_ReturnsArrayList() throws DatabaseException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			ArrayList<Client> clients = (ArrayList<Client>) clientService.getAllClients();
			System.out.println(clients);
			assertNotNull(clients);
		}
	}

	@Test
	public void test_ReturnsArrayList_NotNull() throws DatabaseException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			ArrayList<Client> clientsExpected = new ArrayList<Client>(2);
			clientsExpected.add(new Client(1, "John", "Smith"));
			clientsExpected.add(new Client(2, "Joe", "Jones"));
			assertNotNull(clientsExpected);
		}

	}

	@Test
	public void test_GetClientById_ClientIdDoesntExist() throws DatabaseException, BadParameterException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			try {
				clientService.getClientById("1000");
				fail("Exception did not happen");
			} catch (ClientNotFoundException e) {
				assertEquals(e.getMessage(), "Client with id of 1000 was not found");
			}

		}
	}

	@Test
	public void test_BlankFirstNameAndBlankLastName() throws DatabaseException, AddClientException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO blankNameDTO = new PostClientDTO("", "");
			try {
				clientService.addClient(blankNameDTO);
				fail("Add client exception did not occur");
			} catch (AddClientException e) {
				assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
			}

		}
	}

	@Test
	public void test_BlankFirstNameAndBlankLastNameWithSpaces() throws DatabaseException, AddClientException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO blankNameDTO = new PostClientDTO("   ", "  ");
			try {
				clientService.addClient(blankNameDTO);
				fail("Add client exception did not occur");
			} catch (AddClientException e) {
				assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
			}

		}
	}

	@Test
	public void test_BlankFirstNameNonBlankLastName() throws DatabaseException, AddClientException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO blankNameDTO = new PostClientDTO("", "smith");
			try {
				clientService.addClient(blankNameDTO);
				fail("Add client exception did not occur");
			} catch (AddClientException e) {
				assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
			}
		}
	}

	@Test
	public void test_BlankLastNameNonBlankFirstName() throws DatabaseException, AddClientException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO blankNameDTO = new PostClientDTO("John", "");
			try {
				clientService.addClient(blankNameDTO);
				fail("Add client exception did not occur");
			} catch (AddClientException e) {
				assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
			}

		}
	}

	@Test
	public void test_BlankFirstNameWithSpacesAndNonBlankLastName() throws DatabaseException, AddClientException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO blankNameDTO = new PostClientDTO("     ", "smith");
			try {
				clientService.addClient(blankNameDTO);
				fail("Add client exception did not occur");
			} catch (AddClientException e) {
				assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
			}

		}
	}

	@Test
	public void test_NonBlankFirstNameAndBlankLastNameWithSpaces() throws DatabaseException, AddClientException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO blankNameDTO = new PostClientDTO("John", "     ");
			try {
				clientService.addClient(blankNameDTO);
				fail("Add client exception did not occur");
			} catch (AddClientException e) {
				assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
			}
		}
	}

	@Test
	public void test_BlankLastName_BlankFirstNameWithSpacesFor() throws DatabaseException, AddClientException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO blankNameDTO = new PostClientDTO("John", "");
			try {
				clientService.addClient(blankNameDTO);
				fail("Add client exception did not occur");
			} catch (AddClientException e) {
				assertEquals(e.getMessage(), "User tried to add a client with a blank firstname and/or lastname");
			}
		}
	}

	@Test(expected = ClientNotFoundException.class)
	public void test_getClientById_idNotFoundOf1000()
			throws DatabaseException, BadParameterException, ClientNotFoundException {
		clientService.getClientById("1000");
	}

	@Test
	public void test_getClientById_idNotFormattedCorrectly()
			throws ClientNotFoundException, DatabaseException, BadParameterException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			try {
				clientService.getClientById("abc");

				fail("Exception did not occur");
			} catch (BadParameterException e) {
				assertEquals("Client id must be an int. User provided abc", e.getMessage());
			}
		}
	}

	@Test
	public void testUpdateCient_IdDoesNotExist() throws DatabaseException, BadParameterException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO noClientDTO = new PostClientDTO("John", "Smith");
			try {
				clientService.updateClient("1000", noClientDTO);
			} catch (ClientNotFoundException e) {
				assertEquals("Client with id of 1000 was not found", e.getMessage());
			}
		}
	}

	@Test
	public void testUpdateCient_ClientIdInvalidFormat()
			throws DatabaseException, BadParameterException, ClientNotFoundException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			PostClientDTO noClientDTO = new PostClientDTO("John", "Smith");
			try {
				clientService.updateClient("asdf", noClientDTO);
			} catch (BadParameterException e) {
				assertEquals("Client id must be an int. User provided asdf", e.getMessage());
			}
		}
	}

	@Test
	public void testDeleteClient_IdDoesNotExist() throws DatabaseException, BadParameterException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			try {
				clientService.deleteClient("500");

				fail("ExceptionDidNotOccur");
			} catch (ClientNotFoundException e) {
				assertEquals("Client with id of 500 was not found", e.getMessage());
			}

		}
	}

	@Test
	public void test_deleteClientById_idNotFormattedCorrectly()
			throws ClientNotFoundException, DatabaseException, BadParameterException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			try {
				clientService.getClientById("abc");

				fail("Exception did not occur");
			} catch (BadParameterException e) {
				assertEquals("Client id must be an int. User provided abc", e.getMessage());
			}
		}
	}

}
