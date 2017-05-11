This projects implements the Hub API for the SMSHub app.

The following libraries are used:
- gson
- Jersey Framework 
All the jars can be found in \SMSHub\WebContent\WEB-INF\lib

This is an java project that can be imported in eclipse.

Download eclipse: https://www.eclipse.org/downloads/

Start eclipse and enter a workspace path.
Go to Help menu->Install new software 
In the available choices, select the one that looks like this http://download.eclipse.org/releases/neon
Check the checkbox next to "Web, XML, Java EE ..." and click finish to install the plugin.

To import the project, go to File -> Import -> General -> Existing Project into Workspace
next
In "Select root repository", set the path to the SMSHub project folder (the folder which contains the file .project)

The project should compile now.

To generate the war file, do this in eclipse:

File -> Export -> Web -> WAR File
  Select SMSHub as the web project and choose a destination for the war file.

The war file can then be deployed on heroku like this:

heroku war:deploy C:\Path\to\war\SMSHub.war --app <app-name>