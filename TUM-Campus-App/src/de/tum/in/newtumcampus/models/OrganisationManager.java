package de.tum.in.newtumcampus.models;

import java.io.File;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import de.tum.in.newtumcampus.common.FileUtils;

/**
 * 
 */

/**
 * Event Manager, handles database stuff, external imports
 * <p>
 * 
 * @author Thomas Behrens
 */

public class OrganisationManager {

	/**
	 * Maximal file age in milliseconds (here: (3600*1000*24*5) = 5 Days)
	 */
	private final int MAXFILEAGE = 432000000;

	/**
	 * Constructor, open/create database, create table if necessary
	 * 
	 * <pre>
	 * @param context Context
	 * @param database Filename, e.g. database.db
	 * </pre>
	 */
	public OrganisationManager(Context context) {
		super();
	}

	/**
	 * Look if the xml-File has to get updated
	 * 
	 * @return true (if xml-File is older than 1 week or doesn't exist), false (if xml-File is newer than 1 week)
	 */
	public boolean needSync() {

		File xmlOrgFile;
		try {
			xmlOrgFile = FileUtils.getFileOnSD("organisations", "org.xml");
		} catch (Exception e) {
			Log.d("EXCEPTION", "No SD-card!");
			return true;
		}

		// does file exist? is it a file? is it a xml token error or the real wanted file?
		if (!xmlOrgFile.exists() || !xmlOrgFile.isFile() || !(xmlOrgFile.length() > 100000)) {
			return true;
		}

		Long lastModified = xmlOrgFile.lastModified();

		// if older than 5 days => return true
		if ((lastModified + MAXFILEAGE) < System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	/**
	 * Download xml File of the full Orgtree
	 * 
	 * <pre>
	 * @param force True to force download over normal sync period, else false
	 * @param Token Token for the XML-Request
	 * @throws Exception
	 * </pre>
	 */
	public void downloadFromExternal(String token) throws Exception {

		// cancel, if it has been updated in the last 5 days
		if (!this.needSync()) {
			return;
		}

		DefaultHttpClient client = new DefaultHttpClient();

		// build url
		String url = "https://campus.tum.de/tumonline/wbservicesbasic.orgBaum?pToken=" + token;

		// delete and create new file if there is already an old one existing
		File xmlOrgFile = FileUtils.getFileOnSD("organisations", "org.xml");
		if (xmlOrgFile.exists()) {
			xmlOrgFile.delete();
			xmlOrgFile.createNewFile();
		}

		// get xml data and write it into file
		String xmlText = FileUtils.sendGetRequest(client, url);
		FileUtils.writeFile(xmlOrgFile, xmlText);
		Log.d("Import: org.xml", "Xml file has been new downloaded and saved.");
	}
}