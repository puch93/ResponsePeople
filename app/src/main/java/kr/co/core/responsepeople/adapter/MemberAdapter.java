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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.util.Common;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<MemberData> list;

    public MemberAdapter(Activity act, ArrayList<MemberData> list) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        MemberAdapter.ViewHolder viewHolder = new MemberAdapter.ViewHolder(view);

        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.rcv_height_member_minus)) / 2;

        if (height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.rcv_height_member_default);
        } else {
            height = (int) (height * 1.25);
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

        if(data.isSalary_ok()) {
            holder.salary.setText(data.getSalary());
        } else {
            holder.salary.setText("연봉 검수중");
        }

        Common.processProfileImageRec(act, holder.profile_img, data.getProfile_img(), data.isImage_ok(), 5, 3);
        holder.btn_like.setSelected(data.isLike());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nick, age, job, location, salary;
        ImageView profile_img;
        FrameLayout btn_like, btn_question;
        View itemView;
        CardView card_view;

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

            btn_like = view.findViewById(R.id.btn_like);
            btn_question = view.findViewById(R.id.btn_question);
        }
    }
}
