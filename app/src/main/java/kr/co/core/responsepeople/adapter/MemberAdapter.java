package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.MemberData;

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
        return new MemberAdapter.ViewHolder(view);
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

        Glide.with(act).load(data.getProfile_img()).into(holder.profile_img);
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

        ViewHolder(@NonNull View view) {
            super(view);
            itemView = view;

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
