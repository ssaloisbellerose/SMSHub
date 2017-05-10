package com.hub;

public class AuthenticationSuccessResponse {
	private String url;
	public AuthenticationSuccessResponse()
	{
		
	}
	public AuthenticationSuccessResponse(String url)
	{
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
