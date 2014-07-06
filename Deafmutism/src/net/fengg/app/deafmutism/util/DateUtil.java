package net.fengg.app.deafmutism.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static final int WEATHER_TODAY = 0; // 今天
	public static final int WEATHER_TOMORROW = 1; // 明天
	public static final int WEATHER_AFTER_TOMORROW = 2; // 后天
    
	/**
	 * 根据给定的日期，获取指定日期的下一天或者下下一天
	 * @param specifiedDay
	 * @param whichDay
	 * @return
	 */
    public static String getDateAfterSpecifiedDay(String specifiedDay, int whichDay){
    	String date = specifiedDay.replace("年", "-").replace("月", "-").replace("日", ""); //2013-11-11
        String tomorrow = getSpecifiedDayAfter(date, whichDay);//2013-11-12
        String[] dates = tomorrow.split("-");
        StringBuffer sb = new StringBuffer();
        sb.append(dates[1]).append("月");
        if (Integer.parseInt(dates[2]) < 10) {
        	sb.append(dates[2].substring(1)).append("日");
		} else {
			sb.append(dates[2]).append("日");
		}
        return sb.toString();
    }

    /**
     * 获得指定日期的下一天或者下下一天
     * 
     * @param specifiedDay
     * @return
     */
    private static String getSpecifiedDayAfter(String specifiedDay, int whichDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + whichDay);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
                .format(c.getTime());
        return dayAfter;
    }
    
    /**
     * 得到当前给定星期的下一天是星期几
     * @param week
     * @return
     */
	public static String getWeekAfter(String week) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		int index = 0;
		for (int i = 0; i < weekDays.length; i++) {
			if (weekDays[i].equals(week)) {
				index = i + 1;
				break;
			}
		}
		if (index == weekDays.length) {
			index = 0;
		}
		return weekDays[index];
	}
}