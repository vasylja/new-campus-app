package de.tum.in.newtumcampus;

import java.util.regex.Pattern;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import de.tum.in.newtumcampus.models.LocationManager;

/** Activity to show opening hours */
public class Hours extends Activity implements OnItemClickListener, ViewBinder {

	private String[] names;

	private String[] categories;

	private static int position = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hours);
	}

	@Override
	public void onStart() {
		super.onStart();

		names = new String[] { getString(R.string.libraries), getString(R.string.information),
				getString(R.string.mensa_garching), getString(R.string.mensa_großhadern),
				getString(R.string.mensa_city), getString(R.string.mensa_pasing),
				getString(R.string.mensa_weihenstephan) };

		categories = getString(R.string.facility_categories_splitted).split(",");

		// show all categories
		ListView lv = (ListView) findViewById(R.id.listView);
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names));
		lv.setOnItemClickListener(this);

		if (position == -1) {
			SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slider);
			sd.open();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// refresh current selected category on resume (rotate)
		if (position != -1) {
			onItemClick(null, null, position, 0);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		position = pos;

		SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slider);
		if (sd.isOpened()) {
			sd.animateClose();
		}
		setTitle(getString(R.string.opening_hours) + ": " + names[position]);

		// click on category in list
		LocationManager lm = new LocationManager(this, Const.db);
		Cursor c = lm.getAllHoursFromDb(categories[position]);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, c,
				c.getColumnNames(), new int[] { android.R.id.text1, android.R.id.text2 }) {

			@Override
			public boolean isEnabled(int position) {
				// disable onclick
				return false;
			}
		};
		adapter.setViewBinder(this);

		ListView lv2 = (ListView) findViewById(R.id.listView2);
		lv2.setAdapter(adapter);
		lm.close();
	}

	/** change presentation of locations in the list */
	@Override
	public boolean setViewValue(View view, Cursor c, int index) {
		if (view.getId() == android.R.id.text2) {
			String transport = c.getString(c.getColumnIndex(Const.TRANSPORT_COLUMN));
			String address = c.getString(c.getColumnIndex(Const.ADDRESS_COLUMN));

			String hours = c.getString(c.getColumnIndex(Const.HOURS_COLUMN));
			String remark = c.getString(c.getColumnIndex(Const.REMARK_COLUMN));
			String room = c.getString(c.getColumnIndex(Const.ROOM_COLUMN));

			StringBuilder sb = new StringBuilder(hours + "\n" + address);
			if (room.length() > 0) {
				sb.append(", " + room);
			}
			if (transport.length() > 0) {
				sb.append(" (" + transport + ")");
			}
			if (remark.length() > 0) {
				sb.append("\n" + remark.replaceAll("\\\\n", "\n"));
			}
			TextView tv = (TextView) view;
			tv.setText(sb.toString());

			// linkify email addresses and phone numbers (e.g. 089-123456)
			// don't linkify room numbers 00.01.123
			Linkify.addLinks(tv, Linkify.EMAIL_ADDRESSES);
			Linkify.addLinks(tv, Pattern.compile("[0-9-]{6,}"), "tel:");
			return true;
		}
		return false;
	}
}