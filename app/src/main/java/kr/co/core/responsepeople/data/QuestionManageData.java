package kr.co.core.responsepeople.data;

import java.util.ArrayList;

import lombok.Data;

@Data
public class QuestionManageData {
    private String idx;
    private String question;
    private String answer;
    private String[] sheet;

    private boolean select = false;

    public QuestionManageData(String idx, String question, String answer, String[] sheet) {
        this.idx = idx;
        this.question = question;
        this.answer = answer;
        this.sheet = sheet;
    }
}
