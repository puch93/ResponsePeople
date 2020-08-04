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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ProfileDetailAct;
import kr.co.core.responsepeople.data.ResponseData;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<ResponseData> list;

    public ResponseAdapter(Activity act, ArrayList<ResponseData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<ResponseData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_response, parent, false);
        ResponseAdapter.ViewHolder viewHolder = new ResponseAdapter.ViewHolder(view);

        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.rcv_height_home_minus));

        if (height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.rcv_height_home_default);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.card_view.getLayoutParams();
        params.height = height;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ResponseData data = list.get(i);

        holder.nick.setText(data.getNick());
        holder.age.setText(data.getAge());
        holder.job.setText(data.getJob());
        holder.location.setText(data.getLocation());

        if (data.isSalary_ok()) {
            holder.salary.setText(data.getSalary());
        } else {
            holder.salary.setText("연봉 검수중");
        }

        Common.processProfileImageRec(act, holder.profile_img, data.getProfile_img(), data.isImage_ok(), 5, 3);
        holder.btn_like.setSelected(data.isLike());

        holder.itemView.setOnClickListener(v -> {
            checkProfileRead(data.getIdx());
        });

        holder.progressBar.setProgress(data.getProgress());
        holder.progressText.setText(String.valueOf(data.getProgress()) + "%");


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nick, age, job, location, salary, progressText;
        ImageView profile_img;
        FrameLayout btn_like, btn_question;
        View itemView;
        CardView card_view;
        ProgressBar progressBar;

        ViewHolder(@NonNull View view) {
            super(view);
            itemView = view;

            card_view = view.findViewById(R.id.card_view);
            nick = view.findViewById(R.id.nick);
            age = view.findViewById(R.id.age);
            job = view.findViewById(R.id.job);
            location = view.findViewById(R.id.location);
            salary = view.findViewById(R.id.salary);
            profile_img = view.findViewById(R.id.profile_img);
            progressBar = view.findViewById(R.id.progressBar);
            progressText = view.findViewById(R.id.progressText);

            btn_like = view.findViewById(R.id.btn_like);
            btn_question = view.findViewById(R.id.btn_question);
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
                            act.startActivity(new Intent(act, ProfileDetailAct.class)
                                    .putExtra("type", "pay")
                                    .putExtra("y_idx", y_idx)
                            );
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
                                            act.startActivity(new Intent(act, ProfileDetailAct.class)
                                                    .putExtra("type", "free")
                                                    .putExtra("y_idx", y_idx)
                                            );
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
                                            act.startActivity(new Intent(act, ProfileDetailAct.class)
                                                    .putExtra("type", "pay")
                                                    .putExtra("y_idx", y_idx)
                                            );
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
