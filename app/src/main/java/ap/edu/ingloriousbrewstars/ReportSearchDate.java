package ap.edu.ingloriousbrewstars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;


/**
 * Created by Sander Peeters on 12/16/2015.
 */
public class ReportSearchDate extends AppCompatActivity {
    String curDate;
    SharedPreferences SP;
    boolean appConfirm;
    final Context context = this;
    Toolbar toolbar;
    CaldroidFragment caldroidFragment;
    Calendar cal;
    Firebase mFirebaseRefInventories;

    List<DateTime> arrayListInventories;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_search_date);
        JodaTimeAndroid.init(this);

        //Ophalen van de applicatiekleuren
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        int appBackColor = SP.getInt("applicationBackColor", 0);
        int appBackColorR = SP.getInt("applicationBackColorR", 0);
        int appBackColorG = SP.getInt("applicationBackColorG", 0);
        int appBackColorB = SP.getInt("applicationBackColorB", 0);
        int appColor = SP.getInt("applicationColor", 0);
        int appColorR = SP.getInt("applicationColorR", 0);
        int appColorG = SP.getInt("applicationColorG", 0);
        int appColorB = SP.getInt("applicationColorB", 0);
        boolean appStandardColor = SP.getBoolean("colorStandard", true);
        if (appStandardColor) {
            getWindow().getDecorView().setBackgroundColor(Color.rgb(238, 233, 233));
            toolbar.setBackgroundDrawable(new ColorDrawable(Color.rgb(59, 89, 152)));
        }
        else {
            if (appBackColor != 0) {
                getWindow().getDecorView().setBackgroundColor(Color.rgb(appBackColorR, appBackColorG, appBackColorB));
            }

            if (appColor != 0) {
                toolbar.setBackgroundDrawable(new ColorDrawable(Color.rgb(appColorR, appColorG, appColorB)));
            }
        }

        // Kalender
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY); // Tuesday
        caldroidFragment.setArguments(args);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        mFirebaseRefInventories = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");


        // Inventories toevoegen aan arraylist.
        arrayListInventories = new ArrayList<DateTime>();
        fillArrayDates();

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        appConfirm = SP.getBoolean("applicationInteractionConfirmation", true);

        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                curDate = formatter.format(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                DateTime dateScan = new DateTime(year, month + 1, day, 0, 0, 0, 0);

                if(arrayListInventories.contains(dateScan)) {
                    if (appConfirm) {
                        new AlertDialog.Builder(context)
                                .setTitle("Search report by date")
                                .setMessage("Are you sure you want a report for " + curDate + " ?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent searchByDateView = new Intent(getApplicationContext(), ReportSearchDateView.class);
                                        searchByDateView.putExtra("curDate", curDate);
                                        startActivity(searchByDateView);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        Intent searchByDateView = new Intent(getApplicationContext(), ReportSearchDateView.class);
                        searchByDateView.putExtra("curDate", curDate);
                        startActivity(searchByDateView);
                    }
                }
                else {
                    new AlertDialog.Builder(context)
                            .setTitle("Search report by date")
                            .setMessage("There is no report available for " + curDate + ". Please choose another date.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        };
        caldroidFragment.setCaldroidListener(listener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void selectDates(List<DateTime> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            caldroidFragment.setBackgroundResourceForDateTime(R.color.gray, arrayList.get(i));
        }
        caldroidFragment.refreshView();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ReportSearchDate Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ap.edu.ingloriousbrewstars/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ReportSearchDate Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ap.edu.ingloriousbrewstars/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void fillArrayDates() {

        mFirebaseRefInventories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String dateString = (String) child.child("name").getValue();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(convertedDate);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    DateTime datetime = new DateTime(year, month + 1, day, 0, 0, 0, 0);

                    if (!arrayListInventories.contains(datetime)) {
                        //Log.d("Datum", String.valueOf(datetime));
                        arrayListInventories.add(datetime);
                    }
                }
                //Het selecteren van de datums.
                selectDates(arrayListInventories);
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("error", firebaseError.getMessage());
            }
        });

        /*
        mFirebaseRefInventories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> dates = (Map<String, Object>) snapshot.getValue();
                JSONObject jObject = new JSONObject(dates);

                Iterator<String> iter = jObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = jObject.getJSONObject(key);
                        if (!arrayListInventories.contains(value.getString("name"))) {
                            String dateString = value.getString("name").toString();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date convertedDate = new Date();
                            try {
                                convertedDate = dateFormat.parse(dateString);
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(convertedDate);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            int month = calendar.get(Calendar.MONTH);
                            int year = calendar.get(Calendar.YEAR);
                            DateTime datetime = new DateTime(year, month + 1, day, 0, 0, 0, 0);

                            Log.d("Datum", String.valueOf(datetime));

                            arrayListInventories.add(datetime);
                        }
                    } catch (JSONException e) {
                        System.out.println("iterator error : " + e);
                    }
                }
                //Het selecteren van de datums.
                selectDates(arrayListInventories);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        */
    }
}
