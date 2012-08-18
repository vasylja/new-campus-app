package de.tum.in.newtumcampus.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.newtumcampus.Const;
import de.tum.in.newtumcampus.R;
import de.tum.in.newtumcampus.TumCampus;
import de.tum.in.newtumcampus.common.Utils;

public class ImportTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public ImportTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());

		Utils.getCacheDir(Const.LINKS);
		Utils.getCacheDir("rss");
		Utils.getCacheDir(Const.LECTURES);

		String path = Environment.getExternalStorageDirectory().getPath() + "/tumcampus/";

		BufferedWriter out = new BufferedWriter(new FileWriter(path + "links/test1.url"));
		out.write("[InternetShortcut]\nURL=http://www.in.tum.de/");
		out.close();

		out = new BufferedWriter(new FileWriter(path + "rss/test2.url"));
		out.write("[InternetShortcut]\nURL=http://www.spiegel.de/schlagzeilen/index.rss");
		out.close();

		out = new BufferedWriter(new FileWriter(path + "lectures/test3.csv"));
		out.write("WOCHENTAG;DATUM;VON;BIS;LV_NUMMER;TITEL;ORT;TERMIN_TYP;ANMERKUNG;URL\n");
		out.write("Do;14.07.2011;12:00;14:00;12345;Vorlesung1 (IN0007);00.01.1234;;;");
		out.close();
	}

	@Override
	public void tearDown() throws Exception {
		String path = Environment.getExternalStorageDirectory().getPath() + "/tumcampus/";

		new File(path + "links/test1.url").delete();
		new File(path + "rss/test2.url").delete();
		new File(path + "lectures/test3.csv").delete();

		super.tearDown();
	}

	public void testImportLectures() {
		assertTrue(solo.searchButton(solo.getString(R.string.data_and_settings)));
		solo.clickOnButton(solo.getString(R.string.data_and_settings));

		solo.clickOnButton(solo.getString(R.string.import_lectures_from_sd_card));
		solo.sleep(1000);

		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		solo.clickOnText(solo.getString(R.string.lectures));

		solo.clickOnText("Feiertag");

		assertTrue(solo.searchText("Vorlesung1"));
		solo.clickOnText("Vorlesung1");

		assertTrue(solo.searchText("Do, 14.07.2011 12:00 - 14:00, 00.01.1234"));
		assertTrue(solo.searchText("IN0007"));
	}

	public void testImportLinks() {
		assertTrue(solo.searchButton(solo.getString(R.string.data_and_settings)));
		solo.clickOnButton(solo.getString(R.string.data_and_settings));

		solo.clickOnButton(solo.getString(R.string.import_links));
		solo.sleep(1000);
		solo.scrollDown();

		assertTrue(solo.searchText(solo.getString(R.string.links)));
		solo.clickOnText(solo.getString(R.string.links));

		assertTrue(solo.searchText("test1"));
		solo.clickOnText("test1");
		solo.sleep(2000);
	}

	public void testImportFeeds() {
		assertTrue(solo.searchButton(solo.getString(R.string.data_and_settings)));
		solo.clickOnButton(solo.getString(R.string.data_and_settings));

		solo.clickOnButton(solo.getString(R.string.import_rss_feed));
		solo.sleep(1000);

		assertTrue(solo.searchText(solo.getString(R.string.rss_feeds)));
		solo.clickOnText(solo.getString(R.string.rss_feeds));

		assertTrue(solo.searchText("test2"));
		solo.clickOnText("test2");
		solo.sleep(2000);
	}
	
}