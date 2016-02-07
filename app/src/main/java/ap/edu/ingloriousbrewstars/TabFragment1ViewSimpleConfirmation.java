package ap.edu.ingloriousbrewstars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;

/**
 * Created by Sander Peeters on 1/17/2016.
 */
public class TabFragment1ViewSimpleConfirmation extends AppCompatActivity {
    String storagePlace;
    String category;
    int items;
    TextView header;
    Button buttonCloseConfirmation;
    MyAdapter adapter;
    ListView listView;
    TextView confirmation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_fragment_1_viewsimple_confirmation);
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
        if (getIntent().getBooleanExtra("edit", true)) {
            confirmation.setText("Inventory edited successfully.");
        }
        header = (TextView) findViewById(R.id.titleadvancedconfirmation);
        header.setText(storagePlace);
        buttonCloseConfirmation = (Button) findViewById(R.id.buttonCloseConfirmation);

        adapter = new MyAdapter(TabFragment1ViewSimpleConfirmation.this, generateData());
        listView = (ListView) findViewById(R.id.listViewInventory);
        listView.setAdapter(adapter);

        buttonCloseConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabFragment1ViewSimple.getInstance().finish();
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

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.hops, item1, String.valueOf(items)));

        return models;
    }
}
