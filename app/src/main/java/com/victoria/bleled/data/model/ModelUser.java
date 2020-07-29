package com.victoria.bleled.data.model;


import java.util.Calendar;

public class ModelUser extends BaseModel {
    private String name;
    private String birth;
    private String nationalinfo;
    private String access_token; // 액세스토큰
    private String user_uid; // 회원UID
    private String user_id; // 회원아이디
    private String nickname; // 회원닉네임
    private String sex; // 성별 m,f
    private int age; // 나이
    private int point; // 보유포인트

    public static boolean isNormalUser(String user_id) {
        try {
            if (Integer.parseInt(user_id) > 10) { // 10이하가 특수유저임.
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public boolean isClubBot() {
        if (user_uid != null && user_uid.equals("1")) {
            return true;
        }
        return false;
    }

    public boolean isAdmin() {
        if (user_uid != null && user_uid.equals("2")) {
            return true;
        }
        return false;
    }

    public boolean isAdminChat() {
        if (user_uid != null && user_uid.equals("3")) {
            return true;
        }
        return false;
    }

    public void setUserSex(EUserSex userSex) {
        this.sex = userSex.value;
    }

    public int getAgeByBirth() {
        if (birth == null || birth.isEmpty() == true) {
            return 0;
        }

        try {
            int birthYear = Integer.parseInt(birth.substring(0, 4));
            int curYear = Calendar.getInstance().get(Calendar.YEAR);
            int age = curYear - birthYear + 1;
            return age;
        } catch (Exception e) {
            return 0;
        }
    }

    public enum EUserSex {
        none(""),
        man("m"),
        woman("f");

        private String value;

        EUserSex(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public static EUserSex toEnum(String value) {
            if (value != null) {
                if (value.equals("m") == true) {
                    return EUserSex.man;
                } else if (value.equals("f") == true) {
                    return EUserSex.woman;
                }
            }

            return EUserSex.none;
        }
    }

    public enum EUserLoginType {
        normal("normal"),
        kakao("kakao"),
        google("google"),
        facebook("facebook");

        private String value;

        EUserLoginType(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public static EUserLoginType toEnum(String value) {
            if (value != null) {
                if (value.equals("kakao") == true) {
                    return EUserLoginType.kakao;
                } else if (value.equals("google") == true) {
                    return EUserLoginType.google;
                } else if (value.equals("facebook") == true) {
                    return EUserLoginType.facebook;
                }
            }

            return EUserLoginType.normal;
        }
    }
}
