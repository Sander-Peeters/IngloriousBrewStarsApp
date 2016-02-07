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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class SettingsCategories  extends Activity {

    private Button addCategoryButton;

    private Firebase mFirebaseRef;
    private Firebase mFirebaseRef1;
    private Firebase mFirebaseRef2;
    FirebaseListAdapter<Category> mListAdapter;
    AVLoadingIndicatorView loadingIndicator;
    Context context = this;

    boolean appConfirm;
    SharedPreferences SP;



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Firebase.getDefaultConfig().setPersistenceEnabled(true);*/
        Firebase.setAndroidContext(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_categories);
        overridePendingTransition(R.anim.animation, R.anim.animation2);

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        appConfirm = SP.getBoolean("applicationConfirmation", true);

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


        mFirebaseRef = new Firebase("https://shining-heat-1471.firebaseio.com/categories");

        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        loadingIndicator.setVisibility(View.VISIBLE);

        mListAdapter = new FirebaseListAdapter<Category>(this, Category.class,
                R.layout.item_list_app, mFirebaseRef) {
            @Override
            protected void populateView(View v, Category model) {
                ((TextView)v.findViewById(R.id.tv_name)).setText(model.getName());

                loadingIndicator.setVisibility(View.GONE);

                if (mListAdapter.getCount() == 0) {
                    loadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(SettingsCategories.this, "There are no categories.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
        listView.setAdapter(mListAdapter);

        addCategoryButton = (Button) findViewById(R.id.addCategoryButton);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(SettingsCategories.this);

                final View textEntryView = factory.inflate(R.layout.text_entry, null);
                //text_entry is an Layout XML file containing two text field to display in alert dialog

                final EditText input1 = (EditText) textEntryView.findViewById(R.id.editText);
                final EditText input2 = (EditText) textEntryView.findViewById(R.id.editText2);

                final AlertDialog.Builder alert = new AlertDialog.Builder(SettingsCategories.this);
                alert.setIcon(R.drawable.addcategory).setTitle(
                        "Add new category").setView(
                        textEntryView).setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                View focusView = null;

                                if (input1.getText().toString().matches("")) {
                                    Toast.makeText(getApplicationContext(), "You did not enter a category name.", Toast.LENGTH_SHORT).show();
                                } else {
                                    mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                        boolean placeExists = false;

                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                final String categoryName = child.child("name").getValue().toString();
                                                if (categoryName.equals(input1.getText().toString())) {
                                                    placeExists = true;
                                                }
                                            }
                                            if (placeExists) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Tried to add a category")
                                                        .setMessage("A category with this name already exists. Please choose a different name.")
                                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // do nothing
                                                            }
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            } else {
                                                //Hier code plaatsen om weg te schrijven naar database
                                                String name = input1.getText().toString();
                                                Integer maxbottles;
                                                if (IsEmpty(input2.getText().toString())) {
                                                    maxbottles = 0;
                                                } else {
                                                    maxbottles = Integer.parseInt(input2.getText().toString());
                                                }

                                                Boolean canBeHalfFull = null;
                                                if (IsEmpty(input2.getText().toString())) {
                                                    canBeHalfFull = false;
                                                } else canBeHalfFull = true;
                                                Category cat = new Category(name, maxbottles, canBeHalfFull);
                                                mFirebaseRef.child(name).setValue(cat);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
                                }
                                Log.i("AlertDialog", "TextEntry 1 Entered " + input1.getText().toString());
                                Log.i("AlertDialog", "TextEntry 2 Entered " + input2.getText().toString());
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
                alert.show();
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
                final String itemKey = mListAdapter.getRef(position).getKey();
                final Boolean itemCanBeHalfFull = mListAdapter.getItem(position).getCanBeHalfFull();
                final String itemName = mListAdapter.getItem(position).getName();
                int numberBottles = 0;

                if (itemCanBeHalfFull) {
                    numberBottles = mListAdapter.getItem(position).getMaxBottles();
                }

                switch (index) {
                    case 0:
                        LayoutInflater factory = LayoutInflater.from(SettingsCategories.this);

                        final View textEntryView = factory.inflate(R.layout.text_entry, null);
                        //text_entry is an Layout XML file containing two text field to display in alert dialog

                        final EditText input1 = (EditText) textEntryView.findViewById(R.id.editText);
                        final EditText input2 = (EditText) textEntryView.findViewById(R.id.editText2);

                        input1.setText(itemName.toString(), TextView.BufferType.EDITABLE);
                        //Uit database halen of category over bakken beschikt
                        if (itemCanBeHalfFull) {
                            input2.setText(String.valueOf(numberBottles), TextView.BufferType.EDITABLE);
                        }

                        final AlertDialog.Builder alert = new AlertDialog.Builder(SettingsCategories.this);
                        alert.setIcon(R.drawable.editcategory).setTitle(
                                "Edit category").setView(
                                textEntryView).setPositiveButton("Edit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {

                                        if (input1.getText().toString().matches("")) {
                                            Toast.makeText(getApplicationContext(), "You did not enter a category name.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //Hier code schrijven om category aan te passen
                                            final String new_name = input1.getText().toString();
                                            Integer maxBottles;
                                            if (input2.getText().toString().equals("")) {
                                                maxBottles = mListAdapter.getItem(position).getMaxBottles();
                                            } else {
                                                maxBottles = Integer.parseInt(input2.getText().toString());
                                            }
                                            Boolean editCanBeHalfFull;
                                            if (input2.getText().toString().equals("0") || input2.getText().toString().equals("")) {
                                                editCanBeHalfFull = false;
                                            } else {
                                                editCanBeHalfFull = true;
                                            }


                                            Category editedCategory = new Category(new_name, maxBottles, editCanBeHalfFull);
                                            mFirebaseRef.child(new_name).setValue(editedCategory);
                                            if (!(itemName.equals(new_name))) {
                                                mFirebaseRef.child(itemName).getRef().removeValue();
                                            }
                                            if (input2.getText().toString().equals("")) {
                                                mFirebaseRef.child(new_name).child("maxBottles").removeValue();
                                            }


                                            //Aanpassen van categorieën in inventories.
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
                                                                    String category = (String) child.child("catName").getValue();
                                                                    Firebase mFirebaseRef3 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + date + storage + "/products/" + category);
                                                                    Firebase mFirebaseRef4 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + date + storage + "/products");

                                                                    if (category.equals(itemName)) {
                                                                        Log.d("Date", date);
                                                                        if (child.child("extraBoxes").exists()) {
                                                                            int items = (int) (long) child.child("flessen").getValue();
                                                                            int containers = (int) (long) child.child("extraBoxes").getValue();
                                                                            Map<String, Object> product = new HashMap<String, Object>();
                                                                            product.put("flessen", items);
                                                                            product.put("extraBoxes", containers);
                                                                            product.put("catName", new_name);
                                                                            mFirebaseRef3.removeValue();
                                                                            mFirebaseRef4.child(new_name).updateChildren(product);
                                                                        } else {
                                                                            Map<String, Object> product = new HashMap<String, Object>();
                                                                            product.put("flessen", (int) (long) child.child("flessen").getValue());
                                                                            product.put("catName", new_name);
                                                                            mFirebaseRef3.removeValue();
                                                                            mFirebaseRef4.child(new_name).updateChildren(product);
                                                                        }
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


                                        }
                                        Log.i("AlertDialog", "TextEntry 1 Entered " + input1.getText().toString());
                                        Log.i("AlertDialog", "TextEntry 2 Entered " + input2.getText().toString());
                                    }
                                }).setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                });
                        alert.show();
                        break;
                    case 1:
                        // delete
                        //Hier code plaatsen om category uit database te verwijderen
                        final Firebase itemRef = mListAdapter.getRef(position);

                        if (appConfirm) {
                            new AlertDialog.Builder(SettingsCategories.this)
                                    .setTitle("Delete Category")
                                    .setMessage("Are you sure you want to delete this category?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
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
                                    .setIcon(android.R.drawable.ic_dialog_alert).show();
                        } else {
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

    public boolean IsEmpty(String inputString){
        if (inputString.length() == 0){
            return true;
        }
        else return false;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}