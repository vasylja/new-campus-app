package de.tum.in.newtumcampus;

import org.acra.*;
import org.acra.annotation.*;
import android.app.Application;

@ReportsCrashes(formKey = "dFZGRlBhUVgyd292TlpOVk44RnBid0E6MQ",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.handle_error,
socketTimeout = 8000)
public class CampusApplication extends Application {
	@Override
	public void onCreate(){
		// initialization of ACRA ReportCrash
		ACRA.init(this);
		super.onCreate();
	}
}