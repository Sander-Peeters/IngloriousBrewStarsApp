package ap.edu.ingloriousbrewstars;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class TabFragment3 extends Fragment {
    View rootView;
    ImageButton categories;
    ImageButton storagePlaces;
    ImageButton inventory;
    ImageButton help;
    ImageButton preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_fragment_3, container, false);

        categories = (ImageButton) rootView.findViewById(R.id.imageButtonCategories);
        categories.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent categories = new Intent(rootView.getContext(), SettingsCategories.class);
                startActivity(categories);
            }
        });

        storagePlaces = (ImageButton) rootView.findViewById(R.id.imageButtonStoragePlaces);
        storagePlaces.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent storagePlaces = new Intent(rootView.getContext(), SettingsStoragePlaces.class);
                startActivity(storagePlaces);
            }
        });

        inventory = (ImageButton) rootView.findViewById(R.id.imageButtonInventory);
        inventory.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent inventory = new Intent(rootView.getContext(), SettingsInventory.class);
                startActivity(inventory);
            }
        });

        help = (ImageButton) rootView.findViewById(R.id.imageButtonHelp);
        help.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent help = new Intent(rootView.getContext(), SettingsHelp.class);
                startActivity(help);
            }
        });

        preferences = (ImageButton) rootView.findViewById(R.id.imageButtonPreferences);
        preferences.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent help = new Intent(rootView.getContext(), SettingsPreferences.class);
                startActivity(help);
            }
        });




        return rootView;
    }

}