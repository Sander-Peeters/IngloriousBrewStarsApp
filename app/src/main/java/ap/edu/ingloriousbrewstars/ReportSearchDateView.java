package ap.edu.ingloriousbrewstars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sander Peeters on 12/16/2015.
 */
public class ReportSearchDateView extends Activity {
    String curDate;
    TextView dateTitle;
    List<String> listDataHeader;
    List<String> listDataChild;
    private Firebase mFirebaseRef1;
    private Firebase mFirebaseRef2;
    ArrayList<Model> models;
    MyAdapter adapter;
    ListView listView;
    AVLoadingIndicatorView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_search_date_view);

        curDate = getIntent().getStringExtra("curDate");
        dateTitle = (TextView) findViewById(R.id.textViewDate);
        dateTitle.setText(curDate);

        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        loadingIndicator.setVisibility(View.VISIBLE);

        adapter = new MyAdapter(this, generateData());
        listView = (ListView) findViewById(R.id.listViewDate);
        listView.setAdapter(adapter);

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
    }

    private ArrayList<Model> generateData() {
        //Moet uit database gehaald worden.

        listDataHeader = new ArrayList<String>();
        listDataChild = new ArrayList<String>();
        models = new ArrayList<Model>();

        mFirebaseRef1 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");
        mFirebaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String date = (String) child.child("name").getValue();
                    if (curDate.equals(date)) {
                        final String storage = (String) child.child("storage").getValue();
                        Log.d("Storage", storage);
                        adapter.notifyDataSetChanged();

                        mFirebaseRef2 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + date + storage + "/products");
                        mFirebaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                models.add(new Model(storage));
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    String category = (String) child.child("catName").getValue();
                                    int items = (int) (long) child.child("flessen").getValue();

                                    models.add(new Model(R.drawable.beer_bottle, category, String.valueOf(items)));
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Log.e("error", firebaseError.getMessage());
                            }
                        });

                    }
                }
                loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("error", firebaseError.getMessage());
            }
        });

        return models;
    }
}
