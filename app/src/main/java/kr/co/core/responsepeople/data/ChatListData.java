package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class ChatListData {
    private String room_idx;
    private String contents;
    private String send_date;
    private String unread_count;
    private String y_idx;
    private String y_nick;
    private String y_age;
    private String y_profile_img;
    private boolean y_profile_img_ok;

    public ChatListData(String room_idx, String contents, String send_date, String unread_count, String y_idx, String y_nick, String y_age, String y_profile_img, boolean y_profile_img_ok) {
        this.room_idx = room_idx;
        this.contents = contents;
        this.send_date = send_date;
        this.unread_count = unread_count;
        this.y_idx = y_idx;
        this.y_nick = y_nick;
        this.y_age = y_age;
        this.y_profile_img = y_profile_img;
        this.y_profile_img_ok = y_profile_img_ok;
    }
}
