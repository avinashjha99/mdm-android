package com.avin.mdm.models;

import java.util.List;

public class CallRecordRequest {
	List<CallRecord> callRecords;
	private Account account;
	
	public CallRecordRequest(){
		
	}


	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}


	public List<CallRecord> getCallRecords() {
		return callRecords;
	}


	public void setCallRecords(List<CallRecord> callRecords) {
		this.callRecords = callRecords;
	}
}
