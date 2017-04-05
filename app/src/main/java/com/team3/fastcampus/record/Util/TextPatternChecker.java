package com.team3.fastcampus.record.Util;

import java.util.regex.Pattern;

/**
 * Created by tokijh on 2017. 4. 5..
 */

public class TextPatternChecker {

    public static boolean email(String email) {
        final Pattern emailPattern = Pattern.compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return emailPattern.matcher(email).matches();
    }

    /**
     * 비밀번호 대문자, 소문자, 숫자, 특수문자, 6자 이상 20자 이하
     * @param password
     * @return
     */
    public static boolean password(String password) {
        final Pattern passwordPattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})");
        return passwordPattern.matcher(password).matches();
    }
}
