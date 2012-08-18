package de.tum.in.newtumcampus.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;

/**
 * @author Florian Schulz
 * @solves DebugTest
 * tests debug function
 * TODO Review Vasyl
 */

public class DebugTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public DebugTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testDebug() {
		assertFalse(solo.searchText(solo.getString(R.string.debug)));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.settings));
		solo.clickOnText(solo.getString(R.string.debug));
		solo.goBack();

		assertTrue(solo.searchText(solo.getString(R.string.debug)));
		solo.clickOnText(solo.getString(R.string.debug));

		assertTrue(solo.searchText(solo.getString(R.string.debug_sqllite)));

		solo.clickOnText(solo.getString(R.string.debug_syncs));
		solo.clickOnText(solo.getString(R.string.debug_cafeterias));
		solo.clickOnText(solo.getString(R.string.debug_cafeterias_menus));
		solo.clickOnText(solo.getString(R.string.debug_feeds));
		solo.clickOnText(solo.getString(R.string.debug_feeds_items));
		solo.clickOnText(solo.getString(R.string.debug_lectures));
		solo.clickOnText(solo.getString(R.string.debug_lectures_items));
		solo.clickOnText(solo.getString(R.string.debug_links));
		solo.clickOnText(solo.getString(R.string.debug_events));
		solo.clickOnText(solo.getString(R.string.debug_news));
		solo.clickOnText(solo.getString(R.string.debug_time));
		solo.clickOnText(solo.getString(R.string.debug_locations));
		solo.clickOnText(solo.getString(R.string.debug_master));

		solo.goBack();
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.settings));
		solo.clickOnText(solo.getString(R.string.debug));
		solo.goBack();

		assertFalse(solo.searchText(solo.getString(R.string.debug)));
	}
}