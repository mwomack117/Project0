package com.revature.model;

public class Account {

	private String accountName;
	private float balance;
	
	public Account() {
		super();		
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public Account(String accountName, float balance) {
		super();
		this.accountName = accountName;
		this.balance = balance;
	}

}
