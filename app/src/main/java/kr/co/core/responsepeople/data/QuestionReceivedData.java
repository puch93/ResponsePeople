package kr.co.core.responsepeople.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class QuestionReceivedData implements Serializable {
    private String q_idx;
    private String q_m_idx;
    private String q_question;
    private String q_sheet;
    private String q_answer;
    private String q_regdate;
    private String qh_idx;
    private String qh_answer;
    private String qh_regdate;

    private String answer;

    private String nick;
    private String age;
    private String profile_img;
    private boolean profile_img_ck;

    public QuestionReceivedData(String q_idx, String q_m_idx, String q_question, String q_sheet, String q_answer, String q_regdate, String qh_idx, String qh_answer, String qh_regdate, String nick, String age, String profile_img, boolean profile_img_ck) {
        this.q_idx = q_idx;
        this.q_m_idx = q_m_idx;
        this.q_question = q_question;
        this.q_sheet = q_sheet;
        this.q_answer = q_answer;
        this.q_regdate = q_regdate;
        this.qh_idx = qh_idx;
        this.qh_answer = qh_answer;
        this.qh_regdate = qh_regdate;
        this.nick = nick;
        this.age = age;
        this.profile_img = profile_img;
        this.profile_img_ck = profile_img_ck;
    }
}
