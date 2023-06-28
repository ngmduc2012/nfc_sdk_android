package cist.cmc.nfc.sdk.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import cist.cmc.nfc.sdk.core.chip.lds.AbstractImageInfo;


public class ImageUtils {

    public static Bitmap getBitmap(AbstractImageInfo imageInfo) {
        try {
            int imageLength = imageInfo.getImageLength();
            DataInputStream dataInputStream = new DataInputStream(imageInfo.getImageInputStream());
            byte[] buffer = new byte[imageLength];
            dataInputStream.readFully(buffer, 0, imageLength);
            InputStream inputStream = new ByteArrayInputStream(buffer, 0, imageLength);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
