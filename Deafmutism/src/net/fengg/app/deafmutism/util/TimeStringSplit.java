package net.fengg.app.deafmutism.util;

import java.text.SimpleDateFormat;


public class TimeStringSplit {
	
	
	public static String timeStringSqlit(String timeString){
		    SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(Long.parseLong(timeString)*1000);
	}
	
}
