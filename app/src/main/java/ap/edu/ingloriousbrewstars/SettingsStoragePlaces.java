package ap.edu.ingloriousbrewstars;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by Sander Peeters on 12/1/2015.
 */
public class SettingsStoragePlaces extends AppCompatActivity {

    private Button addStoragePlacebutton;
    SharedPreferences SP;
    boolean appConfirm;
    private GoogleApiClient client;
    AVLoadingIndicatorView loadingIndicator;

    private Firebase mFirebaseRef;
    FirebaseListAdapter<StoragePlace> mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_storageplaces);
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

        mFirebaseRef = new Firebase("https://shining-heat-1471.firebaseio.com/storage_places");

        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        loadingIndicator.setVisibility(View.VISIBLE);

        mListAdapter = new FirebaseListAdapter<StoragePlace>(this, StoragePlace.class,
                R.layout.item_list_app, mFirebaseRef) {
            @Override
            protected void populateView(View v, StoragePlace model) {
                ((TextView)v.findViewById(R.id.tv_name)).setText(model.getName());

                loadingIndicator.setVisibility(View.GONE);

                if (mListAdapter.getCount() == 0) {
                    loadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(SettingsStoragePlaces.this, "There are no storage places.", Toast.LENGTH_SHORT).show();
                }
            }
        };


        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
        listView.setAdapter(mListAdapter);

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        appConfirm = SP.getBoolean("applicationConfirmation", true);

        addStoragePlacebutton = (Button) findViewById(R.id.addStoragePlaceButton);
        addStoragePlacebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddStoragePlace = new Intent(getApplicationContext(), ap.edu.ingloriousbrewstars.AddStoragePlace.class);
                startActivity(AddStoragePlace);
                finish();
            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                openItem.setWidth(dp2px(90));
                openItem.setTitle("Edit");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                final StoragePlace item = mListAdapter.getItem(position);
                switch (index) {
                    case 0:
                        Intent EditStoragePlace = new Intent(getApplicationContext(), ap.edu.ingloriousbrewstars.EditStoragePlace.class);
                        EditStoragePlace.putExtra("originele naam", mListAdapter.getItem(position).getName());
                        EditStoragePlace.putExtra("originele adres", mListAdapter.getItem(position).getAddress());
                        EditStoragePlace.putExtra("originele zip", mListAdapter.getItem(position).getZIP());
                        EditStoragePlace.putExtra("originele stad", mListAdapter.getItem(position).getCity());
                        startActivity(EditStoragePlace);
                        finish();
                        break;
                    case 1:
                        if (appConfirm) {
                            new AlertDialog.Builder(SettingsStoragePlaces.this)
                                    .setTitle("Delete Storage Place")
                                    .setMessage("Are you sure you want to delete this storage place?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // delete
                                            //Hier code plaatsen om storage place uit database te verwijderen
                                            final Firebase itemRef = mListAdapter.getRef(position);

                                            itemRef.removeValue();
                                            mListAdapter.cleanup();
                                            mListAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                        }
                        else {
                            // delete
                            //Hier code plaatsen om storage place uit database te verwijderen
                            final Firebase itemRef = mListAdapter.getRef(position);

                            itemRef.removeValue();
                            mListAdapter.cleanup();
                            mListAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                return false;
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SettingsStoragePlaces Page", // TODO: Define a title for the content shown.
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
                "SettingsStoragePlaces Page", // TODO: Define a title for the content shown.
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


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}