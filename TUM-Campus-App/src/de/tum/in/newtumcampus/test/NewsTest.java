package de.tum.in.newtumcampus.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.News;
import de.tum.in.newtumcampus.models.NewsManager;

public class NewsTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public NewsTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());

		// inject test data
		News n = new News("N1", "Test message", "http://www.test.de", "", Utils.getDate("2011-12-13"));

		NewsManager nm = new NewsManager(getActivity());
		nm.replaceIntoDb(n);
	}

	@Override
	public void tearDown() throws Exception {
		// remove test data
		NewsManager nm = new NewsManager(getActivity());
		nm.removeCache();
		super.tearDown();
	}

	public void testNews() {
		assertTrue(solo.searchText(solo.getString(R.string.news)));

		solo.clickOnText(solo.getString(R.string.news));
		assertTrue(solo.searchText("Test message"));
		assertTrue(solo.searchText("13.12.2011"));

		solo.clickOnText("Test message");
	}

	public void testNewsContextMenu() {
		assertTrue(solo.searchText(solo.getString(R.string.news)));
		solo.clickOnText(solo.getString(R.string.news));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.update));
		solo.sleep(10000);

		assertTrue(solo.searchText("Umfrage"));
		assertTrue(solo.searchText("09.07.2012"));

		assertTrue(solo.searchText(solo.getString(R.string.opening_hours)));
		solo.clickOnText(solo.getString(R.string.opening_hours));
	}
}