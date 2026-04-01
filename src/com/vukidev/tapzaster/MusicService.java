package com.vukidev.tapzaster;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.content.res.AssetFileDescriptor;
import java.util.Random;

public class MusicService extends Service {
    private MediaPlayer mp;
    private String[] playlist = {"music/music1.mp3", "music/music2.mp3", "music/music3.mp3", "music/music4.mp3"};
    private Random random = new Random();

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();

		if (action != null) {
			if (action.equals("PAUSE")) {
				if (mp != null && mp.isPlaying()) mp.pause();
			} else if (action.equals("RESUME")) {
				if (mp != null && !mp.isPlaying()) mp.start();
			}
		} else {
			// First time starting the app
			if (mp == null) playNext();
		}
		return START_STICKY;
	}
    private void playNext() {
		try {
			if (mp != null) {
				mp.stop();    // Stop the actual playback
				mp.release(); // Kill the memory
				mp = null;    // Help the GC see it's gone
			}
			
			mp = new MediaPlayer();
			
			// Pick a random song
			String songPath = playlist[random.nextInt(playlist.length)];
			AssetFileDescriptor afd = getAssets().openFd(songPath);
			
			// Android 1.0 specific check
			if (afd != null) {
				mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				afd.close(); // Close descriptor as soon as source is set
			}

			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer p) { 
					playNext(); 
				}
			});

			mp.prepare();
			mp.start();
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}

    @Override
    public void onDestroy() {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        super.onDestroy();
    }
}