package com.hub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.GET; 
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.server.ResourceConfig;
@Path("/")
public class SMSHub { 
	private String appName = "SMSHub";
	private Client client = ClientBuilder.newClient();
	final ResourceConfig resourceConfig = new ResourceConfig()
	.packages("com.hub")
	.register(GsonMessageBodyHandler.class);

	private HashSet<String> hubLoginTokens = new HashSet<String>();
	public SMSHub() {
		client.register(GsonMessageBodyHandler.class);
	}

	private Response makeCORS(ResponseBuilder req) {
		ResponseBuilder rb = req.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

		return rb.build();
	}
	
	@OPTIONS
	@Path("*")
	public Response corsMyResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		return makeCORS(Response.ok("CHECKOUT,CONNECT,COPY,DELETE,GET,HEAD,LOCK,M-SEARCH,MERGE,MKACTIVITY,MKCALENDAR,MKCOL,MOVE,NOTIFY,PATCH,POST,PROPFIND,PROPPATCH,PURGE,PUT,REPORT,SEARCH,SUBSCRIBE,TRACE,UNLOCK,UNSUBSCRIBE"));
	}
	
	@GET 
	public Response getDefaultRoute(){ 

		return Response.seeOther(URI.create("https://www.google.ca/")).build();
	} 

	@POST 
	@Path("SMSHub/notifications") 
	public Response getNotification(@QueryParam("hubLoginToken") String hubLoginToken){ 
		
		WebTarget webTarget = client.target("https://young-basin-29738.herokuapp.com/getCountForUser?token=" + hubLoginToken);
		Notification response = webTarget.request().get(Notification.class);

		if (response != null)
		{
			//Timer timer = new Timer();
			//timer.schedule(new TimerTask() {
			//	  @Override
			//	  public void run() {
			//	    // Your database code here
			//		  String auth = "jodonnell@broadsoft.com";
			//			WebTarget webTarget = client.target("https://core.broadsoftlabs.com/v1/" + appName + "/" + auth + "/push");
			//			webTarget.request().post(Entity.json(null));
            //
			//	  }
			//	}, 30000);
			return Response.ok(JSONGenerator.generateJson(response), MediaType.APPLICATION_JSON).build(); 			
		}
		else
		{
			return Response.serverError().build();
		}
	}  

	@GET 
	@Path("SMSHub/authenticate")
	public Response getAuthentication(@QueryParam("hubLoginToken") String hubLoginToken,
			@QueryParam("hubUrl") String hubUrl){ 
		// Saving the hubLoginToken since the login is not completed yet.
		// The login will be completed when entering the /login route.
		//hubLoginTokens.add(hubLoginToken);
		//return Response.seeOther(URI.create("https://zipwhip-frontend.herokuapp.com/login?token=" + hubLoginToken)).build();
		
		String auth = "jodonnell@broadsoft.com";
		WebTarget webTarget = client.target("https://core.broadsoftlabs.com/v1/" + appName + "/" + auth + "/auth");
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("hubLoginToken", hubLoginToken);
		formData.add("auth", auth);
		AuthenticationSuccessResponse response = webTarget.request().post(Entity.form(formData), AuthenticationSuccessResponse.class);
        
		if (response != null && response.getUrl() != null)
		{
			try {
				return Response.seeOther(new URI(response.getUrl())).build();
			} catch (URISyntaxException e) {
				return Response.serverError().build();
			}				
		}
		else
		{
			return Response.serverError().build();
		}
	} 
	
	@POST 
	@Path("SMSHub/login")
	public Response login(@QueryParam("token") String hubLoginToken){
		
		// Verify if we received this token via the /authentication route before.
		// If so, then we login. However, if we did not receive this token before,
		// then we consider it as invalid and return an error.
		if (hubLoginTokens.contains(hubLoginToken))
		{
			String temporaryAuth = "temporaryAuth";
			WebTarget webTarget = client.target("https://core.broadsoftlabs.com/v1/" + appName + "/" + temporaryAuth + "/auth");
			MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
			formData.add("hubLoginToken", hubLoginToken);
			formData.add("auth", temporaryAuth);
			AuthenticationSuccessResponse response = webTarget.request().post(Entity.form(formData), AuthenticationSuccessResponse.class);

			try 
			{
				// We no longer need to keep the hubLoginToken since the authentication is completed.
				hubLoginTokens.remove(hubLoginToken);
				if (response != null && response.getUrl() != null)
				{
					return Response.seeOther(new URI(response.getUrl())).build();				
				}
				else
				{
					return Response.serverError().build();
				}
			} catch (URISyntaxException e) {
				return Response.serverError().build();
			} 
		}
		else
		{
			return Response.serverError().build();
		}
	} 
}