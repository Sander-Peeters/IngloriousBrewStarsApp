package ap.edu.ingloriousbrewstars;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sander Peeters on 12/16/2015.
 */
public class ReportSearchCategoryView extends Activity {
    String category;
    TextView categoryTitle;
    List<String> listDataHeader;
    List<String> listDataChild;
    List inventories;
    List categories;
    ArrayList<Model> models;
    private Firebase mFirebaseRef1;
    private Firebase mFirebaseRef2;
    MyAdapter adapter;
    ListView listView;
    String today;
    List dates;
    List storages;
    Date todayDate;
    SimpleDateFormat df;
    Date nearestDate;
    int i;
    SharedPreferences SP;
    int count;
    int childsNumber;
    AVLoadingIndicatorView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_search_category_view);

        category = getIntent().getStringExtra("category");
        categoryTitle = (TextView) findViewById(R.id.textViewCategory);
        categoryTitle.setText(category);

        //Ophalen van de lader
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        loadingIndicator.setVisibility(View.VISIBLE);

        adapter = new MyAdapter(this, generateData());
        listView = (ListView) findViewById(R.id.listViewReportCategory);
        listView.setAdapter(adapter);

        //Tijd
        Calendar c = Calendar.getInstance();
        df = new SimpleDateFormat("dd-MM-yyyy");
        today = df.format(c.getTime());
        try {
            todayDate = df.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dates = new ArrayList();
        storages = new ArrayList();


        inventories = new ArrayList();
        categories = new ArrayList();

        nearestDate = new Date(1900, 1, 1);
        count = 0;
        childsNumber = 0;
    }

    private ArrayList<Model> generateData() {
        //Moet uit database gehaald worden.

        listDataHeader = new ArrayList<String>();
        listDataChild = new ArrayList<String>();
        models = new ArrayList<Model>();
        final String searchCategory = getIntent().getStringExtra("category");

        mFirebaseRef1 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");
        mFirebaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    final String date = (String) child.child("name").getValue();
                    final String storage = (String) child.child("storage").getValue();

                    mFirebaseRef2 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + date + storage + "/products");
                    mFirebaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            int childsCount = (int) (long) snapshot.getChildrenCount();
                            childsNumber += childsCount;
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.e("error", firebaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("error", firebaseError.getMessage());
            }
        });


        mFirebaseRef1 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");
        mFirebaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    final String date = (String) child.child("name").getValue();
                    final String storage = (String) child.child("storage").getValue();

                    mFirebaseRef2 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + date + storage + "/products");
                    mFirebaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                count++;
                                if (child.child("catName").getValue().equals(searchCategory)) {
                                    //Log.d("Storage", storage);
                                    //Log.d("date", date.toString());

                                    if (!storages.contains(storage)) {
                                        storages.add(storage);

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                                        Date convertedDate = new Date();
                                        try {
                                            convertedDate = dateFormat.parse(date);
                                        } catch (ParseException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                        dates.add(convertedDate);
                                    } else {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                                        Date convertedDate = new Date();
                                        try {
                                            convertedDate = dateFormat.parse(date);
                                        } catch (ParseException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                        Date alreadyExistingDate = (Date) dates.get(storages.indexOf(storage));

                                        if (convertedDate.after(alreadyExistingDate)) {
                                            dates.set(storages.indexOf(storage), convertedDate);
                                        }
                                    }
                                }
                            }

                            if (childsNumber == count) {
                                if (storages.size() == 0) {
                                    loadingIndicator.setVisibility(View.GONE);
                                    Toast.makeText(ReportSearchCategoryView.this, "No inventories made for category '" + searchCategory + "'.", Toast.LENGTH_SHORT).show();
                                }
                                for (int i = 0; i < storages.size(); i++) {
                                    Date date = (Date) dates.get(i);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                                    final String dateString = dateFormat.format(date);
                                    final String storage = storages.get(i).toString();

                                    mFirebaseRef2 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + dateString + storage + "/products");
                                    mFirebaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                String category = (String) child.child("catName").getValue();

                                                if (category.equals(searchCategory)) {
                                                    int items = (int) (long) child.child("flessen").getValue();
                                                    models.add(new Model(storage));
                                                    models.add(new Model(R.drawable.beer_bottle, dateString, String.valueOf(items)));
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                            loadingIndicator.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                            Log.e("error", firebaseError.getMessage());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.e("error", firebaseError.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("error", firebaseError.getMessage());
            }
        });
        return models;
    }
}
