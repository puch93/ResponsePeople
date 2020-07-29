package kr.co.core.responsepeople.server.netUtil;

public class NetUrls {
    public static final String DOMAIN = "http://qnating.adamstore.co.kr/lib/control.siso";
    public static final String DOMAIN_ORIGIN = "http://qnating.adamstore.co.kr";

    public static final String TERM = "setMobileDomain"; // 약관 가져오기
    public static final String AUTH_REQUEST = "setCustomerMemberOTP"; // 휴대폰 인증번호 요청
    public static final String AUTH_CONFIRM = "setOTPMemberCheck"; // 휴대폰 인증번호 확인
    public static final String JOIN = "setMobileMemberRegi"; // 회원가입
    public static final String LOGIN = "setMobileLoginCk"; // 로그인
    public static final String ONLINE_MEMBER = "setJoinMemberList"; // 로그인



    public static final String QUESTION_MY_LIST = "getQuestionList"; // 내 질문지 리스트 가져오기
    public static final String DELETE = "delQuestion"; // 내 질문지 리스트 가져오기
}