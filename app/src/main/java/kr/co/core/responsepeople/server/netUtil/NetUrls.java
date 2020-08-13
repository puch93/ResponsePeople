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
    public static final String REALTIME_LIST = "setTestMemberList"; // 홈탭 - 실시간 평가회원 리스트 가져오기
    public static final String PREFER_LIST = "getMatchingMember"; // 추천탭 - 선호설정에 해당하는 인원 가져오기
    public static final String HIGH_SCORE = "setHighScoreList"; // 나에게 높은 점수를 준 이성 가져오기
    public static final String MY_INFO = "getMyProfile"; // 내 정보 가져오기
    public static final String LOGOUT = "setMobilelogout"; // 내 정보 가져오기
    public static final String NEAR_LIST = "setDistanceMemberList"; // 내 위치반경 10km 회원 가져오기
    public static final String QUESTION_RESPONSE_LIST = "getAnswerMember"; // 내 질문 응답한 이성 리스트
    public static final String LIKE = "setFollow"; // 내 질문 응답한 이성 리스트
    public static final String CHAT_PUSH = "setPush"; // 채팅 푸시
    public static final String CHAT_UPLOAD_IMAGE = "setChattingImgUpload"; // 채팅 이미지 업로드
    public static final String CHAT_LIST = "setRoomList"; // 채팅방 리스트
    public static final String PAYMENT_ITEM = "setMobilePaymentlist"; // 결제 아이템 리스트
    public static final String PAYMENT = "setMobilePayment"; // 결제
    public static final String EVALUATION = "setPreference"; // 상대편 평가하기
    public static final String LIKED = "getFollowMemberList"; // 나를 찜한 사람 들고오기
    public static final String PASSWORD_CHANGE = "setPass"; // 비밀번호 변경
    public static final String NOTICE = "listBoardSend"; // 공지사항
    public static final String QUESTION_RECEIVED = "receiveQuestion"; // 받은 질문지 리스트
    public static final String QUESTION_ANSWER_LIST = "getAnswerQuestion"; // 응답한 리스트 가져오기
    public static final String QUESTION_ANSWERED = "getAnswerQuestion"; // 응답한 질문지 리스트
    public static final String QUESTION_SEND = "sendQuestion"; // 질문지 보내기
    public static final String QUESTION_ANSWER = "setAnswerQuestion"; // 응답하기
    public static final String QUESTION_ANSWER_CHK = "sendQuestionChk"; // 응답하기 확정
    public static final String FIND_ID = "findMobileUserID"; // 아이디 확인
    public static final String BLOCK = "setMobileMemberBanRegi"; // 지인 차단
    public static final String BLOCK_LIST = "setMobileMemberBanlist"; // 지인 차단 리스트 가져오기
    public static final String LIKE_OTHER_LIST = "getMyFollowMemberList"; // 내가 찜한 회원 리스트

    public static final String QUESTION_MY_LIST = "getQuestionList"; // 내 질문지 리스트 가져오기
    public static final String QUESTION_DELETE = "delQuestion"; // 질문지 삭제하기
    public static final String QUESTION_SAVE = "setQuestionSave"; // 질문지 추가하기
}