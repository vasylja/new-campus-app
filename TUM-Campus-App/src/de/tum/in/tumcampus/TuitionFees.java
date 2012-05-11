package de.tum.in.tumcampus;

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
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import de.tum.in.tumcampus.common.Dialogs;
import de.tum.in.tumcampus.common.FileUtils;
import de.tum.in.tumcampus.common.Utils;
import de.tum.in.tumcampus.tumonline.TUMOnlineRequest;

/**
 * Activity to download student documents from TUMOnline.
 * 
 * @author Vincenz Doelle
 */

public class TuitionFees extends Activity {

	// credentials
	private String tumOnlineUsername;
	private String tumOnlinePassword;

	// the tuition fee document on the SD card
	private File tuitionFeeDocument;

	// widgets
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tuition_fees);

		// get a reference and create if not exists
		try {
			tuitionFeeDocument = FileUtils.getFileOnSD("documents", "studienbeitragsstatus.html");
		} catch (Exception e) {
			Utils.showLongCenteredToast(this, getString(R.string.no_sd_card));
			Log.d("EXCEPTION", e.getMessage());
		}

		// init web view
		webView = Utils.getDefaultWebView(this, R.id.wvResults);
		webView.setLongClickable(true);
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showAlertDialog();
				return true;
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		// check the credentials
		tumOnlineUsername = PreferenceManager.getDefaultSharedPreferences(this).getString("lrz_id", null);
		if (tumOnlineUsername == null) {
			Dialogs.showIntentSwitchDialog(this, this, getString(R.string.dialog_username_not_set), new Intent(this, TUMOnlineSettings.class));
			return;
		}

		tumOnlinePassword = PreferenceManager.getDefaultSharedPreferences(this).getString("tumonline_password", null);
		if (tumOnlinePassword == null) {
			Dialogs.showIntentSwitchDialog(this, this, getString(R.string.dialog_password_not_set), new Intent(this, TUMOnlineSettings.class));
			return;
		}

		// if the document is updated in the last day show the document immediately
		if (tuitionFeeDocument != null && tuitionFeeDocument.lastModified() > 0 && System.currentTimeMillis() - tuitionFeeDocument.lastModified() <= 86400000) {
			webView.loadUrl("file://" + tuitionFeeDocument.getPath());

		} else {
			// download new
			if (!tuitionFeeDocument.exists()) {
				getDocument("https://campus.tum.de/tumonline/wbstudienbeitragstatus.show");
			} else {
				// give the user the choice
				showAlertDialog();
			}
		}
	}

	/**
	 * Shows an {@link AlertDialog} offering the user whether to open the file from SD card or download it again.
	 */
	private void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(TuitionFees.this);
		builder.setMessage(getString(R.string.load_doc));
		builder.setPositiveButton(getString(R.string.open_from_sd), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				webView.loadUrl("file://" + tuitionFeeDocument.getPath());
			}

		});
		builder.setNegativeButton(getString(R.string.download_new), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				getDocument("https://campus.tum.de/tumonline/wbstudienbeitragstatus.show");
			}
		});

		// configure what to do on cancel
		builder.setCancelable(true);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		// show dialog
		AlertDialog alert = builder.create();
		alert.show();

	}

	/**
	 * Creates a background task and calls fetchDocument while showing a progress dialog.
	 * 
	 * @param documentURL
	 *            URL of the document to be downloaded
	 */
	private void getDocument(String documentURL) {
		if (!Utils.isConnected(this)) {
			Utils.showLongCenteredToast(this, getString(R.string.no_internet_connection));
			return;
		}

		// start the progress dialog
		final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.fetching_document));
		progressDialog.setCancelable(true);

		// fetch information in a background task and show progress dialog in meantime
		final AsyncTask<String, Void, Boolean> backgroundTask = new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... urls) {
				return fetchDocument(urls[0]);
			}

			@Override
			protected void onPostExecute(Boolean status) {
				progressDialog.dismiss();

				if (status == false) {
					Utils.showLongCenteredToast(TuitionFees.this, TuitionFees.this.getString(R.string.tumonline_settings_error));
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

		backgroundTask.execute(documentURL);
	}

	/**
	 * Fetches the tuition fee document from TUMOnline.
	 * 
	 * @param documentURL
	 *            The URL of the document.
	 */
	private boolean fetchDocument(String documentURL) {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		// get cookies
		String url = TUMOnlineRequest.LOGIN_SERVICE_URL;
		FileUtils.sendPostRequest(httpClient, url);

		// prepare credentials
		String username = tumOnlineUsername.replace("@", "%40");
		String password = tumOnlinePassword;

		// log in
		url = "https://campus.tum.de/tumonline/wbanmeldung.durchfuehren?ctxid=check&curl=&cinframe=&cp1=" + username + "&cp2=" + password;
		String resp = FileUtils.sendPostRequest(httpClient, url);

		// login unsuccessful
		if (resp.contains("Kennwort vergessen?")) {
			return false;
		}

		// get document
		String text = FileUtils.sendGetRequest(httpClient, documentURL);

		// log out
		url = TUMOnlineRequest.LOGOUT_SERVICE_URL;
		FileUtils.sendPostRequest(httpClient, url);

		if (text == null) {
			Utils.showLongCenteredToast(this, getString(R.string.something_wrong));
		}

		text = Utils.cutText(text, "<form", "</form>");
		text = text.replace("<select", "<!--<select");
		text = text.replace("</select>", "</select>-->");
		text = Utils.buildHTMLDocument("", text);

		// download file
		FileUtils.writeFile(tuitionFeeDocument, text);

		webView.loadUrl("file://" + tuitionFeeDocument.getPath());

		return true;
	}
}
