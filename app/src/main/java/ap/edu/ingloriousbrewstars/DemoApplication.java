package ap.edu.ingloriousbrewstars;

import com.firebase.client.Firebase;

/**
 * Created by Sander Peeters on 2/1/2016.
 */
public class DemoApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}