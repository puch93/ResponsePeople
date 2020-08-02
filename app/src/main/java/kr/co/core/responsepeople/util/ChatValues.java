package kr.co.core.responsepeople.util;

public class ChatValues {
    public static final String SOCKET_URL = "http://qnating.adamstore.co.kr:1457";

    public static final String TYPE = "type";
    public static final String SITEIDX = "site_idx";
    public static final String ROOMIDX = "room_idx";
    public static final String TALKER = "user_idx";
    public static final String MSG_TYPE = "msg_type";
    public static final String MSG = "c_msg";
    public static final String REAL_MEMBER = "read_user_idx";

    public static final String SEND_MSG = "setChatSave"; // 채팅하기
    public static final String CHATTING_HISTORY = "setRoomList"; // 채팅방 리스트&읽지않은 메세지갯수
    public static final String CHATTING_READ = "readadd"; // 채팅 읽음처리
    public static final String LEAVE = "tempQuit"; // 채팅방 나가기

    //채팅타입
    public static final String MSG_DATELINE = "dateline";
}
