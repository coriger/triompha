package com.triompha.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeDistanceUtil {

    public static final String getDistance(long d) {
        String distance = "";
        long now = System.currentTimeMillis();
        /*if (d > now) {
            return "你提供的时间错误！";
        }*/
        long dis = now - d;

        long ONESECOND = 1000;
        long ONEMINUTE = ONESECOND * 60;
        long ONEHOUR = ONEMINUTE * 60;
        long ONEDAY = ONEHOUR * 24;
        long ONEWEEK = ONEDAY * 7;
        long ONEMONTH = ONEDAY * 30;
        long ONEYEAR = ONEMONTH * 12;

        if (dis < 2000) {
            distance = "刚刚";
        } else if (dis / ONEMINUTE < 1) {
            distance = String.valueOf(dis / ONESECOND) + "秒前";
        } else if (dis / ONEHOUR < 1) {
            distance = String.valueOf(dis / ONEMINUTE) + "分钟前";
        } else if (dis / ONEDAY < 1) {
            distance = String.valueOf(dis / ONEHOUR) + "小时前";
        } else if (dis / ONEWEEK < 1) {
            distance = String.valueOf(dis / ONEDAY) + "天前";
        } else if (dis / ONEMONTH < 1) {
            distance = String.valueOf(dis / ONEWEEK) + "周前";
        } else if (dis / ONEMONTH < 2) {
            distance = String.valueOf(dis / ONEMONTH) + "月前";
        } else {
            distance = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(d));
        }
        return distance;
    }

    public static final String getRemaindNote(long d) {
        String distance = "";
        long now = System.currentTimeMillis();

        long dis = d - now;

        long ONESECOND = 1000;
        long ONEMINUTE = ONESECOND * 60;
        long ONEHOUR = ONEMINUTE * 60;
        long ONEDAY = ONEHOUR * 24;
        long ONEWEEK = ONEDAY * 7;
        long ONEMONTH = ONEDAY * 30;
        long ONEYEAR = ONEMONTH * 12;

        if (dis / ONEMINUTE < 1) {
            distance = String.valueOf(dis / ONESECOND) + "秒";
        } else if (dis / ONEHOUR < 1) {
            distance = String.valueOf(dis / ONEMINUTE) + "分钟";
        } else if (dis / ONEDAY < 1) {
            distance = String.valueOf(dis / ONEHOUR) + "小时";
        } else if (dis / ONEWEEK < 1) {
            distance = String.valueOf(dis / ONEDAY) + "天";
        } else if (dis / ONEMONTH < 1) {
            distance = String.valueOf(dis / ONEWEEK) + "周";
        } else if (dis / ONEMONTH < 12) {
            distance = String.valueOf(dis / ONEMONTH) + "月";
        } else if (dis / ONEYEAR < 2) {
            distance = String.valueOf(dis / ONEYEAR) + "年";
        }
        return distance;
    }

    public static final String getTimeFormat(Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return sdf.format(c.getTime());
    }

    /**
     * 根据时长（秒） 计算
     * @param s
     * @return
     */
    public static final String getRemaindSeconds(int s) {
		String distance = "";
		int hour = 0 , min = 0 , last = 0;
		if (s >= 0 && s < 60)
			distance = s + "秒";
		else if (s >= 60 && s < 3600) {
			 min = s / 60;
			distance = min + "分";
			 last = s - min * 60;
			if (last > 0)
				distance += last + "秒";
		} else if (s >= 3660) {
			 hour = s / 3600;
			distance = hour + "小时";
			 min = (s - hour * 3600) / 60;
			distance += min + "分";
			last = s - hour * 3600 - min * 60;
			if (last > 0)
				distance += last + "秒";
		}
    	return distance;
    }
    
    
    public static void main(String[] args) {
        Long d = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(d);
        c.add(Calendar.DAY_OF_MONTH, 23);
        // c.add(Calendar.HOUR_OF_DAY, -1);
        d = c.getTimeInMillis();

        System.out.println(TimeDistanceUtil.getRemaindSeconds(1));
        System.out.println(TimeDistanceUtil.getRemaindSeconds(279));
        System.out.println(TimeDistanceUtil.getRemaindSeconds(4263));
        //System.out.println(TimeDistanceUtil.getRemaindNote(d));
    }

}
