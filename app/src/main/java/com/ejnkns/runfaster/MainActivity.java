package com.ejnkns.runfaster;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {
    // initialise spotify vars here, might need to use later
    private static String CLIENT_ID;
    private static String REDIRECT_URI;
    private SpotifyAppRemote mSpotifyAppRemote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        // have to set the vars in onStart so we know they'll be read
        // properly from res/value by getString
        // These strings are stored in an xml file ignored by git
        CLIENT_ID = getString(R.string.CLIENT_ID);
        REDIRECT_URI = getString(R.string.REDIRECT_URI);
        super.onStart();
        // Set the connection parameters for spotify
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        // connect to spotify app remote
        SpotifyAppRemote.connect(this, connectionParams,
                // use SpotifyAppRemote.Connector to connect to spotify
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        // get an instance of SpotifyAppRemote
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void handlePlayerState(PlayerState playerState) {

    }

    private void connected() {
        // is there playerApi.load? not play yet?
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
            .subscribeToPlayerState()
            .setEventCallback(playerState -> {
                handlePlayerState(playerState);
            });

        // can enable buttons now
        final Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // resume music
                mSpotifyAppRemote.getPlayerApi().resume();
                Log.d("MainActivity", "playing");

            }
        });
        final Button pauseButton = findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // pause music
                mSpotifyAppRemote.getPlayerApi().pause();
                Log.d("MainActivity", "pausing");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
