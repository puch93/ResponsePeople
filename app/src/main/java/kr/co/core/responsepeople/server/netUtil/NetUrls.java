package kr.co.core.responsepeople.server.netUtil;

public class NetUrls {
    public static final String DOMAIN = "http://qnating.adamstore.co.kr/lib/control.siso";
    public static final String DOMAIN_ORIGIN = "http://qnating.adamstore.co.kr";

    public static final String TERM = "setMobileDomain"; // 약관 가져오기
    public static final String AUTH_REQUEST = "setCustomerMemberOTP"; // 휴대폰 인증번호 요청
    public static final String AUTH_CONFIRM = "setOTPMemberCheck"; // 휴대폰 인증번호 확인
    public static final String JOIN = "setMobileMemberRegi"; // 회원가입
    public static final String LOGIN = "setMobileLoginCk"; // 로그인
    public static final String ONLINE_MEMBER = "setJoinMemberList"; // 접속중 회원 리스트
    public static final String PREFER = "setPreferencesMember"; // 선호설정
    public static final String EVALUATION_COMPLETE = "setAssessComplete"; // 평가완료
    public static final String CHECK_PROFILE_READ = "getMobileProfileViewChk"; // 프로필 열람여부 확인
    public static final String CHECK_1DAY_USED = "getFreeProfileViewchk"; // 1일무료 프로필 사용여부
    public static final String VIEW_PROFILE = "setMobileProfileView"; // 프로필 보기
    public static final String VIEW_PROFILE_ADMIN = "setMobileProfileViewAmdin"; // 프로필 보기
    public static final String CHECK_ROOM = "getChatRoomNumber"; // 프로필 보기
    public static final String REQUEST_CHECK = "setRequestProfile"; // 가입시 프로필사진 재검수요청
    public static final String EDIT_PROFILE = "setMobileaProfileEdit"; // 내 정보 수정
    public static final String RECOMMEND_LIST = "getRecommendList"; // 홈탭 - 추천회원 리스트 가져오기

    public static final String QUESTION_MY_LIST = "getQuestionList"; // 내 질문지 리스트 가져오기
    public static final String DELETE = "delQuestion"; // 내 질문지 리스트 가져오기
}
