package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class MemberData {
    private String idx;
    private String nick;
    private String age;
    private String job;
    private String location;
    private String salary;
    private String profile_img;

    private boolean like;
    private boolean salary_ok;

    public MemberData(String idx, String nick, String age, String job, String location, String salary, String profile_img, boolean like, boolean salary_ok) {
        this.idx = idx;
        this.nick = nick;
        this.age = age;
        this.job = job;
        this.location = location;
        this.salary = salary;
        this.profile_img = profile_img;
        this.like = like;
        this.salary_ok = salary_ok;
    }
}
