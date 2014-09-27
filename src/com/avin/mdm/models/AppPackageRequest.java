package com.avin.mdm.models;

import java.util.List;

public class AppPackageRequest {
	List<AppPackage> appPackages;
	private Account account;
	
	public AppPackageRequest(){
		
	}

	public List<AppPackage> getAppPackages() {
		return appPackages;
	}

	public void setAppPackages(List<AppPackage> appPackages) {
		this.appPackages = appPackages;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	
}
