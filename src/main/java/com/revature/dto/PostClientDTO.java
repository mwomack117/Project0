package com.revature.dto;

public class PostClientDTO {
	
	private String firstName;
	private String lastName;
	
	public PostClientDTO() {
		super();
	}
	
	public PostClientDTO(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}


	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	

}
