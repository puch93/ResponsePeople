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
}
