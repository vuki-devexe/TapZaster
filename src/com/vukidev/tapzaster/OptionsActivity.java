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

public class OptionsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
		
		Button btnabout = (Button) findViewById(R.id.btn_about);
		btnabout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(OptionsActivity.this, about.class);
				startActivity(i);
			}
		});
		
        Button btnSave1 = (Button) findViewById(R.id.btn_save1);
        btnSave1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSaveDialog("Slot 1");
            }
        });

        Button btnSave2 = (Button) findViewById(R.id.btn_save2);
        btnSave2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSaveDialog("Slot 2");
            }
        });
		Button btnReset = (Button) findViewById(R.id.btn_Restall);
		btnReset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new android.app.AlertDialog.Builder(OptionsActivity.this)
					.setTitle("!!! WARNING !!!")
					.setMessage("This will wipe all save slots and settings This cannot be undone!")
					.setPositiveButton("DELETE EVERYTHING", new android.content.DialogInterface.OnClickListener() {
						public void onClick(android.content.DialogInterface dialog, int which) {
							
							android.content.SharedPreferences.Editor editor = getSharedPreferences("ZasterPrefs", MODE_PRIVATE).edit();
							editor.clear();
							editor.putInt("lastslot", 1);
							editor.commit();

							android.widget.Toast.makeText(OptionsActivity.this, "All Data Purged.", 0).show();

							
							finish(); 
						}
					})
					.setNegativeButton("Cancel", null)
					.show();
			}
		});
        Button backBtn = (Button) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    // THIS MUST BE OUTSIDE onCreate
    private void showSaveDialog(final String slotName) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Manage " + slotName);
        builder.setMessage("What would you like to do with this save?");

        builder.setPositiveButton("Save Current", new android.content.DialogInterface.OnClickListener() {
            public void onClick(android.content.DialogInterface dialog, int which) {
                if (slotName == "1") {
					SaveSys.saveGame(OptionsActivity.this,1);
				}else if (slotName == "2") {
					SaveSys.saveGame(OptionsActivity.this,2);
				}
                
                android.widget.Toast.makeText(OptionsActivity.this, "Saved to " + slotName, 0).show();
            }
        });

        builder.setNeutralButton("Load This", new android.content.DialogInterface.OnClickListener() {
            public void onClick(android.content.DialogInterface dialog, int which) {
                android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
				android.content.SharedPreferences.Editor editor = prefs.edit();

				int slotNum = slotName.contains("1") ? 1 : 2;

				editor.putInt("lastslot", slotNum);
				editor.commit();
                SaveSys.loadFromSlot(OptionsActivity.this, slotName);
                android.widget.Toast.makeText(OptionsActivity.this, "Loaded " + slotName, 0).show();
				
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    
}