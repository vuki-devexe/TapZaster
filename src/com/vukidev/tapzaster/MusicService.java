package com.vukidev.tapzaster;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.content.res.AssetFileDescriptor;
import java.util.Random;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.content.SharedPreferences;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class MusicService extends Service {
    private MediaPlayer mp;
    private List<String> playlist = new ArrayList<>();
    private Random random = new Random();

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
        SAVEMEM.MuteMjusic = prefs.getInt("MuteMusic", 0);
        
        loadAllMusic();

        String action = intent.getAction();
        if (action != null) {
			if (action.equals("RELOAD_PLAYLIST")) {
				loadAllMusic();
				playNext();
			} else if (action.equals("PAUSE")) {
				if (mp != null && mp.isPlaying()) mp.pause();
			} else if (action.equals("RESUME")) {
				if (mp != null && !mp.isPlaying()) mp.start();
			}
			else if (SAVEMEM.MuteMjusic == 1) {
				if (mp != null && mp.isPlaying()) mp.pause();
			}
        } else {
            if (mp == null) playNext();
        }
        return START_STICKY;
    }

    private void playNext() {
		if (SAVEMEM.MuteMjusic == 1 || playlist.isEmpty()) return;

		try {
			if (mp != null) {
				mp.release();
				mp = null;
			}

			mp = new MediaPlayer();
			String songPath = playlist.get(random.nextInt(playlist.size()));

			if (songPath.startsWith("asset:")) {
				String cleanPath = songPath.replace("asset:", "");
				android.content.res.AssetFileDescriptor afd = getAssets().openFd(cleanPath);
				mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				afd.close();
			} else {
				mp.setDataSource(songPath);
			}

			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer p) { playNext(); }
			});

			mp.prepare();
			mp.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadAllMusic() {
		playlist.clear();

		playlist.add("asset:music/music1.mp3");
		playlist.add("asset:music/music2.mp3");
		playlist.add("asset:music/music3.mp3");
		playlist.add("asset:music/music4.mp3");
		playlist.add("asset:music/music5.mp3");
		playlist.add("asset:music/music6.mp3");
		playlist.add("asset:music/music7.mp3");

		String path = "/storage/emulated/0/VukiDev/Tapzaster/CMusic/";
		java.io.File directory = new java.io.File(path);
		java.io.File[] files = directory.listFiles();
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		if (files != null) {
			for (java.io.File file : files) {
				String name = file.getName();
				if (name.endsWith(".mp3") || name.endsWith(".wav")) {
					playlist.add(file.getAbsolutePath()); // External path
				}
			}
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