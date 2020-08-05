package kr.co.core.responsepeople.adapter;

import android.app.Activity;
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

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.util.Common;

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
}
