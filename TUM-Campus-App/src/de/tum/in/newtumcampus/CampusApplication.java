package de.tum.in.newtumcampus;

import org.acra.*;
import org.acra.annotation.*;
import android.app.Application;


/**
 * @author Florian Schulz
 * @source http://code.google.com/p/acra/wiki/AdvancedUsage
 * CampusApplication solves an global exception handler,
 * socketTimeout is increased for G2-Networks,
 * and a TOAST is shown to inform the users.
 */
@ReportsCrashes(formKey = "dFZGRlBhUVgyd292TlpOVk44RnBid0E6MQ",
				mode = ReportingInteractionMode.NOTIFICATION,
				resNotifTickerText = R.string.crash_notif_ticker_text,
				resNotifTitle = R.string.crash_notif_title,
				resNotifText = R.string.crash_notif_text,
				resNotifIcon = android.R.drawable.stat_notify_error, // optional. default is a warning sign
				resDialogText = R.string.crash_dialog_text,
				resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
				resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
				resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
				resDialogOkToast = R.string.crash_dialog_ok_toast, // optional. displays a Toast message when the user accepts to send a report.
				socketTimeout = 8000)
public class CampusApplication extends Application {	
	@Override
	public void onCreate(){
		// initialization of ACRA ReportCrash
		ACRA.init(this);
		super.onCreate();
	}
}