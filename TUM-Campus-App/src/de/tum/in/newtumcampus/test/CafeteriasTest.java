package de.tum.in.newtumcampus.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;

/*
 * Florian Schulz
 * changed of xml-external reasons, some not (external information)
 * TODO Review Vasyl
 */
public class CafeteriasTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public CafeteriasTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());

		assertTrue(solo.searchText(solo.getString(R.string.menues)));
		solo.clickOnText(solo.getString(R.string.menues));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.update));
		solo.sleep(10000);
		solo.goBack();
	}

	public void testCafeteriasPortrait() {
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
		solo.clickOnText(solo.getString(R.string.menues));

		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		_testCafeterias();

		solo.goBack();
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
	}

	public void testCafeteriasLandscape() {
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
		solo.clickOnText(solo.getString(R.string.menues));

		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		_testCafeterias();

		solo.goBack();
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
	}

	public void testCafeteriasSettings() {
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
		solo.clickOnText(solo.getString(R.string.menues));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.settings));
		solo.clickOnText(solo.getString(R.string.mensa_filter));
		solo.clearEditText(0);
		solo.enterText(0, "Garching");
		
		solo.goBack();
		solo.clickOnText("OK");
		solo.goBack();
		assertFalse(solo.searchText("München"));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.settings));
		solo.clickOnText(solo.getString(R.string.mensa_filter));
		solo.clearEditText(0);
		solo.goBack();
		solo.clickOnText("OK");
		solo.goBack();
		assertTrue(solo.searchText("München"));
	}

	public void testCafeteriasContextMenu() {
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
		solo.clickOnText(solo.getString(R.string.menues));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.prices));
	}

	private void _testCafeterias() {
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
		solo.clickOnText(solo.getString(R.string.menues));
		assertTrue(solo.searchText(solo.getString(R.string.mensa_garching)));
		solo.clickOnText(solo.getString(R.string.mensa_garching));

		Calendar calendar = Calendar.getInstance();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY) {
			calendar.add(Calendar.DATE, 2);
		}
		if (dayOfWeek == Calendar.SUNDAY) {
			calendar.add(Calendar.DATE, 1);
		}

		SimpleDateFormat de = new SimpleDateFormat("dd.MM.yyyy");
		String today = de.format(calendar.getTime());
		assertTrue(solo.searchText(solo.getString(R.string.mensa_garching)+": " + today));
		assertTrue(solo.searchText("Beilagen"));
		assertTrue(solo.searchText("Tagesgericht 1"));

		assertTrue(solo.searchText(solo.getString(R.string.choose_date)));
		solo.clickOnText(solo.getString(R.string.choose_date));

		if (dayOfWeek == Calendar.FRIDAY) {
			calendar.add(Calendar.DATE, 3);
		} else {
			calendar.add(Calendar.DATE, 1);
		}
		String tomorrow = de.format(calendar.getTime());

		assertTrue(solo.searchText(tomorrow));
		solo.clickOnText(tomorrow);

		assertTrue(solo.searchText(solo.getString(R.string.mensa_garching)+": " + tomorrow));
	}
	
}