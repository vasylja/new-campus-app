package de.tum.in.newtumcampus.test;

import java.util.Date;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;

public class LinksTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public LinksTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
		solo.scrollDown();
	}

	public void testLinksList() {
		assertTrue(solo.searchText(solo.getString(R.string.links)));
		solo.clickOnText(solo.getString(R.string.links));

		assertTrue(solo.searchText("Golem"));
		assertTrue(solo.searchText("Heise"));

		solo.clickOnText("Heise");
	}

	public void testLinksCreateDelete() {
		assertTrue(solo.searchText(solo.getString(R.string.links)));
		solo.clickOnText(solo.getString(R.string.links));

		String name = "some name " + new Date();
		solo.enterText(0, "http://www.heise.de");
		solo.enterText(1, name);

		solo.clickOnText(solo.getString(R.string.add));

		assertTrue(solo.searchText(name));
		solo.clickLongOnText(name);

		solo.clickOnText(solo.getString(R.string.yes));

		assertFalse(solo.searchText(name));
	} 
}