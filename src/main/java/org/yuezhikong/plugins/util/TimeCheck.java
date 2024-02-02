package org.yuezhikong.plugins.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class TimeCheck {
    public static boolean isWithinWorkingHours() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDayOfWeek = now.getDayOfWeek();
        // 确保是周一至周五
        if (currentDayOfWeek == DayOfWeek.MONDAY || currentDayOfWeek == DayOfWeek.TUESDAY ||
                currentDayOfWeek == DayOfWeek.WEDNESDAY || currentDayOfWeek == DayOfWeek.THURSDAY || currentDayOfWeek == DayOfWeek.FRIDAY) {
            int hour = now.getHour();
            int minute = now.getMinute();
            // 检查是否在上午9:30到11:30之间
            if (hour >= 9 && hour < 11) {
                return minute >= 30;
            }
            // 检查是否在下午1:00到3:00之间
            if (hour == 13 || (hour == 12 && minute >= 0)) { // 注意：13点是下午1点，12点可能需要包括整点
                return minute < 60; // 下午时段默认包含所有分钟
            }
        }
        return false;
    }
}

