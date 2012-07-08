package de.tum.in.newtumcampus;

import java.io.File;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.tum.in.newtumcampus.common.Dialogs;
import de.tum.in.newtumcampus.common.FileUtils;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.tumonline.TUMOnlineRequest;

/**
 * Activity to download student documents from TUMOnline.
 * 
 * @author Vincenz Doelle
 * @review Daniel G. Mayr
 * @review Thomas Behrens
 */
public class Documents extends Activity {

	// credentials
	private String tumOnlineUsername;
	private String tumOnlinePassword;

	// list of documents
	private ListView lvDocuments;
	// choice from list
	private int selectedDocument;

	// all documents available; displayed in lvDocuments
	String[] options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.documents);

		// init the list view
		lvDocuments = (ListView) findViewById(R.id.lstDocuments);

	}

	@Override
	public void onStart() {
		super.onStart();

		// check credentials
		tumOnlineUsername = PreferenceManager.getDefaultSharedPreferences(this).getString(Const.LRZ_ID, null);
		if (tumOnlineUsername == null) {
			Dialogs.showIntentSwitchDialog(this, this, getString(R.string.dialog_username_not_set), new Intent(this,
					TUMOnlineSettings.class));
			return;
		}

		tumOnlinePassword = PreferenceManager.getDefaultSharedPreferences(this).getString(Const.TUMONLINE_PASSWORD,
				null);
		if (tumOnlinePassword == null) {
			Dialogs.showIntentSwitchDialog(this, this, getString(R.string.dialog_password_not_set), new Intent(this,
					TUMOnlineSettings.class));
			return;
		}

		// set document options
		options = new String[5];
		options[0] = getString(R.string.certificate_of_matriculation) + " (DE)";
		options[1] = getString(R.string.certificate_of_matriculation) + " (EN)";
		options[2] = getString(R.string.certificate_of_matriculation) + " MVV";
		options[3] = getString(R.string.study_status);
		options[4] = getString(R.string.confirmation_about_payment);

		// initialize the list view
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_custom, options);
		lvDocuments.setAdapter(arrayAdapter);

		lvDocuments.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedDocument = position;

				try {
					// try if document exists in storage
					if (!FileUtils.getFileOnSD(Const.DOCUMENTS,
							FileUtils.getFilename(options[selectedDocument], ".pdf")).exists()) {
						// we can access external storage but file does not exist
						// hence, download it
						getDocument();
						return;
					}
					// if exception occurred, we cannot access external storage
				} catch (Exception e) {
					Utils.showLongCenteredToast(Documents.this, getString(R.string.no_sd_card));
					Log.d("EXCEPTION", e.getMessage());
					return;
				}

				// file exists, so show alert dialog offering the user to open the document from SD-card or to newly
				// download it
				AlertDialog.Builder builder = new AlertDialog.Builder(Documents.this);
				builder.setMessage(getString(R.string.load_doc));
				builder.setPositiveButton(getString(R.string.open_from_sd), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						File file;
						try {
							file = FileUtils.getFileOnSD(Const.DOCUMENTS,
									FileUtils.getFilename(options[selectedDocument], ".pdf"));
							FileUtils.openFile(file, Documents.this, FileUtils.PDF_TYPE);
						} catch (Exception e) {
							Utils.showLongCenteredToast(Documents.this, getString(R.string.no_sd_card));
							Log.d("EXCEPTION", e.getMessage());
						}
					}

				});
				builder.setNegativeButton(getString(R.string.download_new), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						getDocument();
					}
				});

				builder.setCancelable(true);

				// show the dialog
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	/**
	 * Creates a background task and calls fetchDocument while showing a progress dialog.
	 */
	private void getDocument() {
		// cancel if no internet connection
		if (!Utils.isConnected(this)) {
			Utils.showLongCenteredToast(Documents.this, getString(R.string.no_internet_connection));
			return;
		}

		// start the progress dialog
		final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.fetching_document));
		progressDialog.setCancelable(true);

		// fetch information in a background task and show progress dialog in meantime
		final AsyncTask<Void, Void, File> backgroundTask = new AsyncTask<Void, Void, File>() {

			@Override
			protected File doInBackground(Void... params) {
				DefaultHttpClient httpClient = new DefaultHttpClient();

				String url = prepareDownload(httpClient);

				if (url == null) {
					return null;
				}

				return fetchDocument(url, httpClient);
			}

			@Override
			protected void onPostExecute(File file) {
				progressDialog.dismiss();

				// result is not null?
				if (file != null) {
					FileUtils.openFile(file, Documents.this, FileUtils.PDF_TYPE);

				} else { // error occurred
					Utils.showLongCenteredToast(Documents.this,
							Documents.this.getString(R.string.tumonline_settings_error));
				}
			}
		};

		// terminate background task if running
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				backgroundTask.cancel(true);
			}
		});

		backgroundTask.execute();
	}

	/**
	 * Fetch the selected document from TUMOnline.
	 * 
	 * @throws Exception
	 */
	private String prepareDownload(DefaultHttpClient httpClient) {
		// get cookies
		String url = TUMOnlineRequest.LOGIN_SERVICE_URL;
		FileUtils.sendPostRequest(httpClient, url);

		// prepare credentials
		String username = tumOnlineUsername.replace("@", "%40");
		String password = tumOnlinePassword;

		// log in
		url = "https://campus.tum.de/tumonline/wbanmeldung.durchfuehren?ctxid=check&curl=&cinframe=&cp1=" + username
				+ "&cp2=" + password;
		String resp = FileUtils.sendPostRequest(httpClient, url);

		// parse pStPersonNr
		String personNr = parseNumber(resp, "wbStudAusdrucke.html?pStPersonNr=");

		if (personNr.length() == 0) {
			return null;
		}

		url = "https://campus.tum.de/tumonline/wbStudAusdrucke.html?pStPersonNr=" + personNr;
		resp = FileUtils.sendPostRequest(httpClient, url);

		// parse semesterNr
		String semesterNr = parseNumber(resp, "id=\"idImmatSemesterNr\"><option value=\"");

		return getURLForDocument(url, personNr, semesterNr);
	}

	private File fetchDocument(String url, DefaultHttpClient httpClient) {
		// download file
		File file = null;
		try {
			file = FileUtils.getFileOnSD(Const.DOCUMENTS, FileUtils.getFilename(options[selectedDocument], ".pdf"));
		} catch (Exception e) {
			// do not notify user, since this exception cannot not happen here (catched in onItemClick above)
			Log.d("EXCEPTION", e.getMessage());
		}
		file = FileUtils.getFileFromURL(httpClient, url, file);

		// log out
		url = TUMOnlineRequest.LOGOUT_SERVICE_URL;
		FileUtils.sendPostRequest(httpClient, url);

		return file;
	}

	private String getURLForDocument(String url, String personNr, String semesterNr) {
		// decide which document to download
		switch (selectedDocument) {
		case 0:
			url = "https://campus.tum.de/tumonline/wbStudAusdrucke.immatrikulationsbescheinigung?pStPersonNr="
					+ personNr + "&pSemesterNr=" + semesterNr + "&pLanguage=" + "DE";
			break;
		case 1:
			url = "https://campus.tum.de/tumonline/wbStudAusdrucke.immatrikulationsbescheinigung?pStPersonNr="
					+ personNr + "&pSemesterNr=" + semesterNr + "&pLanguage=" + "EN";
			break;
		case 2:
			url = "https://campus.tum.de/tumonline/wbStudAusdrucke.immatrikulationsbeschein_mvv?pStPersonNr="
					+ personNr + "&pSemesterNr=" + semesterNr;
			break;
		case 3:
			url = "https://campus.tum.de/tumonline/wbStudAusdrucke.studienverlaufsbescheinigung?pStPersonNr="
					+ personNr + "&pSemesterNr=" + semesterNr;
			break;
		case 4:
			url = "https://campus.tum.de/tumonline/wbStudAusdrucke.zahlungsbeleg?pStPersonNr=" + personNr
					+ "&pSemesterNr=" + semesterNr;
			break;
		default:
			return null;
		}
		return url;
	}

	/**
	 * Read a number from the position of the given pattern until a non-integer character occurs.
	 * 
	 * @param text Text containing the pattern followed by the number
	 * @param pattern Pattern to look for; will be followed by the desired number
	 * 
	 * @return The number as a String
	 */
	private static String parseNumber(String text, String pattern) {
		int i = text.indexOf(pattern) + pattern.length();
		String number = "";

		// while we read numbers...
		while (("" + text.charAt(i)).matches("([0-9]+)")) {
			number += text.charAt(i);
			i++;
		}
		return number;
	}

}
