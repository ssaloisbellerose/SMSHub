package com.hub;

public class RedirectResponse {
	   private String url;
	   public RedirectResponse(){} 
	    
	   public RedirectResponse(String url){  
	      this.url = url;
	   }  
	   public String getUrl() { 
	      return url; 
	   }  

	   public void setUrl(String url) { 
	      this.url = url; 
	   }
}
