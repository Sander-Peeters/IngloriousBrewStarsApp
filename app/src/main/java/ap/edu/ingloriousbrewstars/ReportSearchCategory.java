package ap.edu.ingloriousbrewstars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Sander Peeters on 12/16/2015.
 */
public class ReportSearchCategory extends Activity {
    CalendarView calendar;
    String curDate;
    SharedPreferences SP;
    boolean appConfirm;
    final Context context = this;
    ArrayList<String> categoriesList;
    Firebase mFirebaseRef;
    AVLoadingIndicatorView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_search_category);

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

        //Ophalen van de lader
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        loadingIndicator.setVisibility(View.VISIBLE);

        categoriesList = new ArrayList<>();

        mFirebaseRef = new Firebase("https://shining-heat-1471.firebaseio.com/categories");
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String categoryName = (String) child.child("name").getValue();
                    Log.d("Category", categoryName);

                    categoriesList.add(categoryName);
                }

                LinearLayout layout = (LinearLayout) findViewById(R.id.layoutbutton);
                layout.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < categoriesList.size(); i++) {
                    LinearLayout row = new LinearLayout(getApplicationContext());
                    row.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

                    Button btnTag = new Button(getApplicationContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 120);
                    btnTag.setText(categoriesList.get(i)); //titel van knop
                    btnTag.setId(i); //id meegeven om knop aan te kunnen roepen
                    params.setMargins(30, 30, 30, 15);
                    btnTag.setLayoutParams(params);
                    btnTag.setOnClickListener(onClickListener);
                    btnTag.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    row.addView(btnTag);
                    layout.addView(row);

                    if (i == categoriesList.size() - 1) {
                        loadingIndicator.setVisibility(View.GONE);
                    }
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("error", firebaseError.getMessage());
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Intent intentViewCategory = new Intent(getApplicationContext(), ReportSearchCategoryView.class);
            intentViewCategory.putExtra("category", categoriesList.get(v.getId()));
            startActivity(intentViewCategory);
        }
    };
}