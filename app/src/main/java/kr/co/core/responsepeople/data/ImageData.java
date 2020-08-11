package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class ImageData {
    private String imageUrl;
    private boolean isPass;

    public ImageData(String imageUrl, boolean isPass) {
        this.imageUrl = imageUrl;
        this.isPass = isPass;
    }
}
