package de.tum.in.newtumcampus.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;

public class PlansTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public PlansTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testPlansList() {
		assertTrue(solo.searchText(solo.getString(R.string.area_maps)));
		solo.clickOnText(solo.getString(R.string.area_maps));

		assertTrue(solo.searchText(solo.getString(R.string.campus_garching)));
		assertTrue(solo.searchText("Campus Stammgelände"));
		assertTrue(solo.searchText(solo.getString(R.string.mvv_fast_train_net)));

		solo.clickOnText(solo.getString(R.string.campus_garching));
		assertTrue(solo.searchText(solo.getString(R.string.plan)+""+solo.getString(R.string.campus_garching)));

		solo.clickOnText(solo.getString(R.string.choose_plan));
		assertTrue(solo.searchText(solo.getString(R.string.mvv_fast_train_net)));

		solo.clickOnText(solo.getString(R.string.mvv_fast_train_net));
		assertTrue(solo.searchText(solo.getString(R.string.plan)+""+solo.getString(R.string.mvv_fast_train_net)));
	}
}