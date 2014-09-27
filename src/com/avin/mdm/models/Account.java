package com.avin.mdm.models;

public class Account {
	    private int id;
	    private String emailId;
	    private String password;
	    private String cloudId;
	    private String firstName;
	    private String lastName;
	    
	    public Account(){
	    	
	    }

		public String getEmailId() {
			return emailId;
		}

		public void setEmailId(String emailId) {
			this.emailId = emailId;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getCloudId() {
			return cloudId;
		}

		public void setCloudId(String cloudId) {
			this.cloudId = cloudId;
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

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	    
	    
}
