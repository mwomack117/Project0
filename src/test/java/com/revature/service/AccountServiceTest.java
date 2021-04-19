package com.revature.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;

import java.sql.Connection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.revature.dao.AccountRepository;
import com.revature.dto.PostAccountDTO;
import com.revature.exceptions.AddAccountException;
import com.revature.exceptions.AddClientException;
import com.revature.exceptions.BadParameterException;
import com.revature.exceptions.DatabaseException;
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
	public void test_happyPath() throws BadParameterException, DatabaseException, AddAccountException {

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
	public void test_blankAccountName_WithoutSpaces() throws BadParameterException, DatabaseException {
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
	public void test_blankAccountName_WithSpaces() throws BadParameterException, DatabaseException  {

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
	public void test_AccountBalance_GreateThanZero() throws BadParameterException, DatabaseException  {

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
	public void test_nonInteger_AccountId() throws AddAccountException, DatabaseException  {
		try(MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			
			try {
				accountService.addAccount("abc", new PostAccountDTO("Checking", 1000));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Client id must be an int. User provided " + "abc");
			}
			
		}
	}
	
	@Test
	public void test_nonFloat_AccountBalance() throws AddAccountException, DatabaseException  {
		try(MockedStatic<ConnectionUtil> mockedConnectionUtil = mockStatic(ConnectionUtil.class)) {
			mockedConnectionUtil.when(ConnectionUtil::getConnection).thenReturn(mockConnection);
			
			try {
				accountService.addAccount("abc", new PostAccountDTO("Checking", 1000));
				fail("BadParameterException was not thrown");
			} catch (BadParameterException e) {
				assertEquals(e.getMessage(), "Client id must be an int. User provided " + "abc");
			}
			
		}
	}

}
