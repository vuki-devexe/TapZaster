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
import android.content.Intent;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.content.pm.PackageManager;
import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class HomeActivity extends Activity {
	
	private final BigInteger BI_ZERO = BigInteger.valueOf(0);
	private final BigInteger BI_ONE = BigInteger.valueOf(1);
	private final BigInteger BI_FIVE = BigInteger.valueOf(5);
	private final BigInteger BI_TEN = BigInteger.valueOf(10);

	private BigInteger f = BI_ZERO;
	private BigInteger f2 = BI_ZERO;
	private BigInteger f3 = BI_ZERO;
	private BigInteger f4 = BI_ZERO;
	private BigInteger f5 = BI_ZERO;
	
	private TextView StatusText;
	private MediaPlayer e1, e2, e3, e4;
	private ImageView zasterBtn;
	
	boolean isSwitching = false;
	
	private String formatBigInt(BigInteger value) {
		String s = value.toString();
		int len = s.length();

		if (len <= 3) {
			return s; // 0 to 999
		} else if (len <= 6) {
			// Thousands (K)
			return s.substring(0, len - 3) + "." + s.substring(len - 3, len - 2) + "K";
		} else if (len <= 9) {
			// Millions (M)
			return s.substring(0, len - 6) + "." + s.substring(len - 6, len - 5) + "M";
		} else if (len <= 12) {
			// Billions (B)
			return s.substring(0, len - 9) + "." + s.substring(len - 9, len - 8) + "B";
		} else if (len <= 15) {
			// Trillions (T)
			return s.substring(0, len - 12) + "." + s.substring(len - 12, len - 11) + "T";
		} else {
			// Quadrillion and beyond (QA)
			return s.substring(0, len - 15) + "QA";
		}
	}
	private void playeffec(int sfx) {
		MediaPlayer player = null;
		if (sfx == 1) player = e1;
		else if (sfx == 2) player = e2;
		else if (sfx == 3) player = e3;
		else if (sfx == 4) player = e4;

		if (player != null) {
			if (player.isPlaying()) {
				player.pause();
				player.seekTo(0);
			}
			player.start();
		}
	}
	private void showFloatingText(float x, float y, String displayValue) {
		final TextView floatText = new TextView(this);
		floatText.setText("+" + displayValue);
		floatText.setTextColor(android.graphics.Color.YELLOW);
		floatText.setTextSize(25);
		floatText.setTypeface(null, android.graphics.Typeface.BOLD);

		// Calculate the center of the Zaster
		float centerX = zasterBtn.getLeft() + (zasterBtn.getWidth() / 2) - 50;
		float centerY = zasterBtn.getTop() + (zasterBtn.getHeight() / 2) - 50;

		// Add a little randomness so they don't overlap perfectly
		floatText.setX(centerX + (float)(Math.random() * 100 - 50));
		floatText.setY(centerY);

		final ViewGroup root = (ViewGroup) findViewById(R.id.main_layout_id);
		if (root != null) {
			root.addView(floatText);
			
			floatText.animate()
				.translationYBy(-300) // Float up
				.alpha(0)             // Fade out
				.setDuration(800)
				.withEndAction(new Runnable() {
					@Override
					public void run() {
						root.removeView(floatText);
					}
				}).start();
		}
	}
	@Override
	public void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.activity_home);
		
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			try {
				int hasPermission = checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
				if (hasPermission != android.content.pm.PackageManager.PERMISSION_GRANTED) {
					
					java.lang.reflect.Method method = this.getClass().getMethod("requestPermissions", String[].class, int.class);
					method.invoke(this, new String[]{
						"android.permission.WRITE_EXTERNAL_STORAGE", 
						"android.permission.READ_EXTERNAL_STORAGE"
					}, 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		calcrice();

		e1 = new MediaPlayer();
		e2 = new MediaPlayer();
		e3 = new MediaPlayer();
		e4 = new MediaPlayer();

		new Thread(new Runnable() {
			public void run() {
				try {
					AssetFileDescriptor afd;
					
					afd = getAssets().openFd("exit.mp3");
					e1.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
					e1.prepare(); afd.close();
					
					afd = getAssets().openFd("open.mp3");
					e2.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
					e2.prepare(); afd.close();
					
					afd = getAssets().openFd("click.mp3");
					e3.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
					e3.prepare(); afd.close();
					
					afd = getAssets().openFd("upgrade.mp3");
					e4.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
					e4.prepare(); afd.close();
				} catch (Exception e) { e.printStackTrace(); }
			}
		}).start();
		
		Intent musicIntent = new Intent(this, MusicService.class);
		startService(musicIntent);
		
		StatusText = (TextView) findViewById(R.id.Zasters);
		zasterBtn = (ImageView) findViewById(R.id.zaster);
		
		SaveSys.loadGame(HomeActivity.this, StatusText);
		applySkinToZaster();
		zasterBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				
				BigInteger basePlusphones = BI_ONE.add(SAVEMEM.phones); 
				BigInteger gain = basePlusphones.multiply(SAVEMEM.Mods).add(SAVEMEM.Unbricked).multiply(SAVEMEM.Romdown);
				SAVEMEM.SCORE = SAVEMEM.SCORE.add(gain);
				StatusText.setText(formatBigInt(SAVEMEM.SCORE));
				
				showFloatingText(0, 0, formatBigInt(gain));
				playeffec(3);
				v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).withEndAction(new Runnable() {
					@Override
					public void run() {
						v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
					}
				}).start();
			}
		});
		Button optionsBtn = (Button) findViewById(R.id.options_button);
		optionsBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				isSwitching = true;
				Intent i = new Intent(HomeActivity.this, OptionsActivity.class);
				i.putExtra("SCORE", SAVEMEM.SCORE.toString());
				i.putExtra("phones", SAVEMEM.phonesupg.toString());
				i.putExtra("techs", SAVEMEM.Techzasters.toString());
				i.putExtra("Mods", SAVEMEM.Mods.toString());
				startActivity(i);
				playeffec(2); // Play your open sound
			}
		});
		Button shopBtn = (Button) findViewById(R.id.shop_button);
		shopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showUpgradePopup();
				playeffec(2);
			}
		});
		saveHandler.postDelayed(saveTask, 5000);
	}
	@Override
	protected void onResume() {
		super.onResume();
		SaveSys.loadGame(HomeActivity.this, StatusText);
		applySkinToZaster();
		sendMusicAction("RESUME");
		autoClickHandler.postDelayed(autoClickTask, 750);
		autoClickHandler.postDelayed(saveTask, 5000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
		int lastSlot = prefs.getInt("lastslot", 1);
		SaveSys.saveGame(HomeActivity.this, lastSlot);
		if (!isSwitching) {
			sendMusicAction("PAUSE");
		}
		isSwitching = false;
		saveHandler.removeCallbacks(saveTask);
		autoClickHandler.removeCallbacks(autoClickTask);
	}
	private void calcrice() {
		// f = 10 * (SAVEMEM.phonesupg * 1.5)
		f = SAVEMEM.phonesupg.multiply(BigInteger.valueOf(15)).divide(BI_TEN).multiply(BI_TEN);
		// f2 = 100 * (SAVEMEM.Techzasters * 2)
		f2 = SAVEMEM.Techzasters.multiply(BigInteger.valueOf(200));
		// f3 = 500 * (SAVEMEM.Mods * 3.5)
		f3 = SAVEMEM.Mods.multiply(BigInteger.valueOf(35)).divide(BI_TEN).multiply(BigInteger.valueOf(500));
		
		f4 = SAVEMEM.Unbricked.multiply(BigInteger.valueOf(267)).divide(BI_TEN).multiply(BigInteger.valueOf(10000));
		
		f5 = SAVEMEM.Romdown.multiply(BigInteger.valueOf(267)).divide(BI_TEN).multiply(BigInteger.valueOf(35000));
	}

	private void showUpgradePopup() {
		android.view.LayoutInflater inflater = getLayoutInflater();
		android.view.View dialogView = inflater.inflate(R.layout.upgrade_dlg, null);

		android.widget.TabHost tabs = (android.widget.TabHost) dialogView.findViewById(android.R.id.tabhost);
		tabs.setup();

		android.widget.TabHost.TabSpec spec1 = tabs.newTabSpec("tag1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Upgrades");
		tabs.addTab(spec1);

		android.widget.TabHost.TabSpec spec2 = tabs.newTabSpec("tag2");
		spec2.setContent(R.id.tab2);
		spec2.setIndicator("Items");
		tabs.addTab(spec2);
		
		android.widget.TabHost.TabSpec spec3 = tabs.newTabSpec("tag3");
		spec3.setContent(R.id.tab3);
		spec3.setIndicator("Other");
		tabs.addTab(spec3);

		final Button buyBtn = (Button) dialogView.findViewById(R.id.buy_upgrade_1);
		final Button buyBtn2 = (Button) dialogView.findViewById(R.id.buy_upgrade_2);
		final Button buyBtn3 = (Button) dialogView.findViewById(R.id.buy_upgrade_3);
		final Button buyBtn4 = (Button) dialogView.findViewById(R.id.buy_upgrade_4);
		final Button buyBtn5 = (Button) dialogView.findViewById(R.id.buy_upgrade_5);
		final Button closeBtn = (Button) dialogView.findViewById(R.id.close_button);

		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setView(dialogView);
		
		final android.app.AlertDialog dialog = builder.create();

		calcrice();
		buyBtn.setText("Buy a Phone: " + SAVEMEM.phonesupg + " (Cost: " + formatBigInt(f) + ")");
		buyBtn2.setText("Hire Techzasters: " + SAVEMEM.Techzasters + " (Cost: " + formatBigInt(f2) + ")");
		buyBtn3.setText("Mod a Phone: " + SAVEMEM.Mods + " (Cost: " + formatBigInt(f3) + ")");
		buyBtn4.setText("Unbrick a Phone: " + SAVEMEM.Unbricked + " (Cost: " + formatBigInt(f4) + ")");
		buyBtn5.setText("Rom Downloader: " + SAVEMEM.Romdown + " (Cost: " + formatBigInt(f5) + ")");
		
		buyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.SCORE.compareTo(f) >= 0) {
					SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(f);
					SAVEMEM.phones = SAVEMEM.phones.add(BI_ONE);
					SAVEMEM.phonesupg = SAVEMEM.phonesupg.add(BI_ONE);
					calcrice();
					buyBtn.setText("Buy a Phone: " + SAVEMEM.phonesupg + " (Cost: " + formatBigInt(f) + ")");
					StatusText.setText(formatBigInt(SAVEMEM.SCORE));
					playeffec(4);
				}
			}
		});

		buyBtn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.SCORE.compareTo(f2) >= 0) {
					SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(f2);
					SAVEMEM.Techzasters = SAVEMEM.Techzasters.add(BI_ONE);
					calcrice();
					buyBtn2.setText("Hire Techzasters: " + SAVEMEM.Techzasters + " (Cost: " + formatBigInt(f2) + ")");
					StatusText.setText(formatBigInt(SAVEMEM.SCORE));
					playeffec(4);
				}
			}
		});

		buyBtn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.SCORE.compareTo(f3) >= 0) {
					if (SAVEMEM.Mods.intValue() < SAVEMEM.SuperUpgradeFinal) {
						SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(f3);
						SAVEMEM.Mods = SAVEMEM.Mods.add(BI_ONE);
						
						calcrice();
						
						buyBtn3.setText("Mod a Phone: " + SAVEMEM.Mods + " (Cost: " + formatBigInt(f3) + ")");
						StatusText.setText(formatBigInt(SAVEMEM.SCORE));
						playeffec(4);
						
					} else {
					}
				}
			}
		});
		
		buyBtn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.SCORE.compareTo(f4) >= 0) {
					if (SAVEMEM.hasTWRP == 1) {
						SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(f4);
						SAVEMEM.Unbricked = SAVEMEM.Unbricked.add(BI_ONE);
						calcrice();
						buyBtn4.setText("Unbrick a Phone: " + SAVEMEM.Unbricked + " (Cost: " + formatBigInt(f4) + ")");
						StatusText.setText(formatBigInt(SAVEMEM.SCORE));
						playeffec(4);
					}
				}
			}
		});

		buyBtn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.SCORE.compareTo(f5) >= 0) {
					if (SAVEMEM.hasFasInt == 1) {
						SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(f5);
						SAVEMEM.Romdown = SAVEMEM.Romdown.add(BI_ONE);
						
						calcrice();
						
						buyBtn5.setText("Rom Downloader: " + SAVEMEM.Romdown + " (Cost: " + formatBigInt(f5) + ")");
						StatusText.setText(formatBigInt(SAVEMEM.SCORE));
						playeffec(4);
					} else {
					}
				}
			}
		});

		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playeffec(1);
				dialog.dismiss();
			}
		});
		final Button btnSD = (Button) dialogView.findViewById(R.id.buy_item_1);
		final Button btnTWRP = (Button) dialogView.findViewById(R.id.buy_item_2);
		final Button btnROMs = (Button) dialogView.findViewById(R.id.buy_item_3);
		final Button btnFasInt = (Button) dialogView.findViewById(R.id.buy_item_4);
		final Button btnPhonePar = (Button) dialogView.findViewById(R.id.buy_item_5);

		if(SAVEMEM.hasSDCard == 1) btnSD.setText("SD Card OWNED");
		if(SAVEMEM.hasTWRP == 1) btnTWRP.setText("TWRP OWNED");
		if(SAVEMEM.hasROMs == 1) btnROMs.setText("ROMs OWNED");
		if(SAVEMEM.hasFasInt == 1) btnFasInt.setText("Fast Internet OWNED");
		if(SAVEMEM.hasPhonePar == 1) btnPhonePar.setText("Phone Parts OWNED");

		btnSD.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.hasSDCard == 0 && SAVEMEM.SCORE.compareTo(SAVEMEM.costSD) >= 0) {
					SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(SAVEMEM.costSD);
					SAVEMEM.hasSDCard = 1;
					btnSD.setText("SD Card OWNED");
					StatusText.setText(formatBigInt(SAVEMEM.SCORE));
					playeffec(4);
					
					android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
					int lastSlot = prefs.getInt("lastslot", 1);
					
					SaveSys.saveGame(HomeActivity.this, lastSlot);
				}
			}
		});
		
		btnTWRP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.hasTWRP == 0 && SAVEMEM.SCORE.compareTo(SAVEMEM.costTWRP) >= 0) {
					SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(SAVEMEM.costTWRP);
					SAVEMEM.hasTWRP = 1;
					btnTWRP.setText("TWRP OWNED");
					StatusText.setText(formatBigInt(SAVEMEM.SCORE));
					playeffec(4);
					android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
					int lastSlot = prefs.getInt("lastslot", 1);
					
					SaveSys.saveGame(HomeActivity.this, lastSlot);
				}
			}
		});
		btnROMs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.hasROMs == 0 && SAVEMEM.SCORE.compareTo(SAVEMEM.costROMs) >= 0) {
					SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(SAVEMEM.costROMs);
					SAVEMEM.hasROMs = 1;
					btnROMs.setText("3000 ROMs OWNED");
					StatusText.setText(formatBigInt(SAVEMEM.SCORE));
					playeffec(4);
					
					android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
					int lastSlot = prefs.getInt("lastslot", 1);
					
					SaveSys.saveGame(HomeActivity.this, lastSlot);
				}
			}
		});
		btnFasInt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.hasFasInt == 0 && SAVEMEM.SCORE.compareTo(SAVEMEM.costFasInt) >= 0) {
					SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(SAVEMEM.costFasInt);
					SAVEMEM.hasFasInt = 1;
					btnFasInt.setText("Fast Internet OWNED");
					StatusText.setText(formatBigInt(SAVEMEM.SCORE));
					playeffec(4);
					android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
					int lastSlot = prefs.getInt("lastslot", 1);
					
					SaveSys.saveGame(HomeActivity.this, lastSlot);
				}
			}
		});
		btnPhonePar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SAVEMEM.hasPhonePar == 0 && SAVEMEM.SCORE.compareTo(SAVEMEM.costPhonePar) >= 0) {
					SAVEMEM.SCORE = SAVEMEM.SCORE.subtract(SAVEMEM.costPhonePar);
					SAVEMEM.hasPhonePar = 1;
					btnPhonePar.setText("Phone Parts OWNED");
					StatusText.setText(formatBigInt(SAVEMEM.SCORE));
					playeffec(4);
					
					android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
					int lastSlot = prefs.getInt("lastslot", 1);
					
					SaveSys.saveGame(HomeActivity.this, lastSlot);
				}
			}
		});
		
		final Button btnImport = (Button) dialogView.findViewById(R.id.btn_import_custom);

		btnImport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFileSelectionDialog();
			}
		});
		
		final Button btncode = (Button) dialogView.findViewById(R.id.btn_evilcodes);

		btncode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isSwitching = true;
				Intent i = new Intent(HomeActivity.this, CodeActivity.class);
				startActivity(i);
				playeffec(2);
			}
		});
		dialog.show();
	}
	private Handler autoClickHandler = new Handler();
    
    private Runnable autoClickTask = new Runnable() {
        public void run() {
            if (SAVEMEM.Techzasters.compareTo(BI_ONE) > 0) {
                BigInteger gain = SAVEMEM.Techzasters.subtract(BI_ONE).multiply(BI_FIVE).multiply(SAVEMEM.Mods).add(SAVEMEM.Unbricked).multiply(SAVEMEM.Romdown);
				SAVEMEM.SCORE = SAVEMEM.SCORE.add(gain);
				StatusText.setText(formatBigInt(SAVEMEM.SCORE));
            }
            autoClickHandler.postDelayed(this, 750);
        }
    };
	private Handler saveHandler = new Handler();
    
    private Runnable saveTask = new Runnable() {
        public void run() {
			android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
			int lastSlot = prefs.getInt("lastslot", 1);
            SaveSys.saveGame(HomeActivity.this, lastSlot);
            saveHandler.postDelayed(this, 5000);
        }
    };
	private void sendMusicAction(String action) {
		Intent intent = new Intent(this, MusicService.class);
		intent.setAction(action);
		startService(intent);
	}
	private void applySkinToZaster() {
		boolean skinApplied = false;

		if (SAVEMEM.SkinPath != null && !SAVEMEM.SkinPath.isEmpty()) {
			File imgFile = new File(SAVEMEM.SkinPath);
			
			if (imgFile.exists()) {
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				if (zasterBtn != null && myBitmap != null) {
					zasterBtn.setImageBitmap(myBitmap);
					skinApplied = true;
				}
			}
		}

		// Fallback: If skin wasn't applied (path invalid or file missing)
		if (!skinApplied && zasterBtn != null) {
			zasterBtn.setImageResource(R.drawable.techzasteri);
		}
	}

	private void showFileSelectionDialog() {
		final String path = "/storage/emulated/0/VukiDev/Tapzaster/CZaster";
		File directory = new File(path);
		
		if (!directory.exists()) directory.mkdirs();

		File[] files = directory.listFiles();
		
		int fileCount = (files == null) ? 0 : files.length;
		final String[] displayNames = new String[fileCount + 1];
		
		displayNames[0] = "Default (Techzaster)";

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				displayNames[i + 1] = files[i].getName();
			}
		}

		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setTitle("Select a Skin");
		builder.setItems(displayNames, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(android.content.DialogInterface dialog, int which) {
				if (which == 0) {
					SAVEMEM.SkinPath = ""; 
				} else {
					SAVEMEM.SkinPath = path + "/" + displayNames[which];
				}
				
				applySkinToZaster();
				
				android.content.SharedPreferences prefs = getSharedPreferences("ZasterPrefs", MODE_PRIVATE);
				SaveSys.saveGame(HomeActivity.this, prefs.getInt("lastslot", 1));
				
				playeffec(4);
			}
		});
		builder.show();
	}
}
