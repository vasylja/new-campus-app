﻿package de.tum.in.newtumcampus.test;

import java.util.Date;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.Const;
import de.tum.in.newtumcampus.TumCampus;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.Feed;
import de.tum.in.newtumcampus.models.FeedItem;
import de.tum.in.newtumcampus.models.FeedItemManager;
import de.tum.in.newtumcampus.models.FeedManager;

public class FeedsTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	private int feedId;

	public FeedsTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());

		FeedManager fm = new FeedManager(getActivity(), Const.db);
		feedId = fm.insertUpdateIntoDb(new Feed("Test feed",
				"http://www.test.de"));
		fm.close();

		// inject test data
		FeedItem fi = new FeedItem(feedId, "Test message",
				"http://www.test.de", "Test description",
				Utils.getDateTime("2011-05-04T14:00:00"), "");

		FeedItemManager fim = new FeedItemManager(getActivity(), Const.db);
		fim.insertIntoDb(fi);
		fim.close();
	}

	@Override
	public void tearDown() throws Exception {
		// remove test data
		FeedItemManager fim = new FeedItemManager(getActivity(), Const.db);
		fim.removeCache();
		fim.close();

		FeedManager fm = new FeedManager(getActivity(), Const.db);
		fm.deleteFromDb(feedId);
		fm.close();
		super.tearDown();
	}

	public void testFeedsList() {
		assertTrue(solo.searchText("RSS-Feeds"));
		solo.clickOnText("RSS-Feeds");

		assertTrue(solo.searchText("Feed auswählen"));

		assertTrue(solo.searchText("Test feed"));
		solo.clickOnText("Test feed");

		assertTrue(solo.searchText("Nachrichten: Test feed"));
		assertTrue(solo.searchText("Test message"));
		assertTrue(solo.searchText("Test description"));

		solo.goBack();
		assertTrue(solo.searchText("Hello World"));
	}

	public void testFeedsContextMenu() {
		assertTrue(solo.searchText("RSS-Feeds"));
		solo.clickOnText("RSS-Feeds");

		assertTrue(solo.searchText("Feed auswählen"));

		assertTrue(solo.searchText("Spiegel"));
		solo.clickOnText("Spiegel");

		solo.sendKey(Solo.MENU);
		solo.clickOnText("Aktualisieren");
		solo.sleep(10000);

		solo.clickInList(0, 0);
		solo.sleep(2000);
	}

	public void testFeedsCreateDelete() {
		assertTrue(solo.searchText("RSS-Feeds"));
		solo.clickOnText("RSS-Feeds");

		assertTrue(solo.searchText("Feed auswählen"));

		// scrollDown not working here
		solo.drag(200, 200, 600, 200, 40);

		String name = "some name " + new Date();
		solo.enterText(0, "http://www.heise.de");
		solo.enterText(1, name);

		solo.clickOnText("Hinzufügen");

		assertTrue(solo.searchText(name));
		solo.clickLongOnText(name);

		assertTrue(solo.searchButton("Ja"));
		solo.clickOnText("Ja");

		assertFalse(solo.searchText(name));
	}
}