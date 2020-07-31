package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class ChatData {
    private String user_idx;

    private String data_type;
    private String send_time;
    private String date_line;
    private String contents;

    private boolean read;

    public ChatData(String user_idx, String data_type, String send_time, String date_line, String contents, boolean read) {
        this.user_idx = user_idx;
        this.data_type = data_type;
        this.send_time = send_time;
        this.date_line = date_line;
        this.contents = contents;
        this.read = read;
    }

    public ChatData(String date_line, String data_type) {
        this.date_line = date_line;
        this.data_type = data_type;
    }
}
