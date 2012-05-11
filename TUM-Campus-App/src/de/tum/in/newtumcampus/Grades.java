package de.tum.in.newtumcampus;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.Exam;
import de.tum.in.newtumcampus.models.ExamList;
import de.tum.in.newtumcampus.models.ExamListAdapter;
import de.tum.in.newtumcampus.tumonline.TUMOnlineRequest;
import de.tum.in.newtumcampus.tumonline.TUMOnlineRequestFetchListener;

/**
 * Activity to show the user's grades/exams passed.
 * 
 * @author Vincenz Doelle
 * @review Daniel G. Mayr
 * @review Thomas Behrens
 */
public class Grades extends Activity implements TUMOnlineRequestFetchListener {

	/** Spinner to choose between programs */
	private Spinner spFilter;

	/** List with all exams passed (including grades) */
	private ExamList examList;

	/** List view to display all exams/grades */
	private ListView lvGrades;

	/** HTTP request handler to handle requests to TUMOnline */
	private TUMOnlineRequest requestHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.grades);

		spFilter = (Spinner) findViewById(R.id.spFilter);
		lvGrades = (ListView) findViewById(R.id.lstGrades);
	}

	@Override
	public void onStart() {
		super.onStart();

		requestHandler = new TUMOnlineRequest("noten", this);

		String accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("access_token", null);
		if (accessToken != null) {
			fetchGrades();
		}
	}

	/**
	 * Fetches all grades from TUMOnline.
	 * 
	 */
	public void fetchGrades() {
		requestHandler.fetchInteractive(this, this);
	}

	/**
	 * Handle the response by deserializing it into model entities.
	 * 
	 * @param rawResp
	 */
	@Override
	public void onFetch(String rawResp) {

		Serializer serializer = new Persister();
		examList = null;

		try {
			// deserialize XML response
			examList = serializer.read(ExamList.class, rawResp);

			// initialize the program choice spinner
			initSpinner();

			// display results in view
			lvGrades.setAdapter(new ExamListAdapter(Grades.this, examList.getExams()));

		} catch (Exception e) {
			Log.d("SIMPLEXML", "wont work: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onFetchError(String errorReason) {
		Utils.showLongCenteredToast(this, errorReason);
	}

	@Override
	public void onFetchCancelled() {
		finish();
	}

	/**
	 * Initialize the spinner for choosing between the study programs.
	 */
	private void initSpinner() {

		// set Spinner data
		List<String> filters = new ArrayList<String>();
		filters.add(getString(R.string.all_programs));

		// get all program ids from the results
		for (int i = 0; i < examList.getExams().size(); i++) {
			String item = examList.getExams().get(i).getProgramID();
			if (filters.indexOf(item) == -1) {
				filters.add(item);
			}
		}

		// init the spinner
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, filters);
		spFilter.setAdapter(spinnerArrayAdapter);

		// handle if program choice is changed
		spFilter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				String filter = spFilter.getItemAtPosition(arg2).toString();

				if (filter == getString(R.string.all_programs)) {
					// display all grades
					lvGrades.setAdapter(new ExamListAdapter(Grades.this, examList.getExams()));
				} else {
					// do filtering according to selected program
					List<Exam> filteredExamList = new ArrayList<Exam>();
					for (int i = 0; i < examList.getExams().size(); i++) {
						Exam item = examList.getExams().get(i);
						if (item.getProgramID().equals(filter)) {
							filteredExamList.add(item);
						}
					}
					// list view gets filtered list
					lvGrades.setAdapter(new ExamListAdapter(Grades.this, filteredExamList));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// select [Alle]
				spFilter.setSelection(0);
				lvGrades.setAdapter(new ExamListAdapter(Grades.this, examList.getExams()));
			}
		});
	}
}
