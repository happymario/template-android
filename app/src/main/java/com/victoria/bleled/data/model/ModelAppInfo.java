package com.victoria.bleled.data.model;

public class ModelAppInfo extends BaseModel {
    private String version_name; // 버전명
    private String inapp_item; // 인앱아이템 목록 (인앱아이템아이디: 구매시 충전금액: 구매버튼에 표시할 문자열)
    private String landing_url; // 업데이트시 랜딩 URL
    private String policy_url; // 개인정보처리방침URL
    private String license_url; // 이용약관URL
    private String ad_image_url; // 광고 이미지URL 빈 문자열인 경우 광고팝업 표시안함
    private String ad_target_url; // 광고 타겟URL 빈문자열인 경우 팝업이미지 클릭시 이동하지 않음
    private String location_url; // 위치기반서비스 이용약관 URL
}
