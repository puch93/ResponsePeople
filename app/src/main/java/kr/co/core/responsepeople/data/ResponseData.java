package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class ResponseData {
    private String idx;
    private String nick;
    private String age;
    private String job;
    private String location;
    private String salary;
    private String profile_img;

    private int progress;

    private boolean like;
    private boolean salary_ok;
    private boolean image_ok;

    public ResponseData(String idx, String nick, String age, String job, String location, String salary, String profile_img, int progress, boolean like, boolean salary_ok, boolean image_ok) {
        this.idx = idx;
        this.nick = nick;
        this.age = age;
        this.job = job;
        this.location = location;
        this.salary = salary;
        this.profile_img = profile_img;
        this.progress = progress;
        this.like = like;
        this.salary_ok = salary_ok;
        this.image_ok = image_ok;
    }
}
