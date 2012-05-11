package de.tum.in.tumcampus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import de.tum.in.tumcampus.common.Utils;
import de.tum.in.tumcampus.models.LectureItemManager;

/**
 * this activity let the user export all his lectures to a google calendar
 * 
 * @author Daniel G. Mayr
 * 
 */
public class Lectures2Calendar extends Activity implements OnClickListener, OnSeekBarChangeListener {

	/** UI elements */
	private Button btnToCalendar;
	private Spinner spinCalendar;
	private SeekBar sbReminder;
	private TextView tvReminderCaption;

	/** google calendar mapping for name to id */
	private Map<String, Integer> calendars;

	/** id of the selected calendar */
	private int selectedCalendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lectures2calendar);

		// bind UI elements
		btnToCalendar = (Button) findViewById(R.id.btnToCalendar);
		btnToCalendar.setOnClickListener(this);
		spinCalendar = (Spinner) findViewById(R.id.spinCalendar);
		sbReminder = (SeekBar) findViewById(R.id.sbReminder);
		sbReminder.setOnSeekBarChangeListener(this);
		// to set minutes for reminder interactively
		tvReminderCaption = (TextView) findViewById(R.id.tvReminderCaption);
	}
	
	@Override
	public void onStart() {
		super.onStart();

		// set reminder
		this.onProgressChanged(sbReminder, 0, false);

		// set calendar list to spinner
		calendars = getAvailableGoogleCalendars(this);
		String[] calendarStrings = calendars.keySet().toArray(new String[0]);
		// simple adapter for the spinner

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, calendarStrings);
		spinCalendar.setAdapter(spinnerArrayAdapter);
		spinCalendar.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String filter = spinCalendar.getItemAtPosition(arg2).toString();
				// get id for that string
				Integer iselect = calendars.get(filter);
				//well, not in map (may not be needed)
				if(iselect==null) selectedCalendar=-1;
				else
					// select it
					selectedCalendar = iselect.intValue();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// set to invalid id
				selectedCalendar = -1;
			}
		});

	}

	/**
	 * @see http://www.developer.com/ws/article.php/3850276/Working-with-the-Android-Calendar.htm
	 */
	public boolean addToCalendar(int calendar_id, String title, String description, String location, long start, long end, int minRemind) {

		// end should be after start
		if (start >= end)
			return false;

		ContentValues event = new ContentValues();
		event.put("calendar_id", calendar_id);
		event.put("title", title);
		event.put("description", description);
		event.put("eventLocation", location);

		event.put("eventTimezone", "Europe/Berlin");

		event.put("dtstart", start);
		event.put("dtend", end);

		// now insert Uri
		Uri newEvent = getContentResolver().insert(getCalendarUri(true), event);
		if (newEvent == null) {
			return false;
		}

		// set reminder
		setReminder(newEvent.getLastPathSegment(), minRemind);

		return true;
	}
	
	/**
	 * set a reminder for a given event
	 * 
	 * @return true if it worked correctly
	 */
	private boolean setReminder(String event_id, int minutes) {
		if (minutes <= 0)
			return false;
		ContentValues values = new ContentValues();
		values.put("event_id", event_id);
		values.put("method", 1);
		values.put("minutes", minutes);
		Uri newReminder = getContentResolver().insert(getReminderUri(), values);
		if (newReminder == null)
			return false;
		// otherwise: super!
		return true;
	}

	
	/**
	 * this method returns the calendar or event uri with respect to the sdk version
	 * 
	 * @param eventUri
	 *            if true returns the eventuri, otherwise the calendar uri
	 * @return eventuri or calendaruri
	 * @see http://blog.yeradis.com/2011/01/failed-to-find-provider-info-for.html
	 */
	private static Uri getCalendarUri(boolean eventUri) {
		Uri calendarURI = null;

		if (android.os.Build.VERSION.SDK_INT <= 7) {
			calendarURI = (eventUri) ? Uri.parse("content://calendar/events") : Uri.parse("content://calendar/calendars");
		} else {
			calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/events") : Uri.parse("content://com.android.calendar/calendars");
		}
		return calendarURI;
	}
	
	/**
	 * this method returns the uri of the reminder content provider
	 * 
	 * @return the reminders uri
	 */
	private static Uri getReminderUri() {
		Uri reminderUri = null;
		if (android.os.Build.VERSION.SDK_INT <= 7) {
			reminderUri = Uri.parse("content://calendar/reminders");
		} else {
			reminderUri = Uri.parse("content://com.android.calendar/reminders");
		}
		return reminderUri;
	}

	/**
	 * will gather all google calendars and their ids
	 * 
	 * @param con
	 *            current context
	 * @return a map of name to id
	 */
	private static Map<String, Integer> getAvailableGoogleCalendars(Context con) {
		ContentResolver ctnresolver = con.getContentResolver();

		String[] projection = new String[] { "_id", "name" };
		Cursor cursor = ctnresolver.query(getCalendarUri(false), projection, null, null, null);

		// nothing found
		// maybe we do not have the rights
		if (cursor == null)
			return null;
		
		// we found some
		// init our result map
		Map<String, Integer> result = new HashMap<String, Integer>();
		while (cursor.moveToNext()) {

			final String idstring = cursor.getString(0);
			final String displayName = cursor.getString(1);
			Integer id = Integer.parseInt(idstring);
			// put it on map
			result.put(displayName, id);
		}
		return result;
	}


	@Override
	public void onClick(View v) {

		if (v.getId() == btnToCalendar.getId()) {
			// export was clicked

			if (selectedCalendar < 0) {
				// something is wrong
				// there is no calendar selected
				Utils.showLongCenteredToast(this, "no calendar selected");
			} else {
				// yes, we have a calendar id

				// get all upcoming lecture units
				LectureItemManager lim = new LectureItemManager(this, Const.db);
				try {
					Cursor c = lim.getFutureFromDb();
					if (c != null)
						while (c.moveToNext()) {
							// get all information out of the cursor
							String title = c.getString(c.getColumnIndex("name"));
							String description = c.getString(c.getColumnIndex("note"));
							String location = c.getString(c.getColumnIndex("location"));
							String startstr = c.getString(c.getColumnIndex("start"));
							String endstr = c.getString(c.getColumnIndex("end"));

							// Log.d("item", title + description + location + startstr + endstr);

							try {
								// format dates
								SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
								Calendar cal = Calendar.getInstance();
								cal.setTime(dateformatter.parse(startstr));
								long start = cal.getTimeInMillis();
								cal.setTime(dateformatter.parse(endstr));
								long end = cal.getTimeInMillis();

								// put it to calendar
								addToCalendar(selectedCalendar, title, description, location, start, end, sbReminder.getProgress());
							} catch (Exception ex) {
								ex.printStackTrace();
							}

						}

				} finally {
					lim.close();
				}

				// a toast to the export!
				Utils.showLongCenteredToast(this, "done");
			}

		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		String appendToCaption;
		if (progress == 0) {
			appendToCaption = getString(R.string.deactivated);
		} else {
			appendToCaption = progress + " min";
		}
		tvReminderCaption.setText(getString(R.string.chooseReminder) + " " + appendToCaption);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// ignore
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// ignore

	}

}
