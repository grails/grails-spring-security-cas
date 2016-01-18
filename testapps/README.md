To test the plugin, first run `grails install` to package the plugin and copy to mavenLocal().

* Unpack `server.tar.gz`. This is a trimmed-down Tomcat 8.0.26 configured to run on port 9090 and with no webapps.
* Copy the war into the webapps directory and rename to `cas.war`. It's a regular CAS war with changes to enable SAML attribute release and disable the requirement for HTTPS on SSO sessions.  Check out the Maven overlay in `build-cas-server` to see the changes that were done or build the WAR file yourself.
* Run the CAS server with `bin/startup.sh` and the three test apps with `grails run-app`.

The test script is simple:

* Navigate to either app's secure controller (http://localhost:8081/secure/admins or http://localhost:8082/secure/admins) and login with admin/admin
* Navigate to the other server's secure controller and you should already be authenticated
* Navigate to the SAML-enabled app (http://localhost:8083/secure/admins) and you will be denied access because the SpringSecurity authorities are pulled directly from CAS, not the local application
* View all of the attributes that are being released through SAML (http://localhost:8083/secure/showAttributes)

The CAS server is configured With the following two accounts:
* username: user
* password: user

* username: admin
* password: admin
