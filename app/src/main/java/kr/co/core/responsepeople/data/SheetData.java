package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class SheetData {
    private String data;
    private boolean checked;

    public SheetData(String data, boolean checked) {
        this.data = data;
        this.checked = checked;
    }
}
