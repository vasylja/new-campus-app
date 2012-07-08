package de.tum.in.newtumcampus.common;

import de.tum.in.newtumcampus.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Helper class to access predefined alert dialogs.
 * 
 * @author Vincenz Doelle
 */
public class Dialogs {

	/**
	 * Shows a dialog asking to switch to another activity in order perform some actions there.
	 * 
	 * @param context The current context.
	 * @param parent The parent activity.
	 * @param msg The message to be displayed.
	 * @param intent The target intent if the user chooses "YES"
	 */
	public static void showIntentSwitchDialog(Context context, final Activity parent, String msg, final Intent intent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton(((Activity) context).getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						parent.startActivity(intent);
					}
				})
				.setNegativeButton(((Activity) context).getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						parent.finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
