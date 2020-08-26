package kr.co.core.responsepeople.data;

import lombok.Data;

@Data
public class ImageEditData {
    private String imageUrl;
    private String imageState;

    public ImageEditData(String imageUrl, String imageState) {
        this.imageUrl = imageUrl;
        this.imageState = imageState;
    }
}
