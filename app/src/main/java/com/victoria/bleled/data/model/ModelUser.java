package com.victoria.bleled.data.model;


public class ModelUser extends BaseModel {
    private String access_token;
    private String id;
    private String name;
    private String reg_time;
    private String profile_url;
    private String profile_url_check;
    private int status;

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
