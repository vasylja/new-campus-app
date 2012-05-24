package de.tum.in.newtumcampus;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.FindLecturesListAdapter;
import de.tum.in.newtumcampus.models.FindLecturesRow;
import de.tum.in.newtumcampus.models.FindLecturesRowSet;
import de.tum.in.newtumcampus.tumonline.TUMOnlineRequest;
import de.tum.in.newtumcampus.tumonline.TUMOnlineRequestFetchListener;

/**
 * This activity represents a small find box to query through the TUMOnline web service to find lectures identified by the acquired query string.
 * 
 * A list of all found lectures will be displayed and by clicking on each it will send the lecture number to the LectureDetails Activity.
 * 
 * HINT: a TUMOnline access token is needed
 * 
 * 
 * needed/linked files:
 * 
 * res.layout.findLectures (Layout XML), models.FindLecturesRowSet, models.FindLecturesListAdapter
 * 
 * 
 * @solves [M4] Lehrveranstaltungen suchen
 * @author Daniel Mayr
 * @review Thomas Behrens
 */
public class FindLectures extends Activity implements OnEditorActionListener, TUMOnlineRequestFetchListener {

	/** UI Elements */
	EditText etFindQuery;
	ListView lvFound;

	/** Handler to send request to TUMOnline */
	private TUMOnlineRequest requestHandler;
	
	private static String VERANSTALTUNGENSUCHE = "veranstaltungenSuche";
	
	private static String P_SUCHE = "pSuche";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findlectures);

		// bind GUI elements
		etFindQuery = (EditText) findViewById(R.id.etFindQuery);
		etFindQuery.setOnEditorActionListener(this);

		lvFound = (ListView) findViewById(R.id.lvFound);
	}

	@Override
	public void onStart() {
		super.onStart();

		// prepare the TUMOnlineRequest and check if the access token is set
		requestHandler = new TUMOnlineRequest(VERANSTALTUNGENSUCHE, this);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (etFindQuery.getText().length() < 2) {
			Utils.showLongCenteredToast(this, getString(R.string.please_insert_at_least_two_chars));
			return false;
		}

		// set the query string as parameter for the TUMOnline request
		requestHandler.setParameter(P_SUCHE, etFindQuery.getText().toString());

		Utils.hideKeyboard(this, etFindQuery);

		// do the TUMOnline request (implement listener here)
		requestHandler.fetchInteractive(this, this);

		return true;
	}

	@Override
	public void onFetch(String rawResponse) {
		Log.d("RESPONSE", rawResponse);

		// deserialize the xml output
		// we use simpleXML for this by providing a
		// class which represents the xml-schema
		Serializer serializer = new Persister();
		FindLecturesRowSet lecturesList = null;

		try {
			lecturesList = serializer.read(FindLecturesRowSet.class, rawResponse);

		} catch (Exception e) {
			Log.d("SIMPLEXML", "wont work: " + e.getMessage());
			e.printStackTrace();
		}

		if (lecturesList == null) {
			// no results found
			return;
		}

		// make some customizations to the ListView
		// provide data via the FindLecturesListAdapter
		lvFound.setAdapter(new FindLecturesListAdapter(this, lecturesList.getLehrveranstaltungen()));

		// deal with clicks on items in the ListView
		lvFound.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// each item represents the current FindLecturesRow
				// item
				Object o = lvFound.getItemAtPosition(position);
				FindLecturesRow item = (FindLecturesRow) o;

				// bundle data for the LectureDetails Activity
				Bundle bundle = new Bundle();
				bundle.putString(item.STP_SP_NR, item.getStp_sp_nr());
				Intent i = new Intent(FindLectures.this, LectureDetails.class);
				i.putExtras(bundle);
				// load LectureDetails
				startActivity(i);
			}
		});
	}

	/**
	 * while fetching a TUMOnline Request an error occurred this will show the error message in a toast
	 */
	@Override
	public void onFetchError(String errorReason) {
		Utils.showLongCenteredToast(this, errorReason);
	}

	@Override
	public void onFetchCancelled() {
		// show toast to notice cancel
		Utils.showLongCenteredToast(this, getString(R.string.cancel));
	}

}
