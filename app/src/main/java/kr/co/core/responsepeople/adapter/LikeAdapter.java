package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ProfileDetailAct;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<MemberData> list;

    public LikeAdapter(Activity act, ArrayList<MemberData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<MemberData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like, parent, false);
        LikeAdapter.ViewHolder viewHolder = new LikeAdapter.ViewHolder(view);

        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.rcv_like_minus)) / 3;

        if (height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.rcv_like_default);
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewHolder.card_view.getLayoutParams();
        params.height = height;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        MemberData data = list.get(i);

        holder.nick.setText(data.getNick());
        holder.age.setText(data.getAge());
        holder.job.setText(data.getJob());
        holder.location.setText(data.getLocation());

        Common.processProfileImageRec(act, holder.profile_img, data.getProfile_img(), data.isImage_ok(), 5, 3);
        holder.itemView.setOnClickListener(v -> {
            checkProfileRead(data.getIdx());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView card_view;
        TextView nick, age, job, location;
        ImageView profile_img;

        View itemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            card_view = itemView.findViewById(R.id.card_view);
            nick = itemView.findViewById(R.id.nick);
            age = itemView.findViewById(R.id.age);
            job = itemView.findViewById(R.id.job);
            location = itemView.findViewById(R.id.location);
            profile_img = itemView.findViewById(R.id.profile_img);
        }
    }

    private void checkProfileRead(String y_idx) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            // 열람 했을 경우 (pay로 던지던 free로 던지던 상관없음)
                            act.startActivityForResult(new Intent(act, ProfileDetailAct.class)
                                    .putExtra("type", "pay")
                                    .putExtra("y_idx", y_idx)
                                    , ProfileDetailAct.TYPE_LIKE);
                        } else {
                            // 열람 안헀을 경우
                            check_1day_used(y_idx);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Check Profile Read");
        server.addParams("dbControl", NetUrls.CHECK_PROFILE_READ);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", y_idx);
        server.execute(true, false);
    }

    /**
     * pv_type(free:1일무료사용할때, pay:일반적으로사용)
     * */
    private void check_1day_used(String y_idx) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            // data:Y(1일무료 이용권 사용가능),N(1일무료 이용권 사용 불가능)

                            String data = StringUtil.getStr(jo, "data");
                            if (!StringUtil.isNull(data)) {
                                if (data.equalsIgnoreCase("Y")) {
                                    // 1일무료 이용권 사용가능
                                    showAlert(act, "프로필 보기", "1일무료 이용권을 사용하고 프로필을 확인하시겠습니까?", new Common.OnAlertAfter() {
                                        @Override
                                        public void onAfterOk() {
                                            act.startActivityForResult(new Intent(act, ProfileDetailAct.class)
                                                    .putExtra("type", "free")
                                                    .putExtra("y_idx", y_idx)
                                                    , ProfileDetailAct.TYPE_LIKE);
                                        }

                                        @Override
                                        public void onAfterCancel() {

                                        }
                                    });
                                } else {
                                    // 1일무료 이용권 사용불가능 -> 하트 5개소모하고 보기로
                                    showAlert(act, "프로필 보기", "하트 5개를 소모하고 프로필을 확인하시겠습니까?", new Common.OnAlertAfter() {
                                        @Override
                                        public void onAfterOk() {
                                            act.startActivityForResult(new Intent(act, ProfileDetailAct.class)
                                                    .putExtra("type", "pay")
                                                    .putExtra("y_idx", y_idx)
                                                    , ProfileDetailAct.TYPE_LIKE);
                                        }

                                        @Override
                                        public void onAfterCancel() {

                                        }
                                    });
                                }
                            }

                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Check 1Day Used");
        server.addParams("dbControl", NetUrls.CHECK_1DAY_USED);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    public void showAlert(Activity act, String title, String contents, final Common.OnAlertAfter onAlertAfter) {
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
}
