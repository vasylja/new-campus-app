package de.tum.in.newtumcampus.services;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.Toast;
import de.tum.in.newtumcampus.Const;
import de.tum.in.newtumcampus.TumCampus;
import de.tum.in.newtumcampus.common.Utils;
import de.tum.in.newtumcampus.models.CafeteriaManager;
import de.tum.in.newtumcampus.models.CafeteriaMenuManager;
import de.tum.in.newtumcampus.models.EventManager;
import de.tum.in.newtumcampus.models.FeedItemManager;
import de.tum.in.newtumcampus.models.FeedManager;
import de.tum.in.newtumcampus.models.LinkManager;
import de.tum.in.newtumcampus.models.NewsManager;
import de.tum.in.newtumcampus.models.OrganisationManager;
import de.tum.in.newtumcampus.models.SyncManager;
import de.tum.in.newtumcampus.R;

/**
 * Service used to download files from external pages
 */
public class DownloadService extends IntentService {

	/**
	 * Indicator to avoid starting new downloads
	 */
	private volatile boolean destroyed = false;

	/**
	 * Download broadcast identifier
	 */
	public final static String broadcast = "de.tum.in.newtumcampus.intent.action.BROADCAST_DOWNLOAD";

	/**
	 * default init (run intent in new thread)
	 */
	public DownloadService() {
		super("DownloadService");
	}

	/**
	 * Notification message
	 */
	private String message = "";

	/**
	 * Default receiver: output feedback as toast and resume activity
	 */
	public static BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (!intent.getAction().equals(DownloadService.broadcast)) {
				return;
			}
			if (intent.getStringExtra("action").length() != 0) {
				Toast.makeText(context, intent.getStringExtra("message"), Toast.LENGTH_LONG).show();

				// wait until images are loaded
				synchronized (this) {
					try {
						int count = 0;
						while (Utils.openDownloads > 0 && count < 10) {
							Utils.log(String.valueOf(Utils.openDownloads));
							wait(1000);
							count++;
						}
					} catch (Exception e) {
						Utils.log(e, "");
					}
				}

				// resume activity
				Intent intent2 = new Intent(context, context.getClass());
				intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intent2);
			}
		}
	};

	@Override
	protected void onHandleIntent(Intent intent) {

		// show download notification
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(ns);

		Notification notification = new Notification(android.R.drawable.stat_sys_download,
				getString(R.string.updating), System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TumCampus.class), 0);

		notification.setLatestEventInfo(this, getString(R.string.tum_campus_download), "", contentIntent);
		nm.notify(1, notification);

		logMessage(getString(R.string.updating), "");

		String action = intent.getStringExtra("action");
		Utils.log(action);

		boolean force = false;
		if (action != null) {
			force = true;
		}

		// download all or only one action
		if ((action == null || action.equals("feeds")) && !destroyed) {
			logMessage(getString(R.string.rss_feeds) + ", ", "");
			downloadFeeds(force);
		}
		if ((action == null || action.equals("news")) && !destroyed) {
			logMessage(getString(R.string.news) + ", ", "");
			downloadNews(force);
		}
		if ((action == null || action.equals("events")) && !destroyed) {
			logMessage(getString(R.string.events) + ", ", "");
			downloadEvents(force);
		}
		if ((action == null || action.equals("cafeterias")) && !destroyed) {
			logMessage(getString(R.string.cafeterias) + ", ", "");
			downloadCafeterias(force);
		}
		if ((action == null || action.equals("links")) && !destroyed) {
			logMessage(getString(R.string.links) + ", ", "");
			downloadLinks();
		}
		if ((action == null || action.equals("organisations")) && !destroyed) {
			logMessage(getString(R.string.organisations) + ", ", "");
			downloadOrganisations();
		}
		// TODO Check whether to change "completed"
		logMessage(getString(R.string.completed), "completed");
		nm.cancel(1);
	}

	/**
	 * Download items for all feeds
	 * 
	 * <pre>
	 * @param force True to force download over normal sync period, else false
	 * </pre>
	 */
	public void downloadFeeds(boolean force) {
		FeedManager nm = new FeedManager(this, Const.db);
		List<Integer> list = nm.getAllIdsFromDb();
		nm.close();

		FeedItemManager nim = new FeedItemManager(this, Const.db);
		for (int id : list) {
			if (destroyed) {
				break;
			}
			try {
				nim.downloadFromExternal(id, false, force);
			} catch (Exception e) {
				logErrorMessage(e, nim.lastInfo);
			}
		}
		nim.close();
	}

	/**
	 * Download news elements
	 * 
	 * <pre>
	 * @param force True to force download over normal sync period, else false
	 * </pre>
	 */
	public void downloadNews(boolean force) {
		NewsManager nm = new NewsManager(this, Const.db);
		try {
			nm.downloadFromExternal(force);
		} catch (Exception e) {
			logErrorMessage(e, "");
		}
		nm.close();
	}

	/**
	 * Download events
	 * 
	 * <pre>
	 * @param force True to force download over normal sync period, else false
	 * </pre>
	 */
	public void downloadEvents(boolean force) {
		EventManager em = new EventManager(this, Const.db);
		try {
			em.downloadFromExternal(force);
		} catch (Exception e) {
			logErrorMessage(e, "");
		}
		em.close();
	}

	/**
	 * Download cafeterias
	 * 
	 * <pre>
	 * @param force True to force download over normal sync period, else false
	 * </pre>
	 */
	public void downloadCafeterias(boolean force) {
		CafeteriaManager cm = new CafeteriaManager(this, Const.db);
		CafeteriaMenuManager cmm = new CafeteriaMenuManager(this, Const.db);
		try {
			cm.downloadFromExternal(force);
			cmm.downloadFromExternal(cm.getAllIdsFromDb(), force);
		} catch (Exception e) {
			logErrorMessage(e, "");
		}
		cmm.close();
		cm.close();
	}

	/**
	 * Download missing icons for links
	 */
	public void downloadLinks() {
		LinkManager lm = new LinkManager(this, Const.db);
		try {
			lm.downloadMissingIcons();
		} catch (Exception e) {
			logErrorMessage(e, "");
		}
		lm.close();
	}

	/**
	 * Download OrganisationTree from TUMOnline
	 * 
	 * @throws Exception
	 */
	public void downloadOrganisations() {
		OrganisationManager lm = new OrganisationManager(this);

		String accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("access_token", null);

		// if no token ask if token want to be set
		if (accessToken == null) {
			logErrorMessage(new Exception(getString(R.string.dialog_access_token_missing2)), "");
			return;
		}
		try {
			lm.downloadFromExternal(accessToken);
		} catch (Exception e) {
			logErrorMessage(e, "");
		}
		// }
	}

	/**
	 * Send notification message to service caller
	 * 
	 * <pre>
	 * @param e Exception, get message and stacktrace from 
	 * @param info Notification info, appended to exception message
	 * </pre>
	 */
	public void logErrorMessage(Exception e, String info) {
		Utils.log(e, info);

		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));

		String errorMessage = e.getMessage();
		if (Utils.getSettingBool(this, Const.Settings.debug)) {
			errorMessage += sw.toString();
		}
		errorMessage = getString(R.string.error) + ": " + errorMessage + " " + info;

		Intent intentSend = new Intent();
		intentSend.setAction(broadcast);
		intentSend.putExtra("message", errorMessage);
		intentSend.putExtra("action", "error");
		sendBroadcast(intentSend);
	}

	/**
	 * Send notification message to service caller
	 * 
	 * <pre>
	 * @param message Notification message
	 * @param action Notification action (e.g. error, completed)
	 * </pre>
	 */
	public void logMessage(String message, String action) {
		this.message += message;

		Intent intentSend = new Intent();
		intentSend.setAction(broadcast);
		intentSend.putExtra("message", this.message);
		intentSend.putExtra("action", action);
		sendBroadcast(intentSend);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// don't start new downloads
		destroyed = true;
		Utils.log("");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.log("");

		try {
			// check if sd card available
			Utils.getCacheDir("");

			// init sync table
			SyncManager sm = new SyncManager(this, Const.db);
			sm.close();
		} catch (Exception e) {
			logErrorMessage(e, "");

			// don't start new downloads
			destroyed = true;
		}
	}
}