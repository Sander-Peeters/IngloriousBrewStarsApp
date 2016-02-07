package ap.edu.ingloriousbrewstars;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sander Peeters on 12/7/2015.
 */
public class TabFragment1ViewSimple extends AppCompatActivity {
    TextView header;
    String storagePlace;
    String category;
    String item1;
    String item2;
    int numberOfItem1;
    int numberOfItem2;
    int count1;
    int count2;
    Button buttonAddItem;
    Button buttonSubstractItem;
    Button buttonSubmitInventory;
    EditText numberOfItemsInContainerAdd;
    EditText numberOfItemsInContainerSubstract;
    private Firebase mFirebaseRef;
    private Firebase mFirebaseRefCategory;
    String formattedDate;
    MyAdapter adapter;
    ListView listView;
    static TabFragment1ViewAdvanced tabFragment1ViewAdvanced;
    boolean editView;

    int overzichtAantalItems;
    int inputAantalItemsAdd;
    int inputAantalItemsSubstract;;
    static TabFragment1ViewSimple tabFragment1ViewSimple;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_fragment_1_viewsimple);
        overridePendingTransition(R.anim.animation, R.anim.animation2);
        tabFragment1ViewSimple = this;

        //Tijd
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(c.getTime());

        mFirebaseRef = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");
        mFirebaseRefCategory = new Firebase("https://shining-heat-1471.firebaseio.com/categories");
        overzichtAantalItems = 0;

        buttonAddItem = (Button) findViewById(R.id.buttonAddItem);
        buttonSubstractItem = (Button) findViewById(R.id.buttonSubstractItem);
        buttonSubmitInventory = (Button) findViewById(R.id.buttonSubmitInventory);

        numberOfItemsInContainerAdd = (EditText) findViewById(R.id.editTextItemsAdd);
        numberOfItemsInContainerAdd.setImeActionLabel("ADD", KeyEvent.KEYCODE_ENTER);
        numberOfItemsInContainerAdd.setImeOptions(EditorInfo.IME_ACTION_DONE);
        numberOfItemsInContainerAdd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            buttonAddItem.performClick();
                            View view = getCurrentFocus();
                            if (view != null) {
                                view.clearFocus();
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        numberOfItemsInContainerSubstract = (EditText) findViewById(R.id.editTextItemsSubstract);
        numberOfItemsInContainerSubstract.setImeActionLabel("SUBSTRACT", KeyEvent.KEYCODE_ENTER);
        numberOfItemsInContainerSubstract.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            buttonSubstractItem.performClick();
                            View view = getCurrentFocus();
                            if (view != null) {
                                view.clearFocus();
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

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
                buttonAddItem.setBackgroundColor(Color.rgb(appColorR, appColorG, appColorB));
                buttonSubstractItem.setBackgroundColor(Color.rgb(appColorR, appColorG, appColorB));
            }
        }

        storagePlace = getIntent().getStringExtra("storagePlace");
        category = getIntent().getStringExtra("category");
        editView = false;
        if(getIntent().getExtras().containsKey("items")) {
            overzichtAantalItems = getIntent().getIntExtra("items", 0);
            editView = true;
        }
        header = (TextView) findViewById(R.id.titleadvanced);
        header.setText(storagePlace);

        adapter = new MyAdapter(this, generateData());
        listView = (ListView) findViewById(R.id.listViewInventory);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfItemsInContainerAdd.getText().toString().contains(".") || numberOfItemsInContainerAdd.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewSimple.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfItemsInContainerAdd.getText().toString().equals(null) || numberOfItemsInContainerAdd.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewSimple.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Hier wegschrijven naar overzichtsveld.
                        inputAantalItemsAdd = Integer.valueOf(numberOfItemsInContainerAdd.getText().toString());

                        adapter = new MyAdapter(TabFragment1ViewSimple.this, generateDataItemsAdd());
                        listView = (ListView) findViewById(R.id.listViewInventory);
                        listView.setAdapter(adapter);
                        Toast.makeText(TabFragment1ViewSimple.this, String.valueOf(numberOfItemsInContainerAdd.getText()) + " item(s) added.", Toast.LENGTH_SHORT).show();
                        numberOfItemsInContainerAdd.setText("");
                    }
                }
            }
        });

        buttonSubstractItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfItemsInContainerSubstract.getText().toString().contains(".") || numberOfItemsInContainerSubstract.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewSimple.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfItemsInContainerSubstract.getText().toString().equals(null) || numberOfItemsInContainerSubstract.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewSimple.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Hier wegschrijven naar overzichtsveld.
                        inputAantalItemsSubstract = Integer.valueOf(numberOfItemsInContainerSubstract.getText().toString());

                        adapter = new MyAdapter(TabFragment1ViewSimple.this, generateDataItemsSubstract());
                        listView = (ListView) findViewById(R.id.listViewInventory);
                        listView.setAdapter(adapter);
                        Toast.makeText(TabFragment1ViewSimple.this, String.valueOf(numberOfItemsInContainerSubstract.getText()) + " item(s) substracted.", Toast.LENGTH_SHORT).show();
                        numberOfItemsInContainerSubstract.setText("");
                    }
                }
            }
        });

        //Push inventory naar FireBase
        buttonSubmitInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Firebase productRef = mFirebaseRef.child(formattedDate + storagePlace);

                Map<String,Object> inventaris = new HashMap<String, Object>();
                inventaris.put("name", formattedDate);
                inventaris.put("storage", storagePlace);
                productRef.updateChildren(inventaris);

                Map<String, Object> product = new HashMap<String, Object>();
                product.put("flessen", overzichtAantalItems);
                product.put("catName", category);
                productRef.child("products/" + category).updateChildren(product);

                Intent intentViewSimpleConfirmation = new Intent(getApplicationContext(), TabFragment1ViewSimpleConfirmation.class);
                intentViewSimpleConfirmation.putExtra("storagePlace", storagePlace);
                intentViewSimpleConfirmation.putExtra("category", category);
                intentViewSimpleConfirmation.putExtra("items", overzichtAantalItems);
                intentViewSimpleConfirmation.putExtra("edit", editView);
                startActivity(intentViewSimpleConfirmation);
            }
        });
    }

    private ArrayList<Model> generateData() {
        //Moet uit database gehaald worden.
        item1 = "Items";

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.hops, item1, String.valueOf(overzichtAantalItems)));

        return models;
    }

    private ArrayList<Model> generateDataItemsAdd() {
        //Moet uit database gehaald worden.
        item1 = "Items";

        overzichtAantalItems += inputAantalItemsAdd;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.hops, item1, String.valueOf(overzichtAantalItems)));

        return models;
    }

    private ArrayList<Model> generateDataItemsSubstract() {
        //Moet uit database gehaald worden.
        item1 = "Items";

        overzichtAantalItems -= inputAantalItemsSubstract;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.hops, item1, String.valueOf(overzichtAantalItems)));

        return models;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static TabFragment1ViewSimple getInstance(){
        return  tabFragment1ViewSimple;
    }
}
