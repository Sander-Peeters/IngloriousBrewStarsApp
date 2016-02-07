package ap.edu.ingloriousbrewstars;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by Sander Peeters on 12/1/2015.
 */
public class SettingsHelpVideo extends AppCompatActivity {

    int fileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_help_video);
        overridePendingTransition(R.anim.animation, R.anim.animation2);

        fileName = getIntent().getIntExtra("fileName", 0);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        VideoView videoHolder = (VideoView) findViewById(R.id.videoView);
        videoHolder.setMediaController(new MediaController(this));
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + fileName);
        videoHolder.setVideoURI(video);
        videoHolder.start();

        videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

    }
}
