package com.revature.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.revature.dao.AccountRepository;
import com.revature.dto.PostAccountDTO;
import com.revature.exceptions.AddAccountException;
import com.revature.exceptions.AddClientException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.ClientNotFoundException;
import com.revature.exceptions.DatabaseException;
import com.revature.exceptions.NoAccountsException;
import com.revature.model.Account;
import com.revature.util.ConnectionUtil;

public class AccountServiceTest {

	private static AccountRepository mockAccountRepository;
	private static Connection mockConnection;

	private AccountService accountService;

	@BeforeClass
	public static void setUp() throws DatabaseException {
		mockAccountRepository = mock(AccountRepository.class);
		mockConnection = mock(Connection.class);

		when(mockAccountRepository.addAccount(eq(1), eq(new PostAccountDTO("Checking", 1000))))
				.thenReturn(new Account(1, "Checking", 1000));

	}

	@Before
	public void beforeTest() {
		accountService = new AccountService(mockAccountRepository);
	}

	@Test
	public void test_happyPath() throws BadParameterException, DatabaseException, AddAccountException,
			InvalidFormatException, NullPointerException {

		// try-with resources
		// Syntactic sugar for creating some sort of object, in this case,
		// mockedConnectionUtil
		// and then calling .close() on that object whenever the block of code is
		// finished
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			Account actual = accountService.addAccount("1", new PostAccountDTO("Checking", 1000));

			Account expected = new Account(1, "Checking", 1000);

			assertEquals(expected, actual);
		}

	}

	@Test
	public void test_blankAccountName_WithoutSpaces_AddAccount()
			throws BadParameterException, DatabaseException, InvalidFormatException, NullPointerException {
		// We are mocking the static method getConnection, because we don't actually
		// want to execute the real
		// Method. If we actually execute the real method, that would mean we are
		// contacting the actual database
		// If our test environment does not have access to the real database, then we
		// would enter into a SQLException
		// situation for our tests, which will not be great
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.addAccount("1", new PostAccountDTO("", 1000));
				fail("AddPirateException was not thrown");
			} catch (AddAccountException e) {
				assertEquals(e.getMessage(), "Account name cannot be blank");
			}

		}
	}

	@Test
	public void test_blankAccountName_WithSpaces_AddAccount()
			throws BadParameterException, DatabaseException, InvalidFormatException, NullPointerException {

		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.addAccount("1", new PostAccountDTO("    ", 1000));
				fail("AddAccountException was not thrown");
			} catch (AddAccountException e) {
				assertEquals(e.getMessage(), "Account name cannot be blank");
			}

		}
	}

	@Test
	public void test_AccountBalance_IsNotLessThanZero_AddAccount()
			throws BadParameterException, DatabaseException, InvalidFormatException, NullPointerException {

		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.addAccount("1", new PostAccountDTO("Checking", -1000));
				fail("AddAccountException was not thrown");
			} catch (AddAccountException e) {
				assertEquals(e.getMessage(), "Account balance cannot be a negative number");
			}

		}
	}

	@Test
	public void test_GetSingleAccountBy_AccountIdNotExist()
			throws AddAccountException, DatabaseException, NoAccountsException, BadParameterException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.getAccountById("500");
				fail("NoAccountsException was not thrown");
			} catch (NoAccountsException e) {
				assertEquals(e.getMessage(), "Account with id of 500 was not found");
			}

		}
	}

	@Test
	public void test_GetAllAccountsByClientId_NoAccountsExist() throws AddAccountException, DatabaseException,
			NoAccountsException, BadParameterException, SQLException, ClientNotFoundException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.getAccountsByClientId("2");
				fail("NoAccountsException was not thrown");
			} catch (NoAccountsException e) {
				assertEquals(e.getMessage(), "This client has no accounts yet");
			}
		}

	}

	@Test
	public void test_DeleteAccountById_NoAccountExists() throws BadParameterException, DatabaseException, SQLException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			
			try {
				accountService.deleteAccount("2");
				fail("NoAccountsException was not thrown");
			} catch (NoAccountsException e) {
				assertEquals(e.getMessage(), "Account with id of 2 was not found");
			}
		}
	}
	
	@Test
	public void test_DeleteAccountById_AccountIdInvalidFomat() throws BadParameterException, DatabaseException, SQLException {
		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			
			try {
				accountService.deleteAccount("2");
				fail("NoAccountsException was not thrown");
			} catch (NoAccountsException e) {
				assertEquals(e.getMessage(), "Account with id of 2 was not found");
			}
		}
	}
	
	@Test
	public void test_AccountUpdate_AccountIdDoesNotExist()
			throws BadParameterException, DatabaseException, InvalidFormatException, NullPointerException {

		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.updateAccount("2",  new PostAccountDTO("Checking", 1000));
				fail("NoAccountsException was not thrown");
			} catch (NoAccountsException e) {
				assertEquals(e.getMessage(), "Account with id of 2 was not found");
			}

		}
	}
	
	@Test
	public void test_AccountUpdate_AccountIdInvalidFormat()
			throws BadParameterException, DatabaseException, InvalidFormatException, NullPointerException, NoAccountsException {

		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.updateAccount("abc",  new PostAccountDTO("Checking", 1000));
				fail("NoAccountsException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Account id must be an int. User provided abc");
			}

		}
	}
	
	@Test
	public void test_AccountUpdate_BlankAccountName_WithSpaces()
			throws BadParameterException, DatabaseException, InvalidFormatException, NullPointerException {

		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.addAccount("1", new PostAccountDTO("    ", 1000));
				fail("UpdateAccountException was not thrown");
			} catch (AddAccountException e) {
				assertEquals(e.getMessage(), "Account name cannot be blank");
			}

		}
	}
	
	@Test
	public void test_AccountUpdate_BlankAccountName_WithOutSpaces()
			throws BadParameterException, DatabaseException, InvalidFormatException, NullPointerException {

		try (MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);

			try {
				accountService.addAccount("1", new PostAccountDTO("", 1000));
				fail("UpdateAccountException was not thrown");
			} catch (AddAccountException e) {
				assertEquals(e.getMessage(), "Account name cannot be blank");
			}

		}
	}

}
