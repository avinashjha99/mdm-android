package com.avin.mdm.models;

import java.util.Date;

public class CallRecord {
	
	private String phNumber;
    private String callType;
    private String callDate;
    private String callDuration ;
    
    public CallRecord(){
    	
    }

	public String getPhNumber() {
		return phNumber;
	}

	public void setPhNumber(String phNumber) {
		this.phNumber = phNumber;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getCallDate() {
		return callDate;
	}

	public void setCallDate(String callDate) {
		this.callDate = callDate;
	}

	public String getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}
    
    

}
