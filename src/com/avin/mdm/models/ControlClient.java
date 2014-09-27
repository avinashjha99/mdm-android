package com.avin.mdm.models;

public class ControlClient {

public static final String CONTROL_CLIENT_KEY= "controlClient";
public static final String FIELD_NAME_COMMANDTYPE= "commandType";
public static final String FIELD_NAME_JSON_COMMAND_DETAILS= "jsonCommandDetails";

public static final String COMMAND_TYPE_KEY= "commandType";
public static final int UNINSTALL_APP_CONTROL= 1000;
public static final int INSTALL_APP_CONTROL= 1001;
public static final int LOCK_PHONE_CONTROL= 1002;
    
    private int commandType;
    private String jsonCommandDetails;

    public ControlClient(){
        
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

    public String getJsonCommandDetails() {
        return jsonCommandDetails;
    }

    public void setJsonCommandDetails(String jsonCommandDetails) {
        this.jsonCommandDetails = jsonCommandDetails;
    }
    
	
}
