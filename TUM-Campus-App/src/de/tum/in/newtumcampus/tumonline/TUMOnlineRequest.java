package de.tum.in.newtumcampus.tumonline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import de.tum.in.newtumcampus.TUMOnlineSettings;
import de.tum.in.newtumcampus.common.Dialogs;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.R;

/**
 * This class will handle all action needed to communicate with the TUMOnline XML-RPC backend.
 * 
 * 
 * @author Thomas Behrens, Vincenz Dölle, Daniel Mayr
 */
public class TUMOnlineRequest {

	// server address
	public static final String SERVICE_BASE_URL = "https://campus.tum.de/tumonline/wbservicesbasic.";

	// login service address
	public static final String LOGIN_SERVICE_URL = "https://campus.tum.de/tumonline/anmeldung.durchfuehren";

	// logout service address
	public static final String LOGOUT_SERVICE_URL = "https://campus.tum.de/tumonline/anmeldung.beenden";

	// set to null, if not needed
	private String accessToken = null;

	/** http client instance for fetching */
	private final HttpClient client = new DefaultHttpClient();

	/** a list/map for the needed parameters */
	private Map<String, String> parameters;

	/** method to call */
	private final String method;

	/** asynchronous task for interactive fetch */
	AsyncTask<Void, Void, String> backgroundTask = null;

	/** Progress dialog while fetching information */
	private ProgressDialog progressDialog;

	/** Message to be displayed in progress dialog */
	private String progressDialogMessage = "";

	// constructor without accessToken
	/**
	 * this constructor generates an empty request call for the TUMOnline webservice without setting any parameters or
	 * the access token to use access token see the other constructors
	 * 
	 * @author Daniel G. Mayr
	 * @param method
	 *            the function name to which we are calling
	 */
	public TUMOnlineRequest(String method) {
		this.method = method;
		resetParameters();
	}

	/**
	 * this constructor generates an request to the given method. you can also provide an access token. if you want to
	 * use the stored access token and show a dialog if this one is not set, take {@link TUMOnlineRequest(String method,
	 * Activity callingActivity)}
	 * 
	 * @param method
	 *            facing web service function
	 * @param accessToken
	 *            user's access token to the webservice
	 */
	public TUMOnlineRequest(String method, String accessToken) {
		this.method = method;
		this.accessToken = accessToken;
		resetParameters();
	}

	/**
	 * this constructor will try to load the access token from preferences. if this is not possible, a dialog will
	 * prompt the user to generate the access token via the settings menu
	 * 
	 * @param method
	 *            function name, which is the last part of the core URL
	 * @param callingActivity
	 *            the activity from which the constructor will be called (mostly this)
	 */
	public TUMOnlineRequest(String method, Activity callingActivity) {
		this.method = method;

		resetParameters();

		if (!loadAccessTokenFromPreferences(callingActivity)) {
			// no access token found
			// show a dialog for the user
			Intent iTUMSettings = new Intent(callingActivity, TUMOnlineSettings.class);
			Dialogs.showIntentSwitchDialog(callingActivity, callingActivity,
					callingActivity.getString(R.string.dialog_access_token_missing), iTUMSettings);

		}

		// rest parameters and set the access token
		resetParameters();

		setProgressDialogMessage(callingActivity.getString(R.string.search_is_running));
	}

	/**
	 * Sets one parameter name to its given value
	 * 
	 * @param name
	 *            identifier of the parameter
	 * @param value
	 *            value of the parameter
	 */
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}

	/**
	 * Check if TUMOnline access token can be retrieved from shared preferences.
	 * 
	 * @param context
	 *            The context
	 * @return true if access token is available; false otherwise
	 */
	private boolean loadAccessTokenFromPreferences(Context context) {
		accessToken = PreferenceManager.getDefaultSharedPreferences(context).getString(TUMOnlineConst.ACCESS_TOKEN,
				null);

		// no access token set, or it is obviously wrong
		if (accessToken == null || accessToken.length() < 1) {
			return false;
		}

		Log.d("AccessToken", accessToken);
		// ok, access token seems valid (at first)

		setParameter(TUMOnlineConst.P_TOKEN, accessToken);
		return true;
	}

	/**
	 * If you want to put a complete Parameter Map into the request, use this function to merge them with the existing
	 * parameter map
	 * 
	 * @param existingMap
	 *            a Map<String,String> which should be set
	 */
	public void setParameters(Map<String, String> existingMap) {
		parameters.putAll(existingMap);
	}

	/**
	 * Returns a map with all set parameter pairs
	 * 
	 * @return Map<String, String> parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/** Reset parameters to an empty Map */
	public void resetParameters() {
		parameters = new HashMap<String, String>();
		// set accessToken as parameter if available
		if (accessToken != null) {
			parameters.put(TUMOnlineConst.P_TOKEN, accessToken);
		}
	}

	/**
	 * This will return the URL to the TUMOnlineRequest with regard to the set parameters
	 * 
	 * @return a String URL
	 */
	public String getRequestURL() {
		String url = SERVICE_BASE_URL + method + "?";
		Iterator<Entry<String, String>> itMapIterator = parameters.entrySet().iterator();
		while (itMapIterator.hasNext()) {
			Entry<String, String> pairs = itMapIterator.next();
			url += pairs.getKey() + "=" + pairs.getValue() + "&";
		}
		return url;
	}

	/**
	 * Fetches the result of the HTTPRequest (which can be seen by using getRequestURL)
	 * 
	 * @author Daniel G. Mayr
	 * @return output will be a raw String
	 * @see getRequestURL
	 */
	public String fetch() {
		String url = getRequestURL();
		Log.d("TUMOnlineXMLRequest", "fetching URL " + url);

		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity responseEntity = response.getEntity();

			if (responseEntity != null) {
				// do something with the response
				String result = EntityUtils.toString(responseEntity);
				// Log.d("FETCH", result);
				return result;
			}

		} catch (Exception e) {
			Log.d("FETCHerror", e.toString());
			e.printStackTrace();
			return e.getMessage();
		}
		return null;

	}

	/**
	 * this fetch method will fetch the data from the TUMOnline Request and will address the listeners onFetch if the
	 * fetch succeeded, else the onFetchError will be called
	 * 
	 * @param context
	 *            the current context (may provide the current activity)
	 * @param listener
	 *            the listener, which takes the result
	 */
	public void fetchInteractive(final Context context, final TUMOnlineRequestFetchListener listener) {

		if (!loadAccessTokenFromPreferences(context)) {
			listener.onFetchCancelled();
		}

		// start the progress dialog
		progressDialog = ProgressDialog.show(context, "", getProgressDialogMessage());
		progressDialog.setCancelable(true);

		// terminate background task if running
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (backgroundTask != null) {
					backgroundTask.cancel(true);
					listener.onFetchCancelled();
				}

			}
		});

		// fetch information in a background task and show progress dialog in meantime
		backgroundTask = new AsyncTask<Void, Void, String>() {

			/** property to determine if there is an internet connection */
			boolean isOnline;

			@Override
			protected String doInBackground(Void... params) {
				// set parameter on the TUMOnline request an fetch the results
				isOnline = Utils.isConnected(context);
				if (!isOnline) {
					// not online, fetch does not make sense
					return null;
				}
				// we are online, return fetch result
				return fetch();
			}

			@Override
			protected void onPostExecute(String result) {
				// stop dialog first
				progressDialog.dismiss();

				// handle result
				if (isOnline == false) {
					listener.onFetchError(context.getString(R.string.no_internet_connection));
					return;
				}
				if (result == null) {
					listener.onFetchError(context.getString(R.string.empty_result));
					// TODO Check whether to move to string.xml
				} else if (result.contains(TUMOnlineConst.TOKEN_NICHT_BESTAETIGT)) {
					Intent iTUMSettings = new Intent(context, TUMOnlineSettings.class);
					Dialogs.showIntentSwitchDialog(context, (Activity) context,
							((Activity) context).getString(R.string.dialog_access_token_invalid), iTUMSettings);

					listener.onFetchError(context.getString(R.string.dialog_access_token_invalid));
				}

				listener.onFetch(result);
			}

		};

		backgroundTask.execute();
	}

	public String getProgressDialogMessage() {
		return progressDialogMessage;
	}

	public void setProgressDialogMessage(String progressDialogMessage) {
		this.progressDialogMessage = progressDialogMessage;
	}

}
