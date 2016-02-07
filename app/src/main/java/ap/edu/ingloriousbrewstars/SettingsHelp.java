package ap.edu.ingloriousbrewstars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander Peeters on 12/1/2015.
 */
public class SettingsHelp extends AppCompatActivity {

    TextView header;
    SwipeMenuListView listView;
    ListAdapter mListAdapter;
    List mAppList;
    AppAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_help);
        overridePendingTransition(R.anim.animation, R.anim.animation2);

        header = (TextView) findViewById(R.id.textViewHeader);
        header.bringToFront();

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

        listView = (SwipeMenuListView) findViewById(R.id.listView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem videoItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                videoItem.setBackground(new ColorDrawable(Color.rgb(96, 96, 96)));
                // set item width
                videoItem.setWidth(dp2px(90));
                // set a icon
                videoItem.setIcon(R.drawable.movie);
                // add to menu
                menu.addMenuItem(videoItem);


                SwipeMenuItem tutorialItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                tutorialItem.setBackground(new ColorDrawable(Color.rgb(128, 128, 128)));
                // set item width
                tutorialItem.setWidth(dp2px(90));
                // set a icon
                tutorialItem.setIcon(R.drawable.tutorial);
                // add to menu
                menu.addMenuItem(tutorialItem);
            }
        };

        // set creator
        listView.setMenuCreator(creator);

        mAppList = new ArrayList<>();
        mAppList.add("Create inventory");
        mAppList.add("Edit inventory");
        mAppList.add("Search by date");
        mAppList.add("Search by category");
        mAppList.add("Export Excel-file");
        mAppList.add("Add/Edit category");
        mAppList.add("Add/Edit storage place");
        mAppList.add("Change app colors");

        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
        mAdapter = new AppAdapter();
        listView.setAdapter(mAdapter);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // video
                        Intent intentVideo = new Intent(getApplicationContext(), SettingsHelpVideo.class);

                        switch (position) {
                            case 0:
                                intentVideo.putExtra("fileName", R.raw.createinventory);
                                startActivity(intentVideo);
                                break;
                            case 1:
                                intentVideo.putExtra("fileName", R.raw.editinventory);
                                startActivity(intentVideo);
                                break;
                            case 2:
                                intentVideo.putExtra("fileName", R.raw.searchbydate);
                                startActivity(intentVideo);
                                break;
                            case 3:
                                intentVideo.putExtra("fileName", R.raw.searchbycategory);
                                startActivity(intentVideo);
                                break;
                            case 4:
                                intentVideo.putExtra("fileName", R.raw.emailreport);
                                startActivity(intentVideo);
                                break;
                            case 5:
                                intentVideo.putExtra("fileName", R.raw.editaddcategory);
                                startActivity(intentVideo);
                                break;
                            case 6:
                                intentVideo.putExtra("fileName", R.raw.editaddstorageplace);
                                startActivity(intentVideo);
                                break;
                            case 7:
                                intentVideo.putExtra("fileName", R.raw.changeappcolor);
                                startActivity(intentVideo);
                                break;
                        }

                        break;
                    case 1:
                        // tutorial
                        String title;
                        String summary;
                        Intent intentSummary = new Intent(getApplicationContext(), SettingsHelpSummary.class);

                        switch (position) {
                            case 0:
                                title = "Help | Create inventory";
                                summary = "When you create an inventory, you are actually going to create a detailed list of articles for a certain storage place and the number of each article.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                            case 1:
                                title = "Help | Edit inventory";
                                summary = "It's possible to edit an existing inventory. For example, today you created an inventory for 'product 1' in 'storage place 1'. Afterwards you see that you forgot to add an number of 'product 1'. You will still be able to add that certain number of 'product 1' to the existing inventory of 'storage place 1'.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                            case 2:
                                title = "Help | Search by date";
                                summary = "The function 'Search by date' gives you an overview of the inventories created for a specific selected day.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                            case 3:
                                title = "Help | Search by category";
                                summary = "The function 'Search by category' gives you an overview of the number of products of the chosen category available for each storage place.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                            case 4:
                                title = "Help | Export Excel-file";
                                summary = "The function 'Export Excel-file' allows you to create and send an overview of all inventories in an Excel-file via email, to the recipients that you want.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                            case 5:
                                title = "Help | Add/Edit category";
                                summary = "It's possible to add a new category and to edit an existing category. When you add or edit a category, it will immediately be available throughout the entire application.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                            case 6:
                                title = "Help | Add/Edit storage place";
                                summary = "It's possible to add a new storage place and to edit an existing one. When you add or edit a storage place, it will immediately be available throughout the entire application.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                            case 7:
                                title = "Help | Change app colors";
                                summary = "Thanks to the 'Preferences Page', you can modify several aspects of the application. For example, it is possible to modify the colors of the application. You can change the color of the toolbar and the color of the background to your favorite color.";
                                intentSummary.putExtra("title", title);
                                intentSummary.putExtra("summary", summary);
                                startActivity(intentSummary);
                                break;
                        }

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


        /*
        Intent intentVideo = new Intent(getApplicationContext(), SettingsHelpVideo.class);
        intentVideo.putExtra("fileName", R.raw.test);
        startActivity(intentVideo);
        */

    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public String getItem(int position) {
            return (String) mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_list_app_2, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            //holder.iv_icon.setImageResource(R.drawable.question);

            switch (position) {
                case 0:
                    holder.iv_icon.setImageResource(R.drawable.helpinventory);
                    break;
                case 1:
                    holder.iv_icon.setImageResource(R.drawable.helpinventory);
                    break;
                case 2:
                    holder.iv_icon.setImageResource(R.drawable.helpreport);
                    break;
                case 3:
                    holder.iv_icon.setImageResource(R.drawable.helpreport);
                    break;
                case 4:
                    holder.iv_icon.setImageResource(R.drawable.helpreport);
                    break;
                case 5:
                    holder.iv_icon.setImageResource(R.drawable.helpcategory);
                    break;
                case 6:
                    holder.iv_icon.setImageResource(R.drawable.helpstorage);
                    break;
                case 7:
                    holder.iv_icon.setImageResource(R.drawable.helpprefereneces);
                    break;
            }

            holder.tv_name.setText(item);
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(this);
            }
        }
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
