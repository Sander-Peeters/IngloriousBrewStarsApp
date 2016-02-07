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
import android.util.Log;
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
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.aakira.expandablelayout.ExpandableWeightLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sander Peeters on 12/7/2015.
 */
public class TabFragment1ViewAdvanced extends AppCompatActivity {
    ArrayList<String> categoriesList;
    TextView header;
    String storagePlace;
    String category;
    String item1;
    String item2;

    Button buttonAddItem;
    Button buttonAddContainer;
    Button buttonSubstractItem;
    Button buttonSubstractContainer;
    Button buttonAddEmptyContainer;
    Button buttonSubstractEmptyContainer;
    Button buttonSubmitInventory;

    EditText numberOfItemsInContainerAdd;
    EditText numberOfContainerAdd;
    EditText numberOfItemsInContainerSubstract;
    EditText numberOfContainerSubstract;
    EditText numberOfEmptyContainerAdd;
    EditText numberOfEmptyContainerSubstract;

    private Firebase mFirebaseRef;
    private Firebase mFirebaseRefCategory;
    String formattedDate;
    MyAdapter adapter;
    ListView listView;
    static TabFragment1ViewAdvanced tabFragment1ViewAdvanced;
    boolean editView;

    int overzichtAantalItems;
    int overzichtAantalContainers;
    int overzichtAantalItemsInContainer;

    int inputAantalItemsAdd;
    int inputAantalContainersAdd;
    int inputAantalItemsSubstract;
    int inputAantalContainersSubstract;
    int inputAantalEmptyContainersAdd;
    int inputAantalEmptyContainersSubstract;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_fragment_1_viewadvanced);
        overridePendingTransition(R.anim.animation, R.anim.animation2);
        Firebase.setAndroidContext(this);
        tabFragment1ViewAdvanced = this;

        //Tijd
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(c.getTime());

        mFirebaseRef = new Firebase("https://shining-heat-1471.firebaseio.com/inventories");
        mFirebaseRefCategory = new Firebase("https://shining-heat-1471.firebaseio.com/categories");
        overzichtAantalItems = 0;
        overzichtAantalContainers = 0;

        buttonAddItem = (Button) findViewById(R.id.buttonAddItem);
        buttonAddContainer = (Button) findViewById(R.id.buttonAddContainer);
        buttonSubstractItem = (Button) findViewById(R.id.buttonSubstractItem);
        buttonSubstractContainer = (Button) findViewById(R.id.buttonSubstractContainer);
        buttonSubmitInventory = (Button) findViewById(R.id.buttonSubmitInventory);
        buttonAddEmptyContainer = (Button) findViewById(R.id.buttonAddEmptyContainer);
        buttonSubstractEmptyContainer = (Button) findViewById(R.id.buttonSubstractEmptyContainer);

        numberOfItemsInContainerAdd = (EditText) findViewById(R.id.editTextItemsInContainer);
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

        numberOfContainerAdd = (EditText) findViewById(R.id.editTextContainers);
        numberOfContainerAdd.setImeActionLabel("ADD", KeyEvent.KEYCODE_ENTER);
        numberOfContainerAdd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            buttonAddContainer.performClick();
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
        numberOfItemsInContainerSubstract = (EditText) findViewById(R.id.editTextItemsInContainerSubstract);
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
        numberOfContainerSubstract = (EditText) findViewById(R.id.editTextContainersSubstract);
        numberOfContainerSubstract.setImeActionLabel("SUBSTRACT", KeyEvent.KEYCODE_ENTER);
        numberOfContainerSubstract.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            buttonSubstractContainer.performClick();
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

        numberOfEmptyContainerAdd = (EditText) findViewById(R.id.editTextEmptyContainerAdd);
        numberOfEmptyContainerAdd.setImeActionLabel("SUBSTRACT", KeyEvent.KEYCODE_ENTER);
        numberOfEmptyContainerAdd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            buttonAddEmptyContainer.performClick();
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

        numberOfEmptyContainerSubstract = (EditText) findViewById(R.id.editTextEmptyContainerSubstract);
        numberOfEmptyContainerSubstract.setImeActionLabel("SUBSTRACT", KeyEvent.KEYCODE_ENTER);
        numberOfEmptyContainerSubstract.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            buttonSubstractEmptyContainer.performClick();
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
                buttonAddContainer.setBackgroundColor(Color.rgb(appColorR, appColorG, appColorB));
                buttonSubstractEmptyContainer.setBackgroundColor(Color.rgb(appColorR, appColorG, appColorB));
                buttonAddEmptyContainer.setBackgroundColor(Color.rgb(appColorR, appColorG, appColorB));
                buttonSubstractContainer.setBackgroundColor(Color.rgb(appColorR, appColorG, appColorB));
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
        if(getIntent().getExtras().containsKey("containers")) {
            overzichtAantalContainers = getIntent().getIntExtra("containers", 0);
        }
        header = (TextView) findViewById(R.id.titleadvanced);
        header.setText(storagePlace);

        adapter = new MyAdapter(this, generateData());
        listView = (ListView) findViewById(R.id.listViewInventory);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);

        mFirebaseRefCategory.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Aantal items mogelijk in container opvragen
                overzichtAantalItemsInContainer = (int) (long) snapshot.child("maxBottles").getValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfItemsInContainerAdd.getText().toString().contains(".") || numberOfItemsInContainerAdd.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewAdvanced.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfItemsInContainerAdd.getText().toString().equals(null) || numberOfItemsInContainerAdd.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewAdvanced.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Integer.valueOf(numberOfItemsInContainerAdd.getText().toString()) > overzichtAantalItemsInContainer) {
                            Toast.makeText(TabFragment1ViewAdvanced.this, "There can only be " + String.valueOf(overzichtAantalItemsInContainer) + " items in one container. Choose a number between 0 and " + String.valueOf(overzichtAantalItemsInContainer) + ".", Toast.LENGTH_SHORT).show();
                        } else {
                            //Hier wegschrijven naar overzichtsveld.
                            inputAantalItemsAdd = Integer.valueOf(numberOfItemsInContainerAdd.getText().toString());

                            adapter = new MyAdapter(TabFragment1ViewAdvanced.this, generateDataItemsAdd());
                            listView = (ListView) findViewById(R.id.listViewInventory);
                            listView.setAdapter(adapter);
                            Toast.makeText(TabFragment1ViewAdvanced.this, String.valueOf(numberOfItemsInContainerAdd.getText()) + " item(s) added in one container.", Toast.LENGTH_SHORT).show();
                            numberOfItemsInContainerAdd.setText("");
                        }
                    }
                }
            }
        });

        buttonAddContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfContainerAdd.getText().toString().contains(".") || numberOfContainerAdd.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewAdvanced.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfContainerAdd.getText().toString().equals(null) || numberOfContainerAdd.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewAdvanced.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Hier wegschrijven naar overzichtsveld.
                        inputAantalContainersAdd = Integer.valueOf(numberOfContainerAdd.getText().toString());

                        adapter = new MyAdapter(TabFragment1ViewAdvanced.this, generateDataContainersAdd());
                        listView = (ListView) findViewById(R.id.listViewInventory);
                        listView.setAdapter(adapter);
                        Toast.makeText(TabFragment1ViewAdvanced.this, String.valueOf(numberOfContainerAdd.getText()) + " full container(s) added.", Toast.LENGTH_SHORT).show();
                        numberOfContainerAdd.setText("");
                    }
                }
            }
        });

        buttonSubstractItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfItemsInContainerSubstract.getText().toString().contains(".") || numberOfItemsInContainerSubstract.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewAdvanced.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfItemsInContainerSubstract.getText().toString().equals(null) || numberOfItemsInContainerSubstract.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewAdvanced.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Integer.valueOf(numberOfItemsInContainerSubstract.getText().toString()) > overzichtAantalItemsInContainer) {
                            Toast.makeText(TabFragment1ViewAdvanced.this, "There can only be " + String.valueOf(overzichtAantalItemsInContainer) + " items in one container. Choose a number between 0 and " + String.valueOf(overzichtAantalItemsInContainer) + ".", Toast.LENGTH_SHORT).show();
                        } else {
                            //Hier wegschrijven naar overzichtsveld.
                            inputAantalItemsSubstract = Integer.valueOf(numberOfItemsInContainerSubstract.getText().toString());

                            adapter = new MyAdapter(TabFragment1ViewAdvanced.this, generateDataItemsSubstract());
                            listView = (ListView) findViewById(R.id.listViewInventory);
                            listView.setAdapter(adapter);
                            Toast.makeText(TabFragment1ViewAdvanced.this, String.valueOf(numberOfItemsInContainerSubstract.getText()) + " item(s) substracted from one container.", Toast.LENGTH_SHORT).show();
                            numberOfItemsInContainerSubstract.setText("");
                        }
                    }
                }
            }
        });

        buttonSubstractContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfContainerSubstract.getText().toString().contains(".") || numberOfContainerSubstract.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewAdvanced.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfContainerSubstract.getText().toString().equals(null) || numberOfContainerSubstract.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewAdvanced.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Hier wegschrijven naar overzichtsveld.
                        inputAantalContainersSubstract = Integer.valueOf(numberOfContainerSubstract.getText().toString());

                        adapter = new MyAdapter(TabFragment1ViewAdvanced.this, generateDataContainersSubstract());
                        listView = (ListView) findViewById(R.id.listViewInventory);
                        listView.setAdapter(adapter);
                        Toast.makeText(TabFragment1ViewAdvanced.this, String.valueOf(numberOfContainerSubstract.getText()) + " full container(s) substracted.", Toast.LENGTH_SHORT).show();
                        numberOfContainerSubstract.setText("");
                    }
                }
            }
        });

        buttonAddEmptyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfEmptyContainerAdd.getText().toString().contains(".") || numberOfEmptyContainerAdd.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewAdvanced.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfEmptyContainerAdd.getText().toString().equals(null) || numberOfEmptyContainerAdd.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewAdvanced.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Hier wegschrijven naar overzichtsveld.
                        inputAantalEmptyContainersAdd = Integer.valueOf(numberOfEmptyContainerAdd.getText().toString());

                        adapter = new MyAdapter(TabFragment1ViewAdvanced.this, generateDataEmptyContainersAdd());
                        listView = (ListView) findViewById(R.id.listViewInventory);
                        listView.setAdapter(adapter);
                        Toast.makeText(TabFragment1ViewAdvanced.this, String.valueOf(numberOfEmptyContainerAdd.getText()) + " empty container(s) added.", Toast.LENGTH_SHORT).show();
                        numberOfEmptyContainerAdd.setText("");
                    }
                }
            }
        });

        buttonSubstractEmptyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfEmptyContainerSubstract.getText().toString().contains(".") || numberOfEmptyContainerSubstract.getText().toString().contains(",")) {
                    Toast.makeText(TabFragment1ViewAdvanced.this, "Value can only be an integer.", Toast.LENGTH_SHORT).show();
                } else {
                    if (numberOfEmptyContainerSubstract.getText().toString().equals(null) || numberOfEmptyContainerSubstract.getText().toString().equals("")) {
                        Toast.makeText(TabFragment1ViewAdvanced.this, "Value has to be more than 0.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Hier wegschrijven naar overzichtsveld.
                        inputAantalEmptyContainersSubstract = Integer.valueOf(numberOfEmptyContainerSubstract.getText().toString());

                        adapter = new MyAdapter(TabFragment1ViewAdvanced.this, generateDataEmptyContainersSubstract());
                        listView = (ListView) findViewById(R.id.listViewInventory);
                        listView.setAdapter(adapter);
                        Toast.makeText(TabFragment1ViewAdvanced.this, String.valueOf(numberOfEmptyContainerSubstract.getText()) + " empty container(s) substracted.", Toast.LENGTH_SHORT).show();
                        numberOfEmptyContainerSubstract.setText("");
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
                product.put("extraBoxes", overzichtAantalContainers);
                product.put("catName", category);
                productRef.child("products/" + category).updateChildren(product);

                Intent intentViewAdvancedConfirmation = new Intent(getApplicationContext(), TabFragment1ViewAdvancedConfirmation.class);
                intentViewAdvancedConfirmation.putExtra("storagePlace", storagePlace);
                intentViewAdvancedConfirmation.putExtra("category", category);
                intentViewAdvancedConfirmation.putExtra("items", overzichtAantalItems);
                intentViewAdvancedConfirmation.putExtra("containers", overzichtAantalContainers);
                intentViewAdvancedConfirmation.putExtra("edit", editView);
                startActivity(intentViewAdvancedConfirmation);
                TabFragment1ViewAdvanced.getInstance().finish();
            }
        });
    }

    private ArrayList<Model> generateData() {
        //Moet uit database gehaald worden.
        item1 = "Items";
        item2 = "Containers";

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(overzichtAantalItems)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(overzichtAantalContainers)));

        return models;
    }

    private ArrayList<Model> generateDataItemsAdd() {
        //Moet uit database gehaald worden.
        item1 = "Items";
        item2 = "Containers";

        overzichtAantalItems += inputAantalItemsAdd;
        overzichtAantalContainers += 1;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(overzichtAantalItems)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(overzichtAantalContainers)));

        return models;
    }

    private ArrayList<Model> generateDataContainersAdd() {
        //Moet uit database gehaald worden.
        item1 = "Items";
        item2 = "Containers";

        overzichtAantalItems += (inputAantalContainersAdd * overzichtAantalItemsInContainer);
        overzichtAantalContainers += inputAantalContainersAdd;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(overzichtAantalItems)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(overzichtAantalContainers)));

        return models;
    }

    private ArrayList<Model> generateDataItemsSubstract() {
        //Moet uit database gehaald worden.
        item1 = "Items";
        item2 = "Containers";

        overzichtAantalItems -= inputAantalItemsSubstract;
        //overzichtAantalContainers += 1;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(overzichtAantalItems)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(overzichtAantalContainers)));

        return models;
    }

    private ArrayList<Model> generateDataContainersSubstract() {
        //Moet uit database gehaald worden.
        item1 = "Items";
        item2 = "Containers";

        overzichtAantalItems -= (inputAantalContainersSubstract * overzichtAantalItemsInContainer);
        overzichtAantalContainers -= inputAantalContainersSubstract;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(overzichtAantalItems)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(overzichtAantalContainers)));

        return models;
    }

    private ArrayList<Model> generateDataEmptyContainersAdd() {
        //Moet uit database gehaald worden.
        item1 = "Items";
        item2 = "Containers";

        overzichtAantalContainers += inputAantalEmptyContainersAdd;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(overzichtAantalItems)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(overzichtAantalContainers)));

        return models;
    }

    private ArrayList<Model> generateDataEmptyContainersSubstract() {
        //Moet uit database gehaald worden.
        item1 = "Items";
        item2 = "Containers";

        overzichtAantalContainers -= inputAantalEmptyContainersSubstract;

        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(category));
        models.add(new Model(R.drawable.beer_bottle, item1, String.valueOf(overzichtAantalItems)));
        models.add(new Model(R.drawable.box, item2, String.valueOf(overzichtAantalContainers)));

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

    public static TabFragment1ViewAdvanced getInstance(){
        return  tabFragment1ViewAdvanced;
    }
}
