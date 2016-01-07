package de.paulwein.paul.intelligence;

import java.util.Calendar;

public class Semantics {
	
	private final static int TYPE_REMIND_IN = 1;
	private final static int TYPE_REMIND_AT = 2;
	
	public static long extractAlarmSchedule(String sentence){
		
		int type = -1;
		String minuten = null;
		String stunden = null;
		
		if(sentence.contains(" in "))
			type = TYPE_REMIND_IN;
		
		if(sentence.contains(" um "))
			type = TYPE_REMIND_AT;
		
		String[] words = sentence.split(" ");
		
		for(int i = 0; i < words.length; i++){
			if(words[i].equals("minuten"))
				minuten = words[i-1];
			else if(words[i].equals("minute")){
				minuten = "1";
			}
			else if(words[i].equals("uhr")){
				stunden = words[i-1];
				if(i+1 < words.length){
					try{
						Integer.parseInt(words[i+1]);
						minuten = words[i+1];
					}catch(NumberFormatException nfe){
						minuten = "0";
					}
				}
				else {
					minuten = "0";
				}
			}
			else if(words[i].equals("stunden")){
				stunden = words[i-1];
			}
			else if(words[i].equals("stunde")){
				stunden = "1";
			}
		}
		
		
		int minutes = -1;
		int hours = -1;
		
		if(minuten != null && !minuten.isEmpty()) {
		try {
			minutes = Integer.parseInt(minuten);
		}catch (NumberFormatException e){
			if(minuten.equalsIgnoreCase("zwei"))
				minutes = 2;
		}
		}
		
		if(stunden != null && !stunden.isEmpty()) {
			hours = Integer.parseInt(stunden);
		}
		
		Calendar cal = Calendar.getInstance();

		switch (type) {
		case TYPE_REMIND_IN:
			if(hours != -1)
				cal.add(Calendar.HOUR_OF_DAY, hours);
			if(minutes != -1)
				cal.add(Calendar.MINUTE, minutes);
			break;
		case TYPE_REMIND_AT:
			if(sentence.contains("morgen"))
				cal.add(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, hours);
			if(minutes != -1)
				cal.set(Calendar.MINUTE, minutes);
			break;
		}
		
		long time = cal.getTimeInMillis();
		
		return time;
	}

}
