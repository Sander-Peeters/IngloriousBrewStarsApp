package ap.edu.ingloriousbrewstars;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Sander Peeters on 12/7/2015.
 */
public class TabFragment1Categories extends AppCompatActivity {
    ArrayList<String> categoriesList;
    String storagePlace;
    TextView header;
    private Firebase mFirebaseRef;
    private Firebase mFirebaseRefInventory;
    static TabFragment1Categories tabFragment1Categories;
    boolean inventoryAlreadyExists;
    String formattedDate;
    Context context = this;
    int items;
    int containers;
    AlertDialog alert;
    AVLoadingIndicatorView loadingIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_fragment_1_categories);
        overridePendingTransition(R.anim.animation, R.anim.animation2);
        Firebase.setAndroidContext(this);
        tabFragment1Categories = this;

        //Ophalen van de lader
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        loadingIndicator.setVisibility(View.VISIBLE);

        //Tijd
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(c.getTime());

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

        storagePlace = getIntent().getStringExtra("storagePlace");
        header = (TextView) findViewById(R.id.titlecategory);
        header.setText(storagePlace);

        categoriesList = new ArrayList<>();
        mFirebaseRefInventory = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + formattedDate + storagePlace + "/products");
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

            //Uit database halen of category 'advanced' is.
            mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        final String categoryName = (String) child.child("name").getValue();
                        final boolean categoryAdvanced = (Boolean) child.child("canBeHalfFull").getValue();
                        if ((categoriesList.get(v.getId())).equals(categoryName)) {

                            mFirebaseRefInventory.child(categoryName).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Map<String, Object> newProducten = (Map<String, Object>) snapshot.getValue();
                                        if (categoryAdvanced) {
                                            items = (int) (long) newProducten.get("flessen");

                                            try {
                                                containers = (int) (long) newProducten.get("extraBoxes");
                                            } catch (NullPointerException ex) {
                                                containers = 0;
                                            }
                                        } else {
                                            items = (int) (long) newProducten.get("flessen");
                                        }

                                        if (!(TabFragment1Categories.this).isFinishing()) {
                                            new AlertDialog.Builder(TabFragment1Categories.this)
                                                    .setTitle("Inventory already exists")
                                                    .setMessage("There is already an inventory made for the category '" + categoryName + "' in '" + storagePlace + "' on " + formattedDate + ". Do you want to edit this inventory?")
                                                    .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (categoryAdvanced) {
                                                                Intent intentViewAdvanced = new Intent(getApplicationContext(), TabFragment1ViewAdvanced.class);
                                                                intentViewAdvanced.putExtra("storagePlace", storagePlace);
                                                                intentViewAdvanced.putExtra("category", categoriesList.get(v.getId()));
                                                                intentViewAdvanced.putExtra("items", items);
                                                                intentViewAdvanced.putExtra("containers", containers);
                                                                intentViewAdvanced.putExtra("containers", containers);
                                                                startActivity(intentViewAdvanced);
                                                            } else {
                                                                Intent intentViewSimple = new Intent(getApplicationContext(), TabFragment1ViewSimple.class);
                                                                intentViewSimple.putExtra("storagePlace", storagePlace);
                                                                intentViewSimple.putExtra("category", categoriesList.get(v.getId()));
                                                                intentViewSimple.putExtra("items", items);
                                                                startActivity(intentViewSimple);
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // do nothing
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                    }
                                    else {
                                        if (categoryAdvanced) {
                                            Intent intentViewAdvanced = new Intent(getApplicationContext(), TabFragment1ViewAdvanced.class);
                                            intentViewAdvanced.putExtra("storagePlace", storagePlace);
                                            intentViewAdvanced.putExtra("category", categoriesList.get(v.getId()));
                                            startActivity(intentViewAdvanced);
                                        } else {
                                            Intent intentViewSimple = new Intent(getApplicationContext(), TabFragment1ViewSimple.class);
                                            intentViewSimple.putExtra("storagePlace", storagePlace);
                                            intentViewSimple.putExtra("category", categoriesList.get(v.getId()));
                                            startActivity(intentViewSimple);
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
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("error", firebaseError.getMessage());
                }
            });
        }
    };

    public static TabFragment1Categories getInstance(){
        return  tabFragment1Categories;
    }
}
