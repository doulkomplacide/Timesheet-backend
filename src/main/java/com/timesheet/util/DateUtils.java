package com.timesheet.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateUtils {
    
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }
    
    public static LocalDate getStartOfWeek(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        return date.with(weekFields.dayOfWeek(), 1);
    }
    
    public static LocalDate getEndOfWeek(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        return date.with(weekFields.dayOfWeek(), 7);
    }
    
    public static int getWeekNumber(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        return date.get(weekFields.weekOfWeekBasedYear());
    }
    
    public static int getYearOfWeek(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        return date.get(weekFields.weekBasedYear());
    }
}