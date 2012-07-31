package de.tum.in.newtumcampus.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;
/**
 * @author first team / Florian Schulz
 * @solves HoursTest
 * tests hours
 */
public class HoursTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public HoursTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
		solo.scrollDown();
	}

	public void testHoursList() {
		assertTrue(solo.searchText(solo.getString(R.string.opening_hours)));
		solo.clickOnText(solo.getString(R.string.opening_hours));

		assertTrue(solo.searchText(solo.getString(R.string.libraries)));
		assertTrue(solo.searchText(solo.getString(R.string.mensa_garching)));
		assertTrue(solo.searchText(solo.getString(R.string.information)));

		solo.clickOnText(solo.getString(R.string.libraries));
		assertTrue(solo.searchText(solo.getString(R.string.opening_hours)+": "+solo.getString(R.string.libraries)));
		assertTrue(solo.searchText("Boltzmannstr. 3, Garching"));

		solo.clickOnText(solo.getString(R.string.choose_category));
		assertTrue(solo.searchText(solo.getString(R.string.mensa_garching)));

		solo.clickOnText(solo.getString(R.string.mensa_garching));
		assertTrue(solo.searchText(solo.getString(R.string.opening_hours)+": "+solo.getString(R.string.mensa_garching)));
	}
}