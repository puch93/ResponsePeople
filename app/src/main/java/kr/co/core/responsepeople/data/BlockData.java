package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class BlockData {
    private String number;
    private String name;
    boolean selected;

    public BlockData(String number, String name, boolean selected) {
        this.number = number;
        this.name = name;
        this.selected = selected;
    }
}
