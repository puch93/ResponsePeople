package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class EtcData {
    private String contents;
    private boolean selected;

    public EtcData(String contents, boolean selected) {
        this.contents = contents;
        this.selected = selected;
    }
}
