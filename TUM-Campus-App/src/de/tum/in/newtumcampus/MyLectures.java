package de.tum.in.newtumcampus;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.FindLecturesListAdapter;
import de.tum.in.newtumcampus.models.FindLecturesRow;
import de.tum.in.newtumcampus.models.FindLecturesRowSet;
import de.tum.in.newtumcampus.tumonline.TUMOnlineRequest;
import de.tum.in.newtumcampus.tumonline.TUMOnlineRequestFetchListener;

/**
 * This activity presents the users' lectures using the TUMOnline web service the results can be filtered by the semester or all shown.
 * 
 * This activity uses the same models as FindLectures.
 * 
 * HINT: a TUMOnline access token is needed
 * 
 * 
 * needed/linked files:
 * 
 * res.layout.mylectures (Layout XML), models.FindLecturesRowSet, models.FindLecturesListAdapter
 * 
 * @solves [M1] Meine Lehrveranstaltungen
 * @author Daniel G. Mayr
 * 
 */
public class MyLectures extends Activity implements TUMOnlineRequestFetchListener {
	/** Handler to send request to TUMOnline */
	private TUMOnlineRequest requestHandler;

	/** filtered list which will be shown */
	FindLecturesRowSet lecturesList = null;

	/** UI elements */
	private ListView lvMyLecturesList;
	private Spinner spFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylectures);

		// bind UI elements
		lvMyLecturesList = (ListView) findViewById(R.id.lvMyLecturesList);
		spFilter = (Spinner) findViewById(R.id.spFilter);
	}

	@Override
	public void onStart() {
		super.onStart();
		// preparing the TUMOnline web service request
		requestHandler = new TUMOnlineRequest("veranstaltungenEigene", this);

		String accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("access_token", null);
		if (accessToken != null) {
			requestHandler.fetchInteractive(this, this);
		}
	}

	/**
	 * Sets all data concerning the FindLecturesListView.
	 * 
	 * @param lecturesList
	 *            filtered list of lectures
	 */
	private void setListView(List<FindLecturesRow> lecturesList) {
		// set ListView to data via the FindLecturesListAdapter
		lvMyLecturesList.setAdapter(new FindLecturesListAdapter(this, lecturesList));

		// handle on click events by showing its LectureDetails
		lvMyLecturesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Object o = lvMyLecturesList.getItemAtPosition(position);
				FindLecturesRow item = (FindLecturesRow) o;

				// set bundle for LectureDetails and show it
				Bundle bundle = new Bundle();
				// we need the stp_sp_nr
				bundle.putString("stp_sp_nr", item.getStp_sp_nr());
				Intent i = new Intent(MyLectures.this, LectureDetails.class);
				i.putExtras(bundle);
				// start LectureDetails for given stp_sp_nr
				startActivity(i);
			}
		});
	}

	@Override
	public void onFetch(String rawResponse) {

		// deserialize the XML
		Serializer serializer = new Persister();
		try {
			lecturesList = serializer.read(FindLecturesRowSet.class, rawResponse);
		} catch (Exception e) {
			Log.d("SIMPLEXML", "wont work: " + e.getMessage());
			e.printStackTrace();
		}

		// set Spinner data (semester)
		List<String> filters = new ArrayList<String>();
		filters.add(getString(R.string.all));
		for (int i = 0; i < lecturesList.getLehrveranstaltungen().size(); i++) {
			String item = lecturesList.getLehrveranstaltungen().get(i).getSemester_id();
			if (filters.indexOf(item) == -1) {
				filters.add(item);
			}
		}
		// simple adapter for the spinner
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, filters);
		spFilter.setAdapter(spinnerArrayAdapter);
		spFilter.setOnItemSelectedListener(new OnItemSelectedListener() {

			/**
			 * if an item in the spinner is selected, we have to filter the results which are displayed in the ListView
			 * 
			 * -> tList will be the data which will be passed to the FindLecturesListAdapter
			 */
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String filter = spFilter.getItemAtPosition(arg2).toString();
				if (filter == getString(R.string.all)) {
					setListView(lecturesList.getLehrveranstaltungen());
				} else {
					// do filtering for the given semester
					List<FindLecturesRow> filteredList = new ArrayList<FindLecturesRow>();
					for (int i = 0; i < lecturesList.getLehrveranstaltungen().size(); i++) {
						FindLecturesRow item = lecturesList.getLehrveranstaltungen().get(i);
						if (item.getSemester_id().equals(filter)) {
							filteredList.add(item);
						}
					}
					// listview gets filtered list
					setListView(filteredList);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// select [Alle], if none selected either
				spFilter.setSelection(0);
				setListView(lecturesList.getLehrveranstaltungen());
			}
		});

		setListView(lecturesList.getLehrveranstaltungen());
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
		// ignore
	}
}
