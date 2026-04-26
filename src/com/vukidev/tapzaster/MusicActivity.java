package com.vukidev.tapzaster;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.os.Handler;
import android.media.MediaPlayer;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.content.Intent;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.content.SharedPreferences;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class MusicActivity extends Activity {

	private List<String> MusicList = new ArrayList<>();
	private ArrayAdapter<String> adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
		android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);

		Set<String> set = prefs.getStringSet("ZasterSet", new HashSet<String>());

		MusicList = new ArrayList<>(set);
		
		
		ListView listView = (ListView) findViewById(R.id.Music_List);

		adapter = new ArrayAdapter<>(
			this, 
			android.R.layout.simple_list_item_1, 
			MusicList
		);
		listView.setAdapter(adapter);
		
		scanMusicFolder();

        Button backBtn = (Button) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
		final Button MuteMBtn = (Button) findViewById(R.id.btn_MuteM);
        MuteMBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				if (SAVEMEM.MuteMjusic == 1) {
					SAVEMEM.MuteMjusic = 0;
				} else {
					SAVEMEM.MuteMjusic = 1;
				}
				if (SAVEMEM.MuteMjusic == 1) {
					MuteMBtn.setText("Music: OFF");
					MuteMBtn.setBackgroundColor(0xffff0000);
				} else {
					MuteMBtn.setText("Music: ON");
					MuteMBtn.setBackgroundColor(0xff00ff00);
				}
				save();
            }
        });
		final Button ReloadMBtn = (Button) findViewById(R.id.btn_loadmm);
		ReloadMBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scanMusicFolder();
				
				Intent i = new Intent(MusicActivity.this, MusicService.class);
				i.setAction("RELOAD_PLAYLIST");
				startService(i);
			}
		});
		//final Button SaveMBtn = (Button) findViewById(R.id.btn_SaveM);
		//SaveMBtn.setOnClickListener(new View.OnClickListener() {
		//	public void onClick(View v) {
		//		save();
		//		
		//		adapter.notifyDataSetChanged();
		//	}
		//});
    }

    private void save() {
		SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("MuteMusic", SAVEMEM.MuteMjusic);
		editor.apply();

		Intent serviceIntent = new Intent(this, MusicService.class);
		if (SAVEMEM.MuteMjusic == 1) {
			serviceIntent.setAction("PAUSE");
		} else {
			serviceIntent.setAction("RESUME");
		}
		
		startService(serviceIntent);
	}
	
	private void scanMusicFolder() {
		MusicList.clear();

		MusicList.add("Too slow Y0Y0lox mix by Y0Y0lox");
		MusicList.add("music2.mp3");
		MusicList.add("music3.mp3");
		MusicList.add("music4.mp3");
		MusicList.add("Tapping Zaster by Y0Y0lox");
		MusicList.add("music6.mp3");
		MusicList.add("Dih Song by Besmirch Adulation (matt n' yoyo)");
		String path = "/storage/emulated/0/VukiDev/Tapzaster/CMusic/";
		java.io.File directory = new java.io.File(path);
		
		// Create the folder if it doesn't exist yet
		if (!directory.exists()) {
			directory.mkdirs();
		}

		java.io.File[] files = directory.listFiles();

		if (files != null) {
			for (java.io.File file : files) {
				String name = file.getName();
				if (name.endsWith(".mp3") || name.endsWith(".wav")) {
					MusicList.add(name);
				}
			}
		}
		
		// Refresh the ListView
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
}