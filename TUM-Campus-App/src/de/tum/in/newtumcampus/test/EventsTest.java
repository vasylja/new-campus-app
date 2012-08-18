package de.tum.in.newtumcampus.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.Event;
import de.tum.in.newtumcampus.models.EventManager;

/**
 * @author first team / Florian Schulz
 * @solves EventsTest
 * checks some functions of events
 * TODO Review Vasyl
 */

public class EventsTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public EventsTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());

		// inject test data
		Event e = new Event("T1", "Test Event", Utils.getDateTime("2011-12-13T14:00:00"),
				Utils.getDateTime("2011-12-13T15:00:00"), "Test location", "Test description", "http://www.test.de",
				String.valueOf(R.drawable.icon));

		EventManager em = new EventManager(getActivity());
		em.replaceIntoDb(e);
	}

	@Override
	public void tearDown() throws Exception {
		// remove test data
		EventManager em = new EventManager(getActivity());
		em.removeCache();
		super.tearDown();
	}

	public void testEvents() {
		assertTrue(solo.searchText(solo.getString(R.string.events)));
		solo.clickOnText(solo.getString(R.string.events));

		assertTrue(solo.searchText("Test Event"));
		assertTrue(solo.searchText("Di, 13.12.2011 14:00 - 15:00"));
		assertTrue(solo.searchText("Test location"));

		solo.clickOnText("Test Event");
		assertTrue(solo.searchText("Test description"));
	}

	public void testEventsContextMenu() {
		assertTrue(solo.searchText(solo.getString(R.string.events)));
		solo.clickOnText(solo.getString(R.string.events));

		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getString(R.string.update));
		solo.sleep(25000);
		assertTrue(solo.searchText("Tag der offenen Tür"));
		assertTrue(solo.searchText("Sa, 27.10.2012 02:00 - 09:00"));
		assertTrue(solo.searchText("Campus Garching"));

		solo.clickOnText("Tag der offenen Tür");
		assertTrue(solo.searchText("steht wieder an!"));
		solo.goBack();

		solo.clickOnText("Vergangene Veranstaltungen");

		solo.goBack();
	}
}