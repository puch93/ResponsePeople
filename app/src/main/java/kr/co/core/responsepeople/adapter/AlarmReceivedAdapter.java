package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap ;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.BaseData;
import kr.co.core.responsepeople.data.QuestionReceivedData;
import kr.co.core.responsepeople.dialog.QuestionAnswerDlg;
import kr.co.core.responsepeople.util.Common;

public class AlarmReceivedAdapter extends RecyclerView.Adapter<AlarmReceivedAdapter.ViewHolder> {
    private Activity act;
    ArrayList<ArrayList<QuestionReceivedData>> list = new ArrayList<>();

    public AlarmReceivedAdapter(Activity act, ArrayList<ArrayList<QuestionReceivedData>> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<ArrayList<QuestionReceivedData>> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public ArrayList<ArrayList<QuestionReceivedData>> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_alarm, parent, false);
        return new AlarmReceivedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        QuestionReceivedData data = list.get(i).get(0);

        holder.nick.setText(data.getNick());
        holder.age.setText(data.getAge());
        holder.register_date.setText(data.getQh_regdate());
        Common.processProfileImageCircle(act, holder.profile_img, data.getProfile_img(), data.isProfile_img_ck(), 2, 5);
        holder.btn_response.setOnClickListener(v -> {
            act.startActivity(new Intent(act, QuestionAnswerDlg.class).putExtra("list", list.get(i)));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nick, age, register_date, btn_response;
        ImageView profile_img;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            nick = itemView.findViewById(R.id.nick);
            age = itemView.findViewById(R.id.age);
            register_date = itemView.findViewById(R.id.register_date);
            btn_response = itemView.findViewById(R.id.btn_response);
            profile_img = itemView.findViewById(R.id.profile_img);
        }
    }
}
