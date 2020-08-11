package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.QuestionManageData;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class QuestionSendAdapter extends RecyclerView.Adapter<QuestionSendAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<QuestionManageData> list;
    SelectListener selectListener;

    public interface SelectListener {
        void selected();
    }
    public ArrayList<QuestionManageData> getList() {
        return list;
    }
    public QuestionSendAdapter(Activity act, ArrayList<QuestionManageData> list, SelectListener selectListener) {
        this.act = act;
        this.list = list;
        this.selectListener = selectListener;
    }

    public void setList(ArrayList<QuestionManageData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_send, parent, false);
        return new QuestionSendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        QuestionManageData data = list.get(i);

        holder.question.setText(data.getQuestion());
        holder.answer_my.setText(data.getAnswer());

        holder.itemView.setOnClickListener(v -> {
            data.setSelect(!data.isSelect());
            list.set(i, data);
            holder.itemView.setSelected(!holder.itemView.isSelected());
            selectListener.selected();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer_my;
        View itemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            question = itemView.findViewById(R.id.question);
            answer_my = itemView.findViewById(R.id.answer_my);

        }
    }
}
