package com.hub;

public class NotificationRequestContent {
	private String csrf;
	private String auth;
	private String username;
	public NotificationRequestContent(){} 

	public NotificationRequestContent(String csrf, String auth, String username){  
		this.csrf = csrf;
		this.auth = auth;
		this.username = username;
	} 

	public String getCsrf() {
		return csrf;
	}

	public void setCsrf(String csrf) {
		this.csrf = csrf;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
} 

