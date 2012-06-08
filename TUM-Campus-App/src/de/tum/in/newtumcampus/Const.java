package de.tum.in.newtumcampus;

/** defines constants for database and settings */
public final class Const {

	/** Identifier of access token */
	public static final String ACCESS_TOKEN = "access_token";

	/** database filename */
	public final static String db = "database.db";

	/** database version used by SQLiteOpenHelper */
	public final static int dbVersion = 1;

	/** Identifier for the German language */
	public static final String DE = "de";

	/** Identifier for the English language */
	public static final String EN = "en";

	/** Action identifier */
	public static final String ACTION_EXTRA = "action";

	/** Message identifier */
	public static final String MESSAGE_EXTRA = "message";

	/** ID identifier */
	public static final String ID_EXTRA = "id";

	/** Title identifier */
	public static final String TITLE_EXTRA = "title";

	/** Action values and filenames *************************************** */

	/** Action value "defaults" */
	public static final String DEFAULTS = "defaults";

	/** Action value "cafeterias" */
	public static final String CAFETERIAS = "cafeterias";

	/** Key for the curricula file */
	public static final String CURRICULA = "curricula";

	/** Action value "documents" */
	public static final String DOCUMENTS = "documents";

	/** Action value "events" */
	public static final String EVENTS = "events";

	/** Action value "feeds" */
	public static final String FEEDS = "feeds";

	/** Action value "noten" */
	public static final String NOTEN = "noten";

	/** Action value "news" */
	public static final String NEWS = "news";

	/** Action value "links" */
	public static final String LINKS = "links";

	/** Action value and filename "organisations" */
	public static final String ORGANISATIONS = "organisations";

	/** Action value and filename "roomfinder" */
	public static final String ROOMFINDER = "roomfinder";

	/** Action value "lecturesTUMOnline" */
	public static final String LECTURES_TUM_ONLINE = "lecturesTUMOnline";

	/** Action value "lectures" */
	public static final String LECTURES = "lectures";

	/** Action value "completed" */
	public static final String COMPLETED = "completed";

	/** Extra value "orgId" */
	public static final String ORG_ID = "orgId";

	/** Extra value "orgParentId" */
	public static final String ORG_PARENT_ID = "orgParentId";

	/** Extra value "ORG_NAME" */
	public static final String ORG_NAME = "orgName";

	/** ********************************************************************* */

	// TODO Check whether there it makes sense to export to strings (because of identifier for SharedPreferences)
	/** LRZ_ID identifier */
	public static final String LRZ_ID = "lrz_id";

	/** TUMONLINE_PASSWORD identifier */
	public static final String TUMONLINE_PASSWORD = "tumonline_password";

	/** Column identifiers ************************************************** */

	/** Identifier of the date column */
	public static final String DATE_COLUMN_DE = "date_de";

	/** Identifier of the id column */
	public static final String ID_COLUMN = "_id";

	/** Identifier of the name column */
	public static final String NAME_COLUMN = "name";

	/** Identifier of the Weekday column */
	public static final String WEEKDAY_COLUMN = "weekday";

	/** Identifier of the Start column */
	public static final String START_DE_COLUMN = "start_de";

	/** Identifier of the End column */
	public static final String END_DE_COLUMN = "end_de";

	/** Identifier of the Location column */
	public static final String LOCATION_COLUMN = "location";

	/** Identifier of the Description column */
	public static final String DESCRIPTION_COLUMN = "description";

	/** Identifier of the Image column */
	public static final String IMAGE_COLUMN = "image";

	/** Identifier of the Link column */
	public static final String LINK_COLUMN = "link";

	/** Identifier of the Transport column */
	public static final String TRANSPORT_COLUMN = "transport";

	/** Identifier of the Address column */
	public static final String ADDRESS_COLUMN = "address";

	/** Identifier of the Hours column */
	public static final String HOURS_COLUMN = "hours";

	/** Identifier of the Remark column */
	public static final String REMARK_COLUMN = "remark";

	/** Identifier of the Room column */
	public static final String ROOM_COLUMN = "room";

	/** Identifier of the Note ("Notiz" not Grade) column */
	public static final String NOTE_COLUMN = "note";

	/** Identifier of the LectureId column */
	public static final String LECTURE_ID_COLUMN = "lectureId";

	/** Identifier of the module column */
	public static final String MODULE_COLUMN = "module";

	/** Identifier of the URL column */
	public static final String URL_COLUMN = "url";

	// TODO IMPORTANT Check whether "start_dt" and "start_de" are actually the same
	/** Lecture starting date */
	public static final String START_DT_COLUMN = "start_dt";

	/** Lecture ending date */
	public static final String END_DT_COLUMN = "end_dt";

	/** ****************************************************************** */

	/** LectureId column values ****************************************** */

	/** Vacation */
	public static final String VACATION = "vacation";

	/** Holiday */
	public static final String HOLIDAY = "holiday";

	/** /** ****************************************************************** */

	/** Error (used in extras) */
	public static final String ERROR = "error";

	/** constants for application settings */
	public static final class Settings {
		/** filter cafeterias by a substring */
		public final static String cafeteriaFilter = "cafeteriaFilter";

		/** activate debug mode (debug activity and detailed error handling) */
		public final static String debug = "debug";

		/** enable silence service, silence the mobile during lectures */
		public final static String silence = "silence";

		/** Settings keys */
		public static final String TUMONLINE_SETTINGS_KEY = "tumonline";
		public static final String APP_DETAILS_SETTINGS_KEY = "app_details";
		public static final String MARKET_SETTINGS_KEY = "market";
	}
}