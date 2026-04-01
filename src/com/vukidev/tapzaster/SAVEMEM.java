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

public class SAVEMEM {
	private static final BigInteger BI_ZERO = BigInteger.valueOf(0);
	private static final BigInteger BI_ONE = BigInteger.valueOf(1);
	private static final BigInteger BI_FIVE = BigInteger.valueOf(5);
	private static final BigInteger BI_TEN = BigInteger.valueOf(10);

	public static BigInteger SCORE = BI_ZERO;
	public static BigInteger phones = BI_ZERO;
	public static BigInteger phonesupg = BI_ZERO;
	public static BigInteger Techzasters = BI_ONE;
	public static BigInteger Mods = BI_ONE;
	public static BigInteger Unbricked = BI_ONE;
	public static BigInteger Romdown = BI_ONE;
	
	public static int SuperUpgradeFinal = 20;
	
	public static int hasSDCard = 0;
	public static int hasTWRP = 0;
	public static int hasROMs = 0;
	public static int hasFasInt = 0;
	public static int hasPhonePar = 0;

	public static BigInteger costSD = new BigInteger("2500");
	public static BigInteger costTWRP = new BigInteger("10500");
	public static BigInteger costROMs = new BigInteger("125500");
	public static BigInteger costFasInt = new BigInteger("2255000");
	public static BigInteger costPhonePar = new BigInteger("3556410");
}
