package com.mshlab.firebasestorageapi;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.InputStream;

public class Helper {


    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }


    public static long uriFileSize(Uri uri, Context context) {
        long dataSize = 0;
        String scheme = uri.getScheme();
        if (scheme != null) {
            if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                try {
                    InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                    if (fileInputStream != null) {
                        dataSize = fileInputStream.available();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
                String path = uri.getPath();
                File file = null;
                try {
                    file = new File(path);
                    dataSize = file.length();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dataSize;
    }
}
