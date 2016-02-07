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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sander Peeters on 12/2/2015.
 */
public class EditStoragePlace extends AppCompatActivity {
    Button addStoragePlace;
    Button cancelStoragePlace;
    EditText SPName;
    EditText SPAddress;
    EditText SPZip;
    EditText SPCity;
    String name;
    String address;
    String zip;
    String city;
    String original_name;
    String original_zip;
    String original_city;
    String original_address;
    Firebase mFirebaseRef;
    Firebase mFirebaseRef1;
    Firebase mFirebaseRef2;
    Firebase mFirebaseRef3;
    Firebase mFirebaseRef4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_storageplace_edit);
        overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
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

        original_name = getIntent().getStringExtra("originele naam");
        original_address = getIntent().getStringExtra("originele adres");
        original_zip = getIntent().getStringExtra("originele zip");
        original_city = getIntent().getStringExtra("originele stad");

        mFirebaseRef = new Firebase("https://shining-heat-1471.firebaseio.com/storage_places");

        SPName = (EditText) findViewById(R.id.editTextName);
        SPAddress = (EditText) findViewById(R.id.editTextAddress);
        SPZip = (EditText) findViewById(R.id.editTextZip);
        SPCity = (EditText) findViewById(R.id.editTextCity);

        SPName.setText(original_name);
        SPAddress.setText(original_address);
        SPZip.setText(original_zip);
        SPCity.setText(original_city);

        addStoragePlace = (Button) findViewById(R.id.editStoragePlaceButton);
        addStoragePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = SPName.getText().toString();
                address = SPAddress.getText().toString();
                zip = SPZip.getText().toString();
                city = SPCity.getText().toString();

                if(isEmpty(name) || isEmpty(address) || isEmpty(zip) || isEmpty(city)) {
                    Toast.makeText(EditStoragePlace.this, "Please fill in all boxes", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Hier code om naar database weg te schrijven.

                    StoragePlace editedPlace = new StoragePlace(name, address, city, zip);
                    mFirebaseRef.child(name).setValue(editedPlace);
                    mFirebaseRef.child(original_name).getRef().removeValue();

                    //Aanpassen van categorieën in inventories.
                    mFirebaseRef1 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");
                    mFirebaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (final DataSnapshot child : snapshot.getChildren()) {
                                final String date = (String) child.child("name").getValue();
                                final String storage = (String) child.child("storage").getValue();

                                if (storage.equals(original_name)) {
                                    mFirebaseRef3 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/");
                                    mFirebaseRef4 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + date + storage);
                                    Map<String, Object> storageMap = new HashMap<String, Object>();
                                    storageMap.put("storage", name);
                                    mFirebaseRef3.child(date + name).setValue(child.getValue());
                                    mFirebaseRef4.removeValue();
                                    mFirebaseRef4 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + date + name);
                                    mFirebaseRef4.updateChildren(storageMap);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.e("error", firebaseError.getMessage());
                        }
                    });




                    //Doordat view terug geopend wordt zal de listview geupdate zijn.
                    Intent SettingStoragePlace = new Intent(getApplicationContext(), SettingsStoragePlaces.class);
                    startActivity(SettingStoragePlace);
                    finish(); //zorgt ervoor dat als men op de back-knop drukt, dat men niet meer op deze view kan.
                }
            }
        });

        cancelStoragePlace = (Button) findViewById(R.id.cancelStoragePlaceButton);
        cancelStoragePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent SettingStoragePlace = new Intent(getApplicationContext(), SettingsStoragePlaces.class);
                startActivity(SettingStoragePlace);
            }
        });
    }

    private boolean isEmpty(String myeditText) {
        return myeditText.trim().length() == 0;
    }
}