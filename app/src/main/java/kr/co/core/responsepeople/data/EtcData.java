package kr.co.core.responsepeople.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class EtcData implements Serializable {
    private String contents;
    private boolean selected;

    public EtcData(String contents, boolean selected) {
        this.contents = contents;
        this.selected = selected;
    }
}
