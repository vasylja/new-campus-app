package de.tum.in.newtumcampus;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import de.tum.in.newtumcampus.models.EventManager;

/**
 * Activity to show event details (name, location, image, description, etc.)
 */
public class EventsDetails extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events_details);

		// get event details from db
		EventManager em = new EventManager(this, Const.db);
		Cursor c = em.getDetailsFromDb(getIntent().getStringExtra(Const.ID_EXTRA));

		if (c.moveToNext()) {
			String description = c.getString(c.getColumnIndex(Const.DESCRIPTION_COLUMN));
			String image = c.getString(c.getColumnIndex(Const.IMAGE_COLUMN));

			String[] weekDays = getString(R.string.week_splitted).split(",");

			setTitle(c.getString(c.getColumnIndex(Const.NAME_COLUMN)));

			/**
			 * <pre>
			 * show infos as:
			 * Week-day, Start DateTime - End Time
			 * Location
			 * Link
			 * </pre>
			 */
			String infos = weekDays[c.getInt(c.getColumnIndex(Const.WEEKDAY_COLUMN))];
			infos += ", " + c.getString(c.getColumnIndex(Const.START_DE_COLUMN)) + " - " + c.getString(c.getColumnIndex(Const.END_DE_COLUMN))
					+ "\n";
			infos += c.getString(c.getColumnIndex(Const.LOCATION_COLUMN)) + "\n";
			infos += c.getString(c.getColumnIndex(Const.LINK_COLUMN));

			TextView tv = (TextView) findViewById(R.id.infos);
			tv.setText(infos.trim());

			tv = (TextView) findViewById(R.id.description);
			tv.setText(description);

			ImageView iv = (ImageView) findViewById(R.id.image);
			iv.setImageURI(Uri.parse(image));

			// resize image: 350 x height adapted in aspect ratio
			if (iv.getDrawable() != null) {
				double ratio = (double) iv.getDrawable().getIntrinsicWidth()
						/ (double) iv.getDrawable().getIntrinsicHeight();

				int screen = getWindowManager().getDefaultDisplay().getWidth();
				int width = Math.min((int) (screen * 0.9), 375);
				iv.getLayoutParams().width = width;
				iv.getLayoutParams().height = (int) Math.floor(width / ratio);
			}
		}
		em.close();
	}
}