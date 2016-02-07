package ap.edu.ingloriousbrewstars;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sander Peeters on 12/1/2015.
 */

public class SettingsInventory extends AppCompatActivity {
    @JsonIgnoreProperties(ignoreUnknown = true)

    private Firebase mFirebaseRef;
    FirebaseListAdapter<Inventory> mListAdapter;
    boolean appConfirm;
    SharedPreferences SP;
    AVLoadingIndicatorView loadingIndicator;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_inventory);
        overridePendingTransition(R.anim.animation, R.anim.animation2);
        Firebase.setAndroidContext(this);

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        appConfirm = SP.getBoolean("applicationConfirmation", true);
        mFirebaseRef = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");

        //Ophalen van de applicatiekleuren
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

        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        loadingIndicator.setVisibility(View.VISIBLE);

        mListAdapter = new FirebaseListAdapter<Inventory>(this, Inventory.class,
                R.layout.item_list_app, mFirebaseRef) {
            @Override
            protected void populateView(View v, Inventory model) {
                ((TextView)v.findViewById(R.id.tv_name)).setText(model.getName().toString() + " | " + model.getStorage().toString());

                loadingIndicator.setVisibility(View.GONE);

                if (mListAdapter.getCount() == 0) {
                    loadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(SettingsInventory.this, "There are no inventories.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);

        listView.setAdapter(mListAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
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
                switch (index) {
                    case 0:
                        if (appConfirm) {
                            new AlertDialog.Builder(SettingsInventory.this)
                                    .setTitle("Delete Inventory")
                                    .setMessage("Are you sure you want to delete this inventory?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Firebase itemRef = mListAdapter.getRef(position);
                                            //itemRef.remove();
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
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } else {
                            Firebase itemRef = mListAdapter.getRef(position);
                            //itemRef.remove();
                            itemRef.removeValue();
                            mListAdapter.cleanup();
                            mListAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                return false;
            }
        });

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}