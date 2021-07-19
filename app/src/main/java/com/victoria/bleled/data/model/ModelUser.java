package com.victoria.bleled.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_user")
public class ModelUser extends BaseModel {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "access_token")
    public String access_token;

    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "first_name")
    public String first_name;

    @ColumnInfo(name = "last_name")
    public String last_name;

    @Ignore
    private String reg_time;

    @Ignore
    private String profile_url;

    @Ignore
    private String profile_url_check;

    @Ignore
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
