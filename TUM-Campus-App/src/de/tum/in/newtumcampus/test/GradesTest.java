package de.tum.in.newtumcampus.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;

/**
 * @author Florian Schulz
 * @sovles GradesTest
 */

public class GradesTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public GradesTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
		solo.scrollDown();
	}

	public void testSpinner() {
		solo.clickOnText("" + R.string.all_programs);
		solo.clickOnRadioButton(1);
	}

}
