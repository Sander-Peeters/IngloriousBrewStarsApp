package ap.edu.ingloriousbrewstars;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sander Peeters on 1/17/2016.
 */
public class TabFragment1ViewAdvancedConfirmation extends AppCompatActivity {
    String storagePlace;
    String category;
    int items;
    int containers;
    TextView header;
    Button buttonCloseConfirmation;
    MyAdapter adapter;
    ListView listView;
    TextView confirmation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_fragment_1_viewadvanced_confirmation);
        overridePendingTransition(R.anim.animation, R.anim.animation2);
        Firebase.setAndroidContext(this);

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
        confirmation = (TextView) findViewById(R.id.confirmation);
        storagePlace = getIntent().getStringExtra("storagePlace");
        category = getIntent().getStringExtra("category");
        items = getIntent().getIntExtra("items", 0);
        containers = getIntent().getIntExtra("containers", 0);
        if (getIntent().getBooleanExtra("edit", true)) {
            confirmation.setText("Inventory edited successfully.");
        }
        header = (TextView) findViewById(R.id.titleadvancedconfirmation);
        header.setText(storagePlace);
        buttonCloseConfirmation = (Button) findViewById(R.id.buttonCloseConfirmation);

        adapter = new MyAdapter(TabFragment1ViewAdvancedConfirmation.this, generateData());
        listView = (ListView) findViewById(R.id.listViewInventory);
        listView.setAdapter(adapter);

        buttonCloseConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabFragment1ViewAdvanced.getInstance().finish();
                TabFragment1Categories.getInstance().finish();
                Intent intentCategories = new Intent(getApplicationContext(), TabFragment1Categories.class);
                intentCategories.putExtra("storagePlace", storagePlace);
                startActivity(intentCategories);
                finish();
            }
        });
    }

    private ArrayList<Model> generateData() {
        //Moet uit database gehaald worden.
        String item1 = "Items";
        String item2 = "Containers";

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(items)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(containers)));

        return models;
    }
}
