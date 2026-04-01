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

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class SaveSys {
	private static String formatBigInt(BigInteger value) {
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
	public static void saveGame(Activity activity, int slot) {
		android.content.SharedPreferences prefs = activity.getSharedPreferences("ZasterPrefs", activity.MODE_PRIVATE);
		android.content.SharedPreferences.Editor editor = prefs.edit();

		// Use the slot number in the key name
		editor.putString("score_slot" + slot, SAVEMEM.SCORE.toString());
		editor.putString("phoneupg_slot" + slot, SAVEMEM.phonesupg.toString());
		editor.putString("techs_slot" + slot, SAVEMEM.Techzasters.toString());
		editor.putString("mods_slot" + slot, SAVEMEM.Mods.toString());
		editor.putString("unbrick_slot" + slot, SAVEMEM.Unbricked.toString());
		editor.putString("romdown_slot" + slot, SAVEMEM.Romdown.toString());
		editor.putInt("sdcard_slot" + slot, SAVEMEM.hasSDCard);
		editor.putInt("twrp_slot" + slot, SAVEMEM.hasTWRP);
		editor.putInt("roms_slot" + slot, SAVEMEM.hasROMs);
		editor.putInt("fasint_slot" + slot, SAVEMEM.hasFasInt);
		editor.putInt("phonepar_slot" + slot, SAVEMEM.hasPhonePar);
		
		// Remember which slot was used last for auto-loading
		editor.putInt("lastslot", slot);
		
		editor.commit(); 
	}
	public static void loadGame(Activity activity, android.widget.TextView StatusText) {
		android.content.SharedPreferences prefs = activity.getSharedPreferences("ZasterPrefs", activity.MODE_PRIVATE);
		
		int slot = prefs.getInt("lastslot", 1);
		
		String savedScore = prefs.getString("score_slot" + slot, "0");
		String savedPhones = prefs.getString("phoneupg_slot" + slot, "0");
		String savedTech = prefs.getString("techs_slot" + slot, "1");
		String savedMods = prefs.getString("mods_slot" + slot, "1");
		SAVEMEM.Unbricked = new java.math.BigInteger(prefs.getString("unbrick_slot" + slot, "1"));
		SAVEMEM.Romdown = new java.math.BigInteger(prefs.getString("romdown_slot" + slot, "1"));
		SAVEMEM.hasSDCard = prefs.getInt("sdcard_slot" + slot, 0);
		SAVEMEM.hasTWRP = prefs.getInt("twrp_slot" + slot, 0);
		SAVEMEM.hasROMs = prefs.getInt("roms_slot" + slot, 0);
		SAVEMEM.hasFasInt = prefs.getInt("fasint_slot" + slot, 0);
		SAVEMEM.hasPhonePar = prefs.getInt("phonepar_slot" + slot, 0);
		
		int savedFINALMULTINT = prefs.getInt("MULTFIN_slot" + slot, 20);
		//String saved = prefs.getString("_slot" + slot, "1");

		SAVEMEM.SCORE = new java.math.BigInteger(savedScore);
		SAVEMEM.phonesupg = new java.math.BigInteger(savedPhones);
		SAVEMEM.Techzasters = new java.math.BigInteger(savedTech);
		SAVEMEM.Mods = new java.math.BigInteger(savedMods);
		
		SAVEMEM.phones = SAVEMEM.phonesupg; 
		if (SAVEMEM.hasPhonePar == 1) {
			SAVEMEM.SuperUpgradeFinal = 2000;
			
		} else if (SAVEMEM.hasSDCard == 1) {
			SAVEMEM.SuperUpgradeFinal = 200;
		}
		else {
			SAVEMEM.SuperUpgradeFinal = 20;
		}
		if (StatusText != null) {
			StatusText.setText(formatBigInt(SAVEMEM.SCORE));
		}
	}
    public static void loadFromSlot(Activity activity, String slotName) {
		
	}
}
