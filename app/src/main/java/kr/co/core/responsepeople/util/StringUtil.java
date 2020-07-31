package kr.co.core.responsepeople.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class StringUtil {
    public static final String TAG = "TEST_HOME";
    public static final String TAG_PUSH = "TEST_PUSH";
    public static final String TAG_PAY = "TEST_PAY";
    public static final String TAG_CHAT = "TEST_CHAT";
    public static final String TAG_RTC = "TEST_RTC";

    public static boolean isNull(String str) {
        if (str == null || str.length() == 0 || str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getRandomString() {
        StringBuilder buffer = new StringBuilder();
        Random random = new Random();

        String[] nums = "1,2,3,4,5,6,7,8".split(",");

        for (int i = 0; i < 8; i++) {
            buffer.append(nums[random.nextInt(nums.length)]);
        }

        Log.i(StringUtil.TAG, "roomId: " + buffer.toString());
        return buffer.toString();
    }

    public static String convertCallTime(long original, String patten) {
        DateFormat df = new SimpleDateFormat(patten, java.util.Locale.getDefault());
        return df.format(original);
    }

    public static String convertDateFormat(String original, String after) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
        sf.setTimeZone(TimeZone.getTimeZone("KST"));
        try {
            Date old_date = sf.parse(original);
            Date after_date = sf.parse(after);

            long calDate = after_date.getTime() - old_date.getTime();
            Log.i(TAG, "calDate: " + calDate);
            Date date = new Date(calDate);
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("mm:ss", java.util.Locale.getDefault());
            return dateFormat1.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String convertTime(String original, String pattern) {
        //아이템별 시간
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
        Date date1 = null;
        try {
            date1 = dateFormat1.parse(original);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern, java.util.Locale.getDefault());
        return dateFormat2.format(date1);
    }

    public static String setNumComma(int price) {
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(price);
    }

    public static String calcAge(String byear) {
        // 현재 연도에서 출생 연도를 뺀다. (2018 - 2000 = 18)
        // 1살을 더한다. (18 + 1 = 19)
        Calendar c = Calendar.getInstance();
//        Log.i(TAG,"year: "+(c.get(Calendar.YEAR)-Integer.parseInt(byear)+1));
        int lastYear = c.get(Calendar.YEAR) - Integer.parseInt(byear) + 1;

        return String.valueOf(lastYear);
    }

    public static String converTime(String original, String pattern) {
        //아이템별 시간
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
        Date date1 = null;
        try {
            date1 = dateFormat1.parse(original);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern, java.util.Locale.getDefault());
        return dateFormat2.format(date1);
    }

    public static String getStr(JSONObject jo, String key) {
        String s = null;
        try {
            if (jo.has(key)) {
                s = jo.getString(key);
                if (s.equalsIgnoreCase("null"))
                    s = "";
            } else {
                s = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static int getInt(JSONObject jo, String key) {
        int s = 0;
        try {
            if (jo.has(key)) {
                s = jo.getInt(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static double getDouble(JSONObject jo, String key) {
        double s = 0.0;
        try {
            if (jo.has(key)) {
                s = jo.getDouble(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void logLargeString(String str) {
        if (str.length() > 1500) {
            Log.i(StringUtil.TAG, str.substring(0, 1500));
            logLargeString(str.substring(1500));
        } else {
            Log.i(StringUtil.TAG, str); // continuation
        }
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context. * @param uri The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
