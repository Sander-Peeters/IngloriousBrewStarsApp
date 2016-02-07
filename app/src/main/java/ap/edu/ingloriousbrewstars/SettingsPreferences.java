package ap.edu.ingloriousbrewstars;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class SettingsPreferences extends AppCompatPreferenceActivity {

    int defaultColorR;
    int defaultColorG;
    int defaultColorB;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Preference colorStandard;
    Preference colorActionbar;
    Preference colorBackground;
    boolean isEnabled;
    SharedPreferences SP;
    EditTextPreference receiver1;
    EditTextPreference receiver2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        this.addPreferencesFromResource(R.xml.settings_preferences);

        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());

        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());

        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (int) getResources().getDimension(R.dimen.activity_vertical_margin) + 35,
                getResources().getDisplayMetrics());

        getListView().setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);


        //Nakijken of 'standard colors' is ingeschakeld.
        colorStandard = (Preference)findPreference("colorStandard");
        colorActionbar = (Preference)findPreference("colorActionbar");
        colorBackground = (Preference)findPreference("colorBackground");
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        isEnabled = SP.getBoolean("colorStandard", true);
        if (isEnabled) {
            colorActionbar.setEnabled(false);
            colorBackground.setEnabled(false);
        }
        else {
            colorActionbar.setEnabled(true);
            colorBackground.setEnabled(true);
        }

        editor = SP.edit();
        colorStandard.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final boolean value = (Boolean) newValue;
                if (value) {
                    colorActionbar.setEnabled(false);
                    colorBackground.setEnabled(false);
                    editor.putBoolean("colorStandard", value);
                    editor.commit();
                    restartApplication();
                }
                else {
                    colorActionbar.setEnabled(true);
                    colorBackground.setEnabled(true);
                    editor.putBoolean("colorStandard", value);
                    editor.commit();
                }
                return true;
            }
        });


        //Ophalen van de applicatiekleuren
        int appBackColor = SP.getInt("applicationBackColor", 0);
        int appBackColorR = SP.getInt("applicationBackColorR", 0);
        int appBackColorG = SP.getInt("applicationBackColorG", 0);
        int appBackColorB = SP.getInt("applicationBackColorB", 0);
        boolean appStandardColor = SP.getBoolean("colorStandard", true);
        if (appStandardColor) {
            getWindow().getDecorView().setBackgroundColor(Color.rgb(238, 233, 233));
        }
        else {
            if (appBackColor != 0) {
                getWindow().getDecorView().setBackgroundColor(Color.rgb(appBackColorR, appBackColorG, appBackColorB));
            }
        }

        receiver1 = (EditTextPreference)findPreference("emailReceiver1");
        if (!receiver1.getText().toString().matches("")) {
            receiver1.setSummary(receiver1.getText().toString());
        }
        else {
            receiver1.setSummary("Here you can formulate a second standard receiver of a report.");
        }
        receiver1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference.getKey().equals("emailReceiver1")) {
                    if (!receiver1.getText().toString().matches("")) {
                        preference.setSummary(receiver1.getText().toString());
                    }
                    else {
                        preference.setSummary("Here you can formulate a first standard receiver of a report.");
                    }
                }
                return true;
            }
        });

        receiver2 = (EditTextPreference)findPreference("emailReceiver2");
        if (!receiver2.getText().toString().matches("")) {
            receiver2.setSummary(receiver2.getText().toString());
        }
        else {
            receiver2.setSummary("Here you can formulate a second standard receiver of a report.");
        }
        receiver2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference.getKey().equals("emailReceiver2")) {
                    if (!receiver2.getText().toString().matches("")) {
                        preference.setSummary(receiver2.getText().toString());
                    }
                    else {
                        preference.setSummary("Here you can formulate a second standard receiver of a report.");
                    }
                }
                return true;
            }
        });

        //Eerste colorpicker
        final ColorPicker cp = new ColorPicker(this, defaultColorR, defaultColorG, defaultColorB);
        Preference myPref = (Preference) findPreference("colorActionbar");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                cp.show();

                preferences = PreferenceManager.getDefaultSharedPreferences(SettingsPreferences.this);
                editor = preferences.edit();
                Button okColor = (Button) cp.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int selectedColorR = cp.getRed();
                        int selectedColorG = cp.getGreen();
                        int selectedColorB = cp.getBlue();
                        int selectedColorRGB = cp.getColor();
                        editor.putInt("applicationColorR", selectedColorR);
                        editor.putInt("applicationColorG", selectedColorG);
                        editor.putInt("applicationColorB", selectedColorB);
                        editor.putInt("applicationColor", selectedColorRGB);
                        editor.commit();

                        cp.dismiss();

                        Intent mStartActivity = new Intent(getApplicationContext(), SettingsPreferences.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                });

                return true;
            }
        });

        //Tweede colorpicker
        Preference myPref2 = (Preference) findPreference("colorBackground");
        myPref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                cp.show();

                preferences = PreferenceManager.getDefaultSharedPreferences(SettingsPreferences.this);
                editor = preferences.edit();
                Button okColor = (Button) cp.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int selectedBackColorR = cp.getRed();
                        int selectedBackColorG = cp.getGreen();
                        int selectedBackColorB = cp.getBlue();
                        int selectedBackColorRGB = cp.getColor();
                        editor.putInt("applicationBackColorR", selectedBackColorR);
                        editor.putInt("applicationBackColorG", selectedBackColorG);
                        editor.putInt("applicationBackColorB", selectedBackColorB);
                        editor.putInt("applicationBackColor", selectedBackColorRGB);
                        editor.commit();

                        cp.dismiss();

                        Intent mStartActivity = new Intent(getApplicationContext(), SettingsPreferences.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                });

                return true;
            }
        });
    }

    //Toolbar bovenaan plaatsen
    private void setupActionBar() {
        getLayoutInflater().inflate(R.layout.toolbar, (ViewGroup)findViewById(android.R.id.content));
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //Ophalen van de applicatiekleuren
        int appColor = SP.getInt("applicationColor", 0);
        int appColorR = SP.getInt("applicationColorR", 0);
        int appColorG = SP.getInt("applicationColorG", 0);
        int appColorB = SP.getInt("applicationColorB", 0);
        boolean appStandardColor = SP.getBoolean("colorStandard", true);
        if (appStandardColor) {
            toolbar.setBackgroundDrawable(new ColorDrawable(Color.rgb(59, 89, 152)));
        }
        else {
            if (appColor != 0) {
                toolbar.setBackgroundDrawable(new ColorDrawable(Color.rgb(appColorR, appColorG, appColorB)));
            }
        }
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public void restartApplication() {
        Intent mStartActivity = new Intent(getApplicationContext(), SettingsPreferences.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}
