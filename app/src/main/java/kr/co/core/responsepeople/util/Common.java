package kr.co.core.responsepeople.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.responsepeople.BuildConfig;
import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BaseAct;
import kr.co.core.responsepeople.server.netUtil.NetUrls;

public class Common {
    public static final int PICK_DIALOG = 101;
    public static final int PHOTO_TAKE = 102;
    public static final int PHOTO_GALLERY = 103;
    public static final int PHOTO_CROP = 104;


    /* toast setting */
    public static void showToast(final Activity act, final String msg) {
        if (null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showToastLong(final Activity act, final String msg) {
        if (null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void showToastNetwork(final Activity act) {
        if (null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, act.getString(R.string.toast_network), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showToastDevelop(final Activity act) {
        if (null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, act.getString(R.string.toast_develop), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface OnAlertAfter {
        void onAfterOk();

        void onAfterCancel();
    }

    public static void showAlert(Activity act, String title, String contents, final OnAlertAfter onAlertAfter) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(contents);

        // ok
        alertDialog.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onAlertAfter.onAfterOk();
                        dialog.cancel();
                    }
                });
        // cancel
        alertDialog.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onAlertAfter.onAfterCancel();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public static String calTier(float score) {
        if (score >= 1 && score <= 4) {
            return "브론즈";
        } else if (score > 4 && score <= 6) {
            return "실버";
        } else if (score > 6 && score <= 8) {
            return "골드";
        } else if (score > 8) {
            return "다이아";
        } else {
            return "브론즈";
        }
    }

    public static String calTierMessage(float score) {
        int result = (int) (100 - (score / 10) * 100);
        if(result == 100) {
            return String.valueOf(result) + "%";
        } else {
            return String.valueOf(result) + "% 이상";
        }
    }

    public static int calTierImage(float score) {
        if (score >= 1 && score <= 4) {
            return R.drawable.icon_b_bronz;
        } else if (score > 4 && score <= 6) {
            return R.drawable.icon_b_silver;
        } else if (score > 6 && score <= 8) {
            return R.drawable.icon_b_gold;
        } else if (score > 8) {
            return R.drawable.icon_b_diamond;
        } else {
            return R.drawable.icon_b_bronz;
        }
    }

    public static void processProfileImageRec(Context ctx, ImageView imageView, String profile_img, boolean isPass, int radius, int sampling) {
        if (StringUtil.isNull(profile_img)) {
            // 사진 값이 없을 때
            Glide.with(ctx)
//                    .load(AppPreference.getProfilePref(ctx, AppPreference.PREF_GENDER).equalsIgnoreCase("M") ? R.drawable.noimg_female : R.drawable.noimg_female)
                    .load(R.drawable.noimg_sexless)
                    .into(imageView);
        } else {
            // 사진 값이 있을 때
            if (!isPass) {
                // 검수중일때
                Glide.with(ctx)
                        .load(NetUrls.DOMAIN_ORIGIN + profile_img)
                        .transform(new BlurTransformation(radius, sampling), new CenterCrop())
                        .into(imageView);
            } else {
                //검수완료일때
                Glide.with(ctx)
                        .load(NetUrls.DOMAIN_ORIGIN + profile_img)
                        .into(imageView);
            }
        }
    }


    public static void processProfileImageCircle(Context ctx, ImageView imageView, String profile_img, boolean isPass, int radius, int sampling) {
        if (StringUtil.isNull(profile_img)) {
            // 사진 값이 없을 때
            Glide.with(ctx)
//                    .load(AppPreference.getProfilePref(ctx, AppPreference.PREF_GENDER).equalsIgnoreCase("M") ? R.drawable.noimg_female : R.drawable.noimg_female)
                    .load(R.drawable.noimg_sexless)
                    .transform(new CircleCrop())
                    .into(imageView);
        } else {
            // 사진 값이 있을 때
            if (!isPass) {
                // 검수중일때
                Glide.with(ctx)
                        .load(NetUrls.DOMAIN_ORIGIN + profile_img)
                        .transform(new BlurTransformation(radius, sampling), new CircleCrop(), new CenterCrop())
                        .into(imageView);
            } else {
                //검수완료일때
                Glide.with(ctx)
                        .load(NetUrls.DOMAIN_ORIGIN + profile_img)
                        .transform(new CircleCrop())
                        .into(imageView);
            }
        }
    }




    public static boolean isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg);
    }

    public static String decodeEmoji(String message) {
        String myString = null;
        try {
            return URLDecoder.decode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    public static boolean checkCellnum(String cellnum) {
        cellnum = PhoneNumberUtils.formatNumber(cellnum);

        boolean returnValue = false;
        try {
            String regex = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(cellnum);
            if (m.matches()) {
                returnValue = true;
            }

            if (returnValue && cellnum != null
                    && cellnum.length() > 0
                    && cellnum.startsWith("010")) {
                cellnum = cellnum.replace("-", "");
                if (cellnum.length() < 10) {
                    returnValue = false;
                }
            }
            return returnValue;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDeviceId(Context context) {
        String newId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i(StringUtil.TAG, "Q이상");
            newId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            Log.i(StringUtil.TAG, "Q미만");
            newId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

            if (StringUtil.isNull(newId)) {
                newId = "35" +
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                        Build.USER.length() % 10;
            }
        }

        return newId;
    }


    public static String convertTimeSimpleLong(long original) {
        Date date = new Date(original);
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("m:ss", java.util.Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String convertTimeSimpleLInt(int original) {
        Date date = new Date(original);
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("m:ss", java.util.Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String convertTime(String original) {
        //아이템별 시간
        String time1 = original;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
        Date date1 = null;
        try {
            date1 = dateFormat1.parse(time1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("a hh:mm", java.util.Locale.getDefault());
        String time2 = dateFormat2.format(date1);
        return time2;
    }

    public static boolean isAppTopRun(Context ctx, String baseClassName) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
        if (info == null || info.size() == 0) {
            return false;
        }
        if (info.get(0).baseActivity.getClassName().equals(baseClassName)) {
            return true;
        } else {
            return false;
        }
    }

    private static class TIME_MAXIMUM {
        static final int SEC = 60;
        static final int MIN = 60;
        static final int HOUR = 24;
        static final int DAY = 30;
        static final int MONTH = 12;
    }

    public static String formatImeString(Date tempDate, Activity act) {
        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;
        if (diffTime < 0) {
            msg = "0" + act.getString(R.string.ago_seconds);
        } else if (diffTime < TIME_MAXIMUM.SEC) {
            msg = diffTime + act.getString(R.string.ago_seconds);
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + act.getString(R.string.ago_minutes);
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + act.getString(R.string.ago_hours);
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + act.getString(R.string.ago_days);
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + act.getString(R.string.ago_months);
        } else {
            msg = (diffTime) + act.getString(R.string.ago_years);
        }

        return msg;
    }

    public static File downloadImage(String imgUrl) {
        Bitmap img = null;
        File f = null;
        Log.e(StringUtil.TAG, "imgUrl: " + imgUrl);

        try {
            f = createImageFile();
            URL url = new URL(imgUrl);
            URLConnection conn = url.openConnection();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            img = BitmapFactory.decodeStream(bis);

            bis.close();

            FileOutputStream out = new FileOutputStream(f);
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            img.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;
    }

    public static void getAlbum(Fragment frag, Activity act, int request_code) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        if (frag != null)
            frag.startActivityForResult(intent, request_code);
        else {
            act.startActivityForResult(intent, request_code);
        }
    }

    public static void takePhoto(Fragment frag, Activity act, int request_code, OnPhotoAfterAction listener) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri;
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Common.showToast(act, "이미지 처리 오류! 다시 시도해주세요.");
            e.printStackTrace();
        }

        if (photoFile != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(act,
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                listener.doPhotoUri(photoUri);
            } else {
                photoUri = Uri.fromFile(photoFile);
                listener.doPhotoUri(photoUri);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            if (frag != null)
                frag.startActivityForResult(intent, request_code);
            else {
                act.startActivityForResult(intent, request_code);
            }

        }
    }

    public static File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//        String imageFileName = "thefiven" + timeStamp;

        Log.i(StringUtil.TAG, "createImageFile: ");
        String imageFileName = String.valueOf(System.currentTimeMillis());

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RESPONSEPEOPLE");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(imageFileName, ".png", storageDir);
    }


    public static void cropImage(Fragment frag, Activity act, Uri photoUri, int request_code, OnPhotoAfterAction listener) {
        Log.i(StringUtil.TAG, "cropImage: ");
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri, "image/*");

        // 파일 생성
        try {
            File albumFile = createImageFile();
            Log.e(StringUtil.TAG, "cropImage: " + albumFile.getAbsolutePath());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(act, BuildConfig.APPLICATION_ID + ".provider", albumFile);
            } else {
                photoUri = Uri.fromFile(albumFile);
            }
            listener.doPhotoUri(photoUri);

        } catch (IOException e) {
            Log.e(StringUtil.TAG, "cropImage: 에러");
            e.printStackTrace();
        }

        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoUri);

        // 여러 카메라어플중 기본앱 세팅
        List<ResolveInfo> list = act.getPackageManager().queryIntentActivities(cropIntent, 0);

        Intent i = new Intent(cropIntent);
        ResolveInfo res = list.get(0);

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        act.grantUriPermission(res.activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        if (frag != null)
            frag.startActivityForResult(i, request_code);
        else {
            act.startActivityForResult(i, request_code);
        }
    }
}
