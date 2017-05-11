This projects implements the Hub API for the SMSHub app.

The following libraries are used:
- gson
- Jersey Framework 
All the jars can be found in \SMSHub\WebContent\WEB-INF\lib

This is an java project that can be imported in eclipse.
To generate the war file, do this in eclipse:

File -> Export -> Web -> WAR File
  Select SMSHub as the web project and choose a destination for the war file.

The war file can then be deployed on heroku like this:

heroku war:deploy C:\Path\to\war\SMSHub.war --app <app-name>