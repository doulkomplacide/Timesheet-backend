package com.timesheet.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]{10,14}$"
    );
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidHours(Double hours) {
        return hours != null && hours > 0 && hours <= 24;
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        return start != null && end != null && !end.isBefore(start);
    }
}