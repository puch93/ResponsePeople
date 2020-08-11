package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class NoticeData {
    private String idx;
    private String title;
    private String date;
    private String detail;
    private boolean select;

    public NoticeData(String idx, String title, String date, String detail) {
        this.idx = idx;
        this.title = title;
        this.date = date;
        this.detail = detail;
    }
}
