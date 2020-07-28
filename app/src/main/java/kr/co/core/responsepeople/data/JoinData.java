package kr.co.core.responsepeople.data;

import java.io.File;
import java.util.ArrayList;

import lombok.Data;

@Data
public class JoinData {
    private String id;
    private String pw;
    private String nick;
    private String gender;
    private String hp;
    private String birth;
    private String location;
    private String height;
    private String edu;
    private String body;
    private String job;
    private String religion;
    private String drink;
    private String smoke;
    private String salary;
    private String charm;
    private String ideal;
    private String interest;
    private String intro;

    // 파일 (이미지, 연봉)
    private ArrayList<String> images;
    private File salary_file;

    public JoinData() {
        images = new ArrayList<>();
    }
}
