﻿package de.tum.in.newtumcampus.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.Const;
import de.tum.in.newtumcampus.TumCampus;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.Event;
import de.tum.in.newtumcampus.models.EventManager;
import de.tum.in.newtumcampus.R;

public class EventsTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public EventsTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());

		// inject test data
		Event e = new Event("T1", "Test Event",
				Utils.getDateTime("2011-12-13T14:00:00"),
				Utils.getDateTime("2011-12-13T15:00:00"), "Test location",
				"Test description", "http://www.test.de",
				String.valueOf(R.drawable.icon));

		EventManager em = new EventManager(getActivity(), Const.db);
		em.replaceIntoDb(e);
		em.close();
	}

	@Override
	public void tearDown() throws Exception {
		// remove test data
		EventManager em = new EventManager(getActivity(), Const.db);
		em.removeCache();
		em.close();
		super.tearDown();
	}

	public void testEvents() {
		assertTrue(solo.searchText("Veranstaltungen"));
		solo.clickOnText("Veranstaltungen");

		assertTrue(solo.searchText("Test Event"));
		assertTrue(solo.searchText("Di, 13.12.2011 14:00 - 15:00"));
		assertTrue(solo.searchText("Test location"));

		solo.clickOnText("Test Event");
		assertTrue(solo.searchText("Test description"));
	}

	public void testEventsContextMenu() {
		assertTrue(solo.searchText("Veranstaltungen"));
		solo.clickOnText("Veranstaltungen");

		solo.sendKey(Solo.MENU);
		solo.clickOnText("Aktualisieren");
		solo.sleep(25000);

		assertTrue(solo.searchText("Tag der Informatik"));
		assertTrue(solo.searchText("Fr, 02.12.2011 16:00 - 19:00"));
		assertTrue(solo.searchText("Campus Garching"));

		solo.clickOnText("Tag der Informatik");
		assertTrue(solo.searchText("Universität München lädt ein"));
		solo.goBack();

		solo.clickOnText("Vergangene Veranstaltungen");

		assertTrue(solo.searchText("Rückmeldung"));
		solo.clickOnText("Rückmeldung");

		assertTrue(solo.searchText("Rückmeldefrist"));
		solo.goBack();

		solo.goBack();
		assertTrue(solo.searchText("Hello World"));
	}
}