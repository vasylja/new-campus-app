package de.tum.in.newtumcampus.test;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;

public class TransportsTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public TransportsTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testTransportsPortrait() {
		assertTrue(solo.searchText(solo.getString(R.string.mvv)));
		solo.clickOnText(solo.getString(R.string.mvv));

		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		_testTransports();

		solo.goBack();
	}

	public void testTransportsLandscape() {
		assertTrue(solo.searchText(solo.getString(R.string.mvv)));
		solo.clickOnText(solo.getString(R.string.mvv));

		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		_testTransports();

		solo.goBack();
	}

	public void testTransportsSearchDelete() {
		assertTrue(solo.searchText(solo.getString(R.string.mvv)));
		solo.clickOnText(solo.getString(R.string.mvv));

		// search station
		solo.enterText(0, "kie");
		solo.sleep(3000);

		assertTrue(solo.searchText("Kieferngarten"));
		solo.clickOnText("Kieferngarten");
		assertTrue(solo.searchText(solo.getString(R.string.departure)+" Kieferngarten"));
		assertTrue(solo.searchText("U6 Klinikum"));

		solo.clickOnText("Marienplatz");
		solo.sleep(3000);

		// delete item
		solo.clickLongOnText("Kieferngarten");

		assertTrue(solo.searchButton(solo.getString(R.string.yes)));
		solo.clickOnText(solo.getString(R.string.yes));

		assertFalse(solo.searchText("Kieferngarten"));
	}

	public void testTransportsContextMenu() {
		assertTrue(solo.searchText(solo.getString(R.string.mvv)));
		solo.clickOnText(solo.getString(R.string.mvv));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.mvv_efa));
	}

	private void _testTransports() {
		// departures
		assertTrue(solo.searchText("Marienplatz"));
		solo.clickOnText("Marienplatz");
		solo.sleep(3000);
		assertTrue(solo.searchText(solo.getString(R.string.departure)+" Marienplatz"));
		assertTrue(solo.searchText("U3 Moosach"));

		assertTrue(solo.searchText("Garching-Forschungszentrum"));
		solo.clickOnText("Garching-Forschungszentrum");
		solo.sleep(3000);

		assertTrue(solo.searchText(solo.getString(R.string.departure)+" Garching-Forschungszentrum"));
		assertTrue(solo.searchText("U6 Klinikum"));
	}
	
}