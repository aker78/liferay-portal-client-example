/**
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Copyright (c) 2009-2013 Antonio Musarra (antonio.musarra@gmail.com). All rights reserved. 
 */

package it.dontesta.liferay.example.portal.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.client.soap.portal.kernel.repository.model.FileEntrySoap;
import com.liferay.client.soap.portal.model.CompanySoap;
import com.liferay.client.soap.portal.model.GroupSoap;
import com.liferay.client.soap.portal.service.ServiceContext;
import com.liferay.client.soap.portal.service.http.CompanyServiceSoap;
import com.liferay.client.soap.portal.service.http.CompanyServiceSoapServiceLocator;
import com.liferay.client.soap.portal.service.http.GroupServiceSoap;
import com.liferay.client.soap.portal.service.http.GroupServiceSoapService;
import com.liferay.client.soap.portal.service.http.GroupServiceSoapServiceLocator;
import com.liferay.client.soap.portal.service.http.Portal_UserServiceSoapBindingStub;
import com.liferay.client.soap.portal.service.http.UserServiceSoap;
import com.liferay.client.soap.portal.service.http.UserServiceSoapServiceLocator;
import com.liferay.client.soap.portlet.documentlibrary.service.http.DLAppServiceSoap;
import com.liferay.client.soap.portlet.documentlibrary.service.http.DLAppServiceSoapServiceLocator;

public class UploadDocumentOnDL {

	static final Logger LOGGER = LoggerFactory
			.getLogger(UploadDocumentOnDL.class);

	static final String LIFERAY_USER_NAME = "will";
	static final String LIFERAY_USER_PASSWORD = "will";

	static final long LIFERAY_FOLDER_ID = 0L;
	static final String LIFERAY_PUBLISH_SITE = "Guest";

	static final String DL_DLAPP_SERVICE = "Portlet_DL_DLAppService";
	static final String USER_SERVICE = "Portal_UserService";
	static final String COMPANY_SERVICE = "Portal_CompanyService";
	static final String GROUP_SERVICE = "Portal_GroupService";

	static final String FILE_TO_UPLOAD = "/Users/amusarra/Documents/xml_oracle.pdf";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			URL userServiceEndPoint = _getURL(LIFERAY_USER_NAME,
					LIFERAY_USER_PASSWORD, USER_SERVICE);
			URL dlAppServiceEndPoint = _getURL(LIFERAY_USER_NAME,
					LIFERAY_USER_PASSWORD, DL_DLAPP_SERVICE);
			URL companyServiceEndPoint = _getURL(LIFERAY_USER_NAME,
					LIFERAY_USER_PASSWORD, COMPANY_SERVICE);
			URL groupServiceEndPoint = _getURL(LIFERAY_USER_NAME,
					LIFERAY_USER_PASSWORD, GROUP_SERVICE);

			LOGGER.info("Try lookup User Service by End Point: "
					+ userServiceEndPoint + "...");
			UserServiceSoapServiceLocator locatorUser = new UserServiceSoapServiceLocator();
			UserServiceSoap userService = locatorUser
					.getPortal_UserService(userServiceEndPoint);

			((Portal_UserServiceSoapBindingStub) userService)
					.setUsername(LIFERAY_USER_NAME);
			((Portal_UserServiceSoapBindingStub) userService)
					.setPassword(LIFERAY_USER_PASSWORD);

			LOGGER.info("Try lookup Company Service by End Point: "
					+ companyServiceEndPoint + "...");
			CompanyServiceSoapServiceLocator locatorCompany = new CompanyServiceSoapServiceLocator();
			CompanyServiceSoap companyService = locatorCompany
					.getPortal_CompanyService(companyServiceEndPoint);
			CompanySoap companySoap = companyService
					.getCompanyByVirtualHost("localhost");

			LOGGER.info("Get UserID" + "...");
			long userId = 0;
			userId = userService.getUserIdByScreenName(
					companySoap.getCompanyId(), LIFERAY_USER_PASSWORD);
			LOGGER.info("UserId for user named " + LIFERAY_USER_PASSWORD
					+ " is " + userId);

			LOGGER.info("Try lookup Group Service by End Point: "
					+ groupServiceEndPoint + "...");
			GroupServiceSoapService locatorGroup = new GroupServiceSoapServiceLocator();
			GroupServiceSoap groupService = locatorGroup
					.getPortal_GroupService(groupServiceEndPoint);

			GroupSoap[] usergroups = groupService.getUserSites();
			long groupId = 0;

			for (int i = 0; i < usergroups.length; i++) {
				if (usergroups[i].getName().equalsIgnoreCase(
						LIFERAY_PUBLISH_SITE)) {
					groupId = usergroups[i].getGroupId();
					LOGGER.info("Found the group " + LIFERAY_PUBLISH_SITE
							+ " (GroupId: " + groupId
							+ ") to publish the document");
				}
			}

			LOGGER.info("Try lookup DL App Service by End Point: "
					+ dlAppServiceEndPoint + "...");
			DLAppServiceSoapServiceLocator locatorDLApp = new DLAppServiceSoapServiceLocator();
			DLAppServiceSoap dlAppService = locatorDLApp
					.getPortlet_DL_DLAppService(dlAppServiceEndPoint);

			String sourceFileName = "xml_oracle.pdf";
			String mimeType = "application/pdf";
			String title = "Test file add by SOAP Service";
			String description = "Test file add by SOAP Service";
			String changeLog = "";

			ServiceContext serviceContext = new ServiceContext();
			serviceContext.setWorkflowAction(1);

			FileEntrySoap fileEntrySoap = dlAppService.addFileEntry(groupId,
					LIFERAY_FOLDER_ID, sourceFileName, mimeType, title,
					description, changeLog, getFileAsByte(FILE_TO_UPLOAD),
					serviceContext);

			LOGGER.info("The file " + sourceFileName
					+ " has been correctly added to liferay");
			LOGGER.info("File Id:" + fileEntrySoap.getFileEntryId());
			LOGGER.info("File Size: " + fileEntrySoap.getSize());
			LOGGER.info("File Version: " + fileEntrySoap.getVersion());

		} catch (RemoteException re) {
			LOGGER.error(re.getClass().getCanonicalName()
					+ re.getMessage());
		} catch (ServiceException se) {
			LOGGER.error(se.getClass().getCanonicalName()
					+ se.getMessage());
		}
	}
	
	/**
	 * Get the URL Liferay SOAP Service
	 * 
	 * @param remoteUser
	 * @param password
	 * @param serviceName
	 * @return
	 */
	private static URL _getURL(String remoteUser, String password,
			String serviceName) {

		final String LIFERAY_PROTOCOL = "http://";
		final String LIFERAY_TCP_PORT = "8080";
		final String LIFERAY_FQDN = "localhost";
		final String LIFERAY_AXIS_PATH = "/api/secure/axis/";

		try {
			return new URL(LIFERAY_PROTOCOL
					+ URLEncoder.encode(remoteUser, "UTF-8") + ":"
					+ URLEncoder.encode(password, "UTF-8") + "@" + LIFERAY_FQDN
					+ ":" + LIFERAY_TCP_PORT + LIFERAY_AXIS_PATH + serviceName);
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
			return null;
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Get file as Byte array
	 * 
	 * @param pathFile
	 * @return
	 */
	private static byte[] getFileAsByte(String pathFile) {
		File file = new File(pathFile);

		try {
			FileInputStream fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fin.read(fileContent);
			fin.close();

			return fileContent;
		} catch (FileNotFoundException e) {
			LOGGER.error("File not found: " + e);
		} catch (IOException e) {
			LOGGER.error("Exception while reading the file: " + e);
		}
		return null;
	}

}
