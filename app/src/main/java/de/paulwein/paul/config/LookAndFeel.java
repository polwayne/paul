package de.paulwein.paul.config;

import android.content.Context;
import android.graphics.Typeface;

public class LookAndFeel implements ILookAndFeel{

	private static final String FONT_DIR = "fonts/";
	private static final String TF_HEADLINE = "goodmorningafternoon.ttf";
	private static final String TF_DEFAULT = "Jellyka_Estrya_Handwriting.ttf";
	private static final String TF_OTHER = "birthofahero.ttf";
	
	private Context mContext;
	
	public LookAndFeel(Context context){
		mContext = context;
	}
	
	
	@Override
	public Typeface getHeadLineTypeFace() {
		return Typeface.createFromAsset(mContext.getAssets(),FONT_DIR + TF_HEADLINE);
	}

	@Override
	public Typeface getDefaultTypeFace() {
		return Typeface.createFromAsset(mContext.getAssets(),FONT_DIR + TF_DEFAULT);
	}


	@Override
	public Typeface getOtherTypeFace() {
		return Typeface.createFromAsset(mContext.getAssets(),FONT_DIR + TF_OTHER);
	}
}
