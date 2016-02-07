package ap.edu.ingloriousbrewstars;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by Sander Peeters on 12/16/2015.
 */
public class ReportExportExcel extends Activity {
    SharedPreferences SP;
    boolean appConfirm;
    final Context context = this;
    Button excelButton;
    EditText emailaddress1EditText;
    EditText emailaddress2EditText;
    EditText emailNotaEditText;
    String emailaddress1;
    String emailaddress2;
    String emailaddress1Preference;
    String emailaddress2Preference;
    String emailnota;
    File wbfile;
    Firebase mFirebaseRef1;
    Firebase mFirebaseRef2;
    Firebase mFirebaseRef3;
    int countcolumn1;
    int countcolumn2;
    int countcolumn3;
    int countcolumn4;
    int countcolumn5;
    int countrow;
    ScheduledExecutorService worker;
    WritableWorkbook wb;
    int childsNumber;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_export_excel);

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        appConfirm = SP.getBoolean("applicationConfirmation", true);

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setUseTemporaryFileDuringWrite(true);
        wbfile = new File(getFilesDir() + File.separator + "Report.xls");

        countcolumn1 = 0;
        countcolumn2 = 1;
        countcolumn3 = 2;
        countcolumn4 = 3;
        countcolumn5 = 4;
        countrow = 1;
        childsNumber = 0;
        count = 0;

        worker = Executors.newSingleThreadScheduledExecutor();
        wb = null;

        try{
            wb = Workbook.createWorkbook(wbfile,wbSettings);
            final WritableSheet sheet = wb.createSheet("IBS Report", 0);
            createCell(sheet, 0, 0, "DATE");
            createCell(sheet, 0, 1, "STORAGE PLACE");
            createCell(sheet, 0, 2, "CATEGORY");
            createCell(sheet, 0, 3, "ITEMS");
            createCell(sheet, 0, 4, "CONTAINERS");

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
                                countrow++;
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    count++;
                                    final String category = (String) child.child("catName").getValue();
                                    final int items = (int) (long) child.child("flessen").getValue();
                                    int containers = 0;
                                    if (child.child("extraBoxes").exists()) {
                                        containers = (int) (long) child.child("extraBoxes").getValue();
                                    }

                                    try {
                                        createCell(sheet, countrow, countcolumn1, date);
                                        createCell(sheet, countrow, countcolumn2, storage);
                                        createCell(sheet, countrow, countcolumn3, category);
                                        createCell(sheet, countrow, countcolumn4, String.valueOf(items));
                                        if (child.child("extraBoxes").exists()) {
                                            createCell(sheet, countrow, countcolumn5, String.valueOf(containers));
                                        }
                                        else {
                                            createCell(sheet, countrow, countcolumn5, "/");
                                        }
                                        countrow++;
                                    } catch (WriteException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (childsNumber == count) {
                                    try {
                                        wb.write();
                                        wb.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (WriteException e) {
                                        e.printStackTrace();
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
        }catch(Exception ex){
            ex.printStackTrace();
        }

        if(wbfile == null){
            Log.d("----FILE----", "NULL");
        }else{
            Log.d("----FILE----", "Not Null");
        }

        emailaddress1EditText = (EditText) findViewById(R.id.emailReceiver1);
        emailaddress2EditText = (EditText) findViewById(R.id.emailReceiver2);
        emailaddress1Preference = SP.getString("emailReceiver1", "");
        if (!emailaddress1Preference.toString().equals("")) {
            emailaddress1EditText.setText(emailaddress1Preference.toString());
        }
        emailaddress2Preference = SP.getString("emailReceiver2", "");
        if (!emailaddress2Preference.toString().equals("")) {
            emailaddress2EditText.setText(emailaddress2Preference.toString());
        }
        emailNotaEditText = (EditText) findViewById(R.id.nota);

        excelButton = (Button) findViewById(R.id.excelButton);

        //checks if there is an internet connection.
        if (!isNetworkAvailable()) {
            excelButton.setEnabled(false);
            excelButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.offline, 0, 0, 0);
            excelButton.setText("No Internet Connection");
        }

        excelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailaddress1 = emailaddress1EditText.getText().toString();
                emailaddress2 = emailaddress2EditText.getText().toString();
                emailnota = emailNotaEditText.getText().toString();

                Mail m = new Mail("Inglorious.Brew.Stars.App@gmail.com", "IngloriousBrewStarsApp"); //email, wachtwoord
                m.addReceiver(emailaddress1);
                if (!emailaddress2.equals("")) {
                    m.addReceiver(emailaddress2);
                }

                if (!emailnota.equals("")) {
                    m.setNota(emailnota);
                }

                try {
                    m.addAttachment(wbfile.getAbsolutePath());

                    if(m.send()) {
                        Toast.makeText(ReportExportExcel.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ReportExportExcel.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e) {
                    //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }
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
            }
        }
    }

    public void createCell(WritableSheet sheet, int column, int row, String content) throws WriteException {
        Label newCell = new Label(row, column, content);
        sheet.addCell(newCell);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}