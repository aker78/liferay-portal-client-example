Liferay Portal Client Example
=====================================
Welcome dear readers!

A few days ago, I made ​​a simple example of using the Liferay Client Library. The project is based on Maven. Below are shown the steps needed to perform a test. I remember that the operations performed by the sample program are:

* Performs login on Liferay;
* Retrieves the CompanyID based on the virtualhost;
* Retrieve UserId of the user to ScreenName and CompanyID;
* Retrieves the GroupId of the site Guest;
* Performs the upload of a document on the Document Library site Guest.

	$ git clone git://github.com/amusarra/liferay-portal-client-example.git
	$ cd liferay-portal-client-example/
	$ mvn package
List 1. Clone repository and build the package

	$ cd target/
	$ java -jar portal-client-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar
List 2. Run the portal client example
	
	[00:18:15,816 INFO UploadDocumentOnDL]: Try lookup User Service by End Point: http://will:will@localhost:8080/api/secure/axis/Portal_UserService...
	[00:18:16,176 INFO UploadDocumentOnDL]: Try lookup Company Service by End Point: http://will:will@localhost:8080/api/secure/axis/Portal_CompanyService...
	[00:18:16,343 INFO UploadDocumentOnDL]: Get UserID...
	[00:18:16,378 INFO UploadDocumentOnDL]: UserId for user named will is 11801
	[00:18:16,378 INFO UploadDocumentOnDL]: Try lookup Group Service by End Point: http://will:will@localhost:8080/api/secure/axis/Portal_GroupService...
	[00:18:16,441 INFO UploadDocumentOnDL]: Found the group Guest (GroupId: 19) to publish the document
	[00:18:16,441 INFO UploadDocumentOnDL]: Try lookup DL App Service by End Point: http://will:will@localhost:8080/api/secure/axis/Portlet_DL_DLAppService...
	[00:18:16,922 INFO UploadDocumentOnDL]: The file BenchmarkingSugarOnSolaris-SugarCon2009.pdf has been correctly added to liferay
	[00:18:16,922 INFO UploadDocumentOnDL]: File Id:13101
	[00:18:16,922 INFO UploadDocumentOnDL]: File Size: 735142
	[00:18:16,922 INFO UploadDocumentOnDL]: File Version: 1.0
List 3. Show the performed tasks.

![Figure 1 – List View of the Document Library](http://musarra.files.wordpress.com/2013/05/screen-shot-2013-05-23-at-23-59-20.png)
![Figure 2 – Detail View of the document inserted by SOAP Services](http://musarra.files.wordpress.com/2013/05/screen-shot-2013-05-23-at-23-59-45.png)
