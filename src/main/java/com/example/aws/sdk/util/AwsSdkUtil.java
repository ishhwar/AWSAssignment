package com.example.aws.sdk.util;

import java.time.LocalDateTime;

public class AwsSdkUtil {
    public static final String IN_PROGRESS = "IN_PROGRESS" ;
    public static final String SUCCESS ="SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }
}
