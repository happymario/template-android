package com.victoria.bleled.data.remote;

public class ApiException extends Exception {
    public static final int SUCCESS = 0; //성공
    public static final int API_RESULT_ERROR_SYSTEM = 101; //시스템오류
    public static final int API_RESULT_ERROR_DB = 102;  //db연동오류
    public static final int API_RESULT_ERROR_PRIVILEGE = 103;  //권한오류
    public static final int API_RESULT_ERROR_PARAM = 104; //파라미터오류
    public static final int API_RESULT_ERROR_UPLOAD = 105;  //파일upload오류
    public static final int API_RESULT_ERROR_ACCESS_TOKEN = 106;   //accesstoken 오류

    public static final int API_RESULT_ERROR_USER_NO_EXIST = 202; //회원정보존재안함
    public static final int API_RESULT_ERROR_USER_NOT_NORMAL = 203; //회원상태오류
    public static final int API_RESULT_ERROR_USER_BLOCKED = 204; //차단된 유저
    public static final int API_RESULT_ERROR_USER_PAUSED = 205; //일시정지된 유저
    public static final int API_RESULT_ERROR_USER_ID_EXISTS = 210; //아이디 중복
    public static final int API_RESULT_ERROR_USER_NICKNAME_DUPLICATED = 211; //닉네임 중복
    public static final int API_RESULT_ERROR_USER_VERIFY_INFO_DUPLICATED = 212; //실명인증정보 중복
    public static final int API_RESULT_ERROR_USER_SNS_INFO_DUPLICATED = 213; //SNS 로그인정보 중복
    public static final int API_RESULT_ERROR_USER_REGISTER_HOUR_LIMIT = 220; //재가입 시간제한
    public static final int API_RESULT_ERROR_USER_AGE_LIMIT = 230; //나이제한

    public static final int API_RESULT_ERROR_CHARACTER_RETAINED = 310; //이미 보유한 캐랙터
    public static final int API_RESULT_ERROR_BLOCK_WORD = 400; //금지어
    public static final int API_RESULT_ERROR_EACH_BLOCKED = 401; //상대방 차단
    public static final int API_RESULT_ERROR_POINT_NOT_ENOUGH = 450; //포인트 부족

    public static final int API_RESULT_ERROR_CLUB_NO_EXIST = 500; //클럽존재안함
    public static final int API_RESULT_ERROR_CLUB_DELETED = 501; //클럽삭제됨
    public static final int API_RESULT_ERROR_CLUB_MINE_NO_EXIST = 502;  //본인클럽룸 존재안함 <-club/detail에서만
    public static final int API_RESULT_ERROR_CLUB_ALREADY_JOINED = 510; //이미 클럽에 가입됨
    public static final int API_RESULT_ERROR_CLUB_NO_SEAT = 550; //클럽룸이 다 찬 상태
    public static final int API_RESULT_ERROR_CLUB_ALREADY_REQUEST = 551; //이미 클럽룸 가입이 신청됨

    public static final int API_RESULT_NO_EXIST_INAPP_ITEM = 701; //인앱아이템 존재안함
    public static final int API_RESULT_PURCHASE_ERROR = 702; //비정상 결제
    public static final int API_RESULT_PURCHASE_DUPLICATED = 703; //중복 결제

    public static final int ERR_NO_DATA = -1;

    private int code;
    private String msg;
    private String reason;

    public ApiException(int code, String msg, String reason) {
        super(msg);

        this.code = code;
        this.msg = msg;
        this.reason = reason;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
