package de.tum.in.newtumcampus.test;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.LectureItem;
import de.tum.in.newtumcampus.models.LectureItemManager;
import de.tum.in.newtumcampus.models.LectureManager;

public class LecturesTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public LecturesTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());

		// inject test data
		LectureItem li = new LectureItem("T1", "T1", Utils.getDateTime("2011-05-04T14:00:00"),
				Utils.getDateTime("2011-05-04T16:00:00"), "CSCW 2", "IN2119", "01.07.023", "", "", "T1");

		LectureItem li2 = new LectureItem.Holiday("TH1", Utils.getDate("2011-12-13"), "Some Holiday");
		LectureItem li3 = new LectureItem.Vacation("VAC2", Utils.getDate("2012-12-13"), Utils.getDate("2012-12-24"), "Some Vacation");

		LectureItemManager lim = new LectureItemManager(getActivity());
		lim.replaceIntoDb(li);
		lim.replaceIntoDb(li2);
		lim.replaceIntoDb(li3);

		LectureManager lm = new LectureManager(getActivity());
		lm.updateLectures();
	}

	@Override
	public void tearDown() throws Exception {
		// remove test data
		LectureItemManager lim = new LectureItemManager(getActivity());
		lim.deleteLectureFromDb("T1");
		lim.deleteLectureFromDb("TH1");
		lim.deleteItemFromDb("VAC2");

		LectureManager lm = new LectureManager(getActivity());
		lm.deleteItemFromDb("T1");
		super.tearDown();
	}

	public void testLecturesPortrait() {
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));

		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		_testLectures();
	}

	public void testLecturesLandscape() {
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));

		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		_testLectures();
	}

	public void testLecturesLink() {
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));

		assertTrue(solo.searchText(solo.getString(R.string.next_lectures)));

		assertTrue(solo.searchText("CSCW"));
		solo.clickOnText("CSCW");
	}

	public void testLecturesItemDelete() {
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));

		solo.clickOnText("Feiertag");

		assertTrue(solo.searchText("Some Holiday"));
		solo.clickLongOnText("Some Holiday");

		assertTrue(solo.searchButton(solo.getString(R.string.yes)));
		solo.clickLongOnText(solo.getString(R.string.yes));

		assertFalse(solo.searchText("Some Holiday"));
	}

	public void testLecturesDelete() {
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));

		assertTrue(solo.searchText("CSCW"));
		solo.clickLongOnText("CSCW");

		assertTrue(solo.searchButton(solo.getString(R.string.yes)));
		solo.clickLongOnText(solo.getString(R.string.yes));

		assertFalse(solo.searchText("CSCW"));
	}

	public void testLecturesContextMenu() {
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));
		solo.sleep(2000);
	}

	private void _testLectures() {
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));
		assertTrue(solo.searchText("Feiertag"));
		solo.clickOnText("Feiertag");
		assertTrue(solo.searchText("Frohnleichnam"));
		assertTrue(solo.searchText(", 07.06.2012")); //without day! - language reasons

		assertTrue(solo.searchText("Ferien"));
		solo.clickOnText("Ferien");
		assertTrue(solo.searchText("Some Vacation"));
		assertTrue(solo.searchText("13.12.2012 - 24.12.2012"));

		solo.clickOnText("CSCW");
		assertTrue(solo.searchText("Mi, 04.05.2011 14:00 - 16:00, 01.07.023"));
		assertTrue(solo.searchText("IN2119"));
		solo.clickOnText("IN2119");	
	}

	/**
	 * @author Florian Schulz
	 * tests slider
	 * TODO Review Vasyl
	 */
	public void testSlider(){
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));
		assertTrue(solo.searchText(solo.getString(R.string.slide_lectures)));
		solo.clickOnText(solo.getString(R.string.slide_lectures));
		assertTrue(solo.searchText(solo.getString(R.string.my_lectures)));
		solo.clickOnText(solo.getString(R.string.my_lectures));
		solo.goBack();
		solo.goBack();
		assertTrue(solo.searchText(solo.getString(R.string.search_lectures)));
		solo.clickOnText(solo.getString(R.string.search_lectures));
		solo.goBack();
		/* if calenderexport is released
		assertTrue(solo.searchText(solo.getString(R.string.export2calendar)));
		solo.clickOnText(solo.getString(R.string.export2calendar));
		solo.goBack();
		*/
	}
}