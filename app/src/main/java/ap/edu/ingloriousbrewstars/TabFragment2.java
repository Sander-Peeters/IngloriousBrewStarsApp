package ap.edu.ingloriousbrewstars;

        import android.app.ActionBar;
        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v4.app.Fragment;
        import android.support.v4.view.ViewPager;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu;
        import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay;

        import android.os.Bundle;
        import android.support.v7.app.ActionBarActivity;
        import android.widget.AbsListView;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.fasterxml.jackson.annotation.JsonAutoDetect;
        import com.firebase.client.ChildEventListener;
        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.ValueEventListener;
        import com.firebase.ui.FirebaseListAdapter;

        import org.eazegraph.lib.charts.PieChart;
        import org.eazegraph.lib.models.PieModel;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Collection;
        import java.util.Date;
        import java.util.Iterator;
        import java.util.List;
        import java.util.ListIterator;
        import java.util.Map;
        import java.util.Random;
        import java.util.concurrent.Semaphore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class TabFragment2 extends Fragment {
    View rootView;
    private ExpandableMenuOverlay menuOverlay;
    private Firebase mFirebaseRef1;
    private Firebase mFirebaseRef2;
    List categoriesList;
    List itemsList;
    int count;
    int childsNumber;
    List<String> colorList;
    List dates;
    List categories;
    List storages;
    PieChart mPieChart;
    int colorID;
    String color;
    String categorySaved;
    float colorFactor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_fragment_2, container, false);
        Firebase.setAndroidContext(rootView.getContext());

        categoriesList  = new ArrayList();
        itemsList  = new ArrayList();
        colorList = new ArrayList();
        colorList.add("#FE6DA8"); colorList.add("#820D16"); colorList.add("#CDA67F");
        colorList.add("#FED70E"); colorList.add("#01ad4b"); colorList.add("#0067b2");
        colorList.add("#DAF650"); colorList.add("#71717e"); colorList.add("#ff784e");
        colorList.add("#003366"); colorList.add("#4e3518"); colorList.add("#7580bc");
        colorList.add("#ff9900"); colorList.add("#009933"); colorList.add("#800000");

        menuOverlay = (ExpandableMenuOverlay) rootView.findViewById(R.id.button_menu);
        menuOverlay.setOnMenuButtonClickListener(new ExpandableButtonMenu.OnMenuButtonClick() {
            @Override
            public void onClick(ExpandableButtonMenu.MenuButton action) {
                switch (action) {
                    case MID:
                        Intent searchByCategory = new Intent(rootView.getContext(), ReportSearchCategory.class);
                        startActivity(searchByCategory);
                        menuOverlay.getButtonMenu().toggle();
                        break;
                    case LEFT:
                        Intent searchByDate = new Intent(rootView.getContext(), ReportSearchDate.class);
                        startActivity(searchByDate);
                        menuOverlay.getButtonMenu().toggle();
                        break;
                    case RIGHT:
                        Intent ExportExcel = new Intent(rootView.getContext(), ReportExportExcel.class);
                        startActivity(ExportExcel);
                        menuOverlay.getButtonMenu().toggle();
                        break;
                }
            }
        });

        //Vullen van PieChart op TabFragement2.
        generatePieChart();

        return rootView;
    }

    //Vullen van PieChart op TabFragement2.
    public void generatePieChart() {
        count = 0;
        dates = new ArrayList();
        categories = new ArrayList();
        storages = new ArrayList();
        mPieChart = (PieChart) rootView.findViewById(R.id.piechart);
        colorID = 0;
        color = "";
        categorySaved = "";
        colorFactor = 0;


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
                            int childsCount = (int) (long) snapshot.getChildrenCount();
                            childsNumber += childsCount;
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

        mFirebaseRef1 = new Firebase("https://shining-heat-1471.firebaseio.com/categories");
        mFirebaseRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    final String category = (String) child.child("name").getValue();

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
                                            count++;

                                            if (child.child("catName").getValue().equals(category)) {

                                                if (!storages.contains(storage)) {
                                                    storages.add(storage);

                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                                                    Date convertedDate = new Date();
                                                    try {
                                                        convertedDate = dateFormat.parse(date);
                                                    } catch (ParseException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                    dates.add(convertedDate);
                                                } else {
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                                                    Date convertedDate = new Date();
                                                    try {
                                                        convertedDate = dateFormat.parse(date);
                                                    } catch (ParseException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                    Date alreadyExistingDate = (Date) dates.get(storages.indexOf(storage));

                                                    if (convertedDate.after(alreadyExistingDate)) {
                                                        dates.set(storages.indexOf(storage), convertedDate);
                                                    }
                                                }
                                            }
                                        }

                                        if (childsNumber == count) {
                                            for (int i = 0; i < storages.size(); i++) {
                                                final Date date = (Date) dates.get(i);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
                                                final String dateString = dateFormat.format(date);
                                                final String storage = storages.get(i).toString();

                                                mFirebaseRef2 = new Firebase("https://shining-heat-1471.firebaseio.com/inventories/" + dateString + storage + "/products");
                                                mFirebaseRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        for (DataSnapshot child : snapshot.getChildren()) {
                                                            String categoryDB = (String) child.child("catName").getValue();

                                                            if (categoryDB.equals(category)) {
                                                                if (!categorySaved.equals(categoryDB)) {
                                                                    categorySaved = categoryDB;
                                                                    colorID++;
                                                                    colorFactor = 0;
                                                                } else {
                                                                    colorFactor += 0.15;
                                                                }
                                                                int items = (int) (long) child.child("flessen").getValue();
                                                                mPieChart.addPieSlice(new PieModel(category + " - " + storage, items, lighter(Color.parseColor(String.valueOf(colorList.get(colorID))), colorFactor)));
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(FirebaseError firebaseError) {
                                                        Log.e("error", firebaseError.getMessage());
                                                    }
                                                });
                                            }
                                            count = 0;
                                            storages.clear();
                                            dates.clear();
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
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("error", firebaseError.getMessage());
            }
        });
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }
}