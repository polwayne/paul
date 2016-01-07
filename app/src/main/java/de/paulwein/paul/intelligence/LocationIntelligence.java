package de.paulwein.paul.intelligence;

import android.content.Intent;

public class LocationIntelligence {
	
	private static final String TIME_RECORDING_CHECK_IN = "com.dynamicg.timerecording.CHECK_IN";
	private static final String TIME_RECORDING_CHECK_OUT = "com.dynamicg.timerecording.CHECK_OUT";
	
	public static Intent getWorkIntent(boolean enter){
		Intent workIntent = null;
		if(enter)
			workIntent = new Intent(TIME_RECORDING_CHECK_IN);
		else
			workIntent = new Intent(TIME_RECORDING_CHECK_OUT);
		return workIntent;
	}

}
