package com.victoria.bleled.service.fcm;

import android.content.Context;

import com.victoria.bleled.R;
import com.victoria.bleled.data.model.ModelUser;

import java.io.Serializable;
import java.util.Map;

public class PushMessage implements Serializable {
    private String type; // 메시지유형 (text, photo)
    private String content; // 메시지내용
    private String from_user_uid; // 보낸 회원UID
    private String from_nickname; // 보낸 회원 닉네임

    public PushMessage(Map<String, String> data) {
        try {
            type = data.get("type");
            content = data.get("content");

            if (data.containsKey("from_user_uid")) {
                from_user_uid = data.get("from_user_uid");
            }
            if (data.containsKey("from_nickname")) {
                from_nickname = data.get("from_nickname");
            }
        } catch (Exception e) {

        }
    }

    public String getDisplayTitle(Context context) {
        return context.getString(R.string.app_name);
    }

    public String getDisplayMsg(Context context) {
        ModelUser user = new ModelUser(0);
        //user.setUserid(from_user_uid);
        return content;
    }

    public boolean isChatType() {
        EPushType ePushType = EPushType.toEnum(type);
        if (ePushType == EPushType.chat_text || ePushType == EPushType.chat_photo
                || ePushType == EPushType.club_photo || ePushType == EPushType.club_text) {
            return true;
        }

        return false;
    }

    public boolean isClubChatPush() {
        EPushType ePushType = EPushType.toEnum(type);
        if (ePushType == EPushType.club_photo || ePushType == EPushType.club_text
                || ePushType == EPushType.club_boss_force_out) {
            return true;
        }

        return false;
    }

    public boolean isOneChatPush() {
        EPushType ePushType = EPushType.toEnum(type);
        if (ePushType == EPushType.chat_text || ePushType == EPushType.chat_photo
                || ePushType == EPushType.room_out) {
            return true;
        }

        return false;
    }

    public enum EPushType {
        none(""),
        chat_text("text"),
        chat_photo("photo"),
        club_text("club_text"),
        club_photo("club_photo"),
        room_out("room_out"),
        club_boss_invite("club_boss_invite"),
        club_boss_accept("club_boss_accept"),
        club_boss_reject("club_boss_reject"),
        club_boss_force_out("club_boss_force_out"), // 강퇴
        club_boss_remove("club_boss_remove"),       // 폭파
        club_member_req("club_member_req"),
        club_member_accept("club_member_accept"),
        club_member_reject("club_member_reject"),
        club_member_cancel("club_member_cancel"),
        club_boss_vip_cancel("club_boss_vip_cancel"),
        freecharge_point("freecharge_point"),
        admin_text("admin_text"),
        admin_notice("admin_notice"),
        admin_event("admin_event");


        private String value;

        EPushType(String v) {
            this.value = v;
        }

        public static EPushType toEnum(String value) {
            if (value != null) {
                if (value.equals("text")) {
                    return EPushType.chat_text;
                } else if (value.equals("photo")) {
                    return EPushType.chat_photo;
                } else if (value.equals("club_text")) {
                    return EPushType.club_text;
                } else if (value.equals("club_photo")) {
                    return EPushType.club_photo;
                } else if (value.equals("room_out")) {
                    return EPushType.room_out;
                } else if (value.equals("club_boss_invite")) {
                    return EPushType.club_boss_invite;
                } else if (value.equals("club_boss_accept")) {
                    return EPushType.club_boss_accept;
                } else if (value.equals("club_boss_reject")) {
                    return EPushType.club_boss_reject;
                } else if (value.equals("club_boss_force_out")) {
                    return EPushType.club_boss_force_out;
                } else if (value.equals("club_boss_remove")) {
                    return EPushType.club_boss_remove;
                } else if (value.equals("club_member_req")) {
                    return EPushType.club_member_req;
                } else if (value.equals("club_member_accept")) {
                    return EPushType.club_member_accept;
                } else if (value.equals("club_member_reject")) {
                    return EPushType.club_member_reject;
                } else if (value.equals("club_member_cancel")) {
                    return EPushType.club_member_cancel;
                } else if (value.equals("freecharge_point")) {
                    return EPushType.freecharge_point;
                } else if (value.equals("admin_text")) {
                    return EPushType.admin_text;
                } else if (value.equals("admin_notice")) {
                    return EPushType.admin_notice;
                } else if (value.equals("admin_event")) {
                    return EPushType.admin_event;
                }

            }
            return EPushType.none;
        }
    }
}