package com.hub;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import javax.ws.rs.GET; 
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

	public SMSHub() {
		client.register(GsonMessageBodyHandler.class);
	}

	private Response makeCORS(ResponseBuilder req) {
		ResponseBuilder rb = req.header("Access-Control-Allow-Origin", "https://core.broadsoftlabs.com")
				.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
				.header("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token")
				.header("Access-Control-Allow-Credentials", "true");

		return rb.build();
	}

	@OPTIONS 
	@Path("SMSHub/notifications") 
	public Response notificationsOptions() {
		System.out.println("*** OPTIONS NOTIF");
		return makeCORS(Response.ok("CHECKOUT,CONNECT,COPY,DELETE,GET,HEAD,LOCK,M-SEARCH,MERGE,MKACTIVITY,MKCALENDAR,MKCOL,MOVE,NOTIFY,PATCH,POST,PROPFIND,PROPPATCH,PURGE,PUT,REPORT,SEARCH,SUBSCRIBE,TRACE,UNLOCK,UNSUBSCRIBE"));
	}

	@GET 
	public Response getDefaultRoute(){ 

		return Response.seeOther(URI.create("https://zipwhip-frontend.herokuapp.com/chat")).build();
	} 

	@POST 
	@Path("SMSHub/update") 
	public Response update(@QueryParam("number") String number){ 
		if (number == null)
			return Response.serverError().build();
		System.out.println("POST " + "https://core.broadsoftlabs.com/v1/" + appName + "/" + number + "/push");
		WebTarget webTarget = client.target("https://core.broadsoftlabs.com/v1/" + appName + "/" + number + "/push");
		Response resp = webTarget.request().post(Entity.json(null));
		System.out.println("PushResponseStatus " + (resp != null? resp.getStatus() : "null"));
		return Response.ok().build();
	}
	
	@POST 
	@Path("SMSHub/notifications") 
	public Response getNotification(String x, @QueryParam("authToken") String authToken){ 

		System.out.println("*** Notification content: " + x);
		System.out.println("*** Notification authtoken: " + authToken);
		NotificationRequestContent content = (NotificationRequestContent) JSONGenerator.generateTOfromJson(x, NotificationRequestContent.class);
		String number;
		if (content != null && content.getAuth() != null)
			number = content.getAuth();
		else
			number = authToken;

		System.out.println("*** Notification: auth " + number);
		if (number == null)
		{
			return Response.serverError().build();
		}

		try {
			number = URLEncoder.encode(number, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return Response.serverError().build();
		}

		System.out.println("*** Notification: POST TO " + "https://young-basin-29738.herokuapp.com/getCountForUser?number=" + number);
		WebTarget webTarget = client.target("https://young-basin-29738.herokuapp.com/getCountForUser?number=" + number);
		Notification response = webTarget.request().get(Notification.class);

		if (response != null)
		{
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

		return Response.seeOther(URI.create("https://zipwhip-frontend.herokuapp.com/login?token=" + hubLoginToken)).build();
	} 

	@POST 
	@Path("SMSHub/login")
	public Response login(@QueryParam("token") String hubLoginToken, @QueryParam("number") String number){
		if (hubLoginToken == null || number == null)
			return Response.serverError().build();
		
		WebTarget webTarget = client.target("https://core.broadsoftlabs.com/v1/" + appName + "/" + number + "/auth");
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("hubLoginToken", hubLoginToken);
		formData.add("auth", number);
		formData.add("authToken", number);
		AuthenticationSuccessResponse response = webTarget.request().post(Entity.form(formData), AuthenticationSuccessResponse.class);

		if (response != null && response.getUrl() != null)
		{
			System.out.println("Redirecting to " + response.getUrl());
			return Response.ok(JSONGenerator.generateJson(new RedirectResponse(response.getUrl())), MediaType.APPLICATION_JSON).build();				
		}
		else
		{
			return Response.serverError().build();
		}
	} 
}