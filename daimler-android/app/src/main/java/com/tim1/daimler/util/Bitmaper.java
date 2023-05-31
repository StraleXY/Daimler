package com.tim1.daimler.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Bitmaper {
    public static Bitmap toBitmap(String image){
        try {
            String imageDataBytes = image.substring(image.indexOf(",") + 1);
            return BitmapFactory.decodeStream(new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT)));
        }
        catch (Exception e) {
            return null;
        }
    }
}
