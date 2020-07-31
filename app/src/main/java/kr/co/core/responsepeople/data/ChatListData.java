package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class ChatListData {
    private String room_idx;
    private String contents;
    private String send_date;
    private String unread_count;

    private String y_nick;
    private String y_age;
    private String y_profile_img;
    private boolean y_profile_img_ok;
}
