package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ProfileDetailAct;
import kr.co.core.responsepeople.activity.ProfileDetailBeforeAct;
import kr.co.core.responsepeople.activity.QuestionSendAct;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class MemberBeforeAdapter extends RecyclerView.Adapter<MemberBeforeAdapter.ViewHolder> {
    private Activity act;

    public ArrayList<MemberData> getList() {
        return list;
    }

    private ArrayList<MemberData> list;

    public MemberBeforeAdapter(Activity act, ArrayList<MemberData> list) {
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
        LogUtil.logI("테스트테스트2");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_before, parent, false);
        MemberBeforeAdapter.ViewHolder viewHolder = new MemberBeforeAdapter.ViewHolder(view);

        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.rcv_height_member_before_minus)) / 3;

        if (height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.rcv_height_member_before_default);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.card_view.getLayoutParams();
        params.height = height;
        LogUtil.logI("테스트테스트3");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        LogUtil.logI("테스트테스트");
        MemberData data = list.get(i);

        Common.processProfileImageRec(act, holder.profile_img, data.getProfile_img(), data.isImage_ok(), 5, 3);

        holder.itemView.setOnClickListener(v -> {
            act.startActivity(new Intent(act, ProfileDetailBeforeAct.class).putExtra("y_idx", data.getIdx()));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_img;
        View itemView;
        CardView card_view;

        ViewHolder(@NonNull View view) {
            super(view);
            itemView = view;
            card_view = view.findViewById(R.id.card_view);
            profile_img = view.findViewById(R.id.profile_img);
        }
    }
}
