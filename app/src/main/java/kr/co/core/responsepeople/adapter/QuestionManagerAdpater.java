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

public class QuestionManagerAdpater extends RecyclerView.Adapter<QuestionManagerAdpater.ViewHolder> {
    private Activity act;
    private ArrayList<QuestionManageData> list;

    public QuestionManagerAdpater(Activity act, ArrayList<QuestionManageData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<QuestionManageData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionManagerAdpater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        QuestionManageData data = list.get(i);

        holder.question.setText(data.getQuestion());
        holder.answer_my.setText(data.getAnswer());

        holder.root.removeAllViews();
        LayoutInflater mInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int j = 0; j < data.getSheet().length; j++) {
            CheckBox checkBox = (CheckBox) mInflater.inflate(R.layout.layout_checkbox, holder.root, false);
            checkBox.setText(data.getSheet()[j]);
            if(data.getSheet()[j].equalsIgnoreCase(data.getAnswer())) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            holder.root.addView(checkBox);
        }

        if(data.isSelect()) {
            holder.root.setVisibility(View.VISIBLE);
        } else {
            holder.root.setVisibility(View.GONE);
        }
        holder.title_area.setOnClickListener(v -> {
            data.setSelect(!data.isSelect());
            notifyItemChanged(i);
        });
        holder.btn_delete.setOnClickListener(v -> {
            Common.showAlert(act, "질문지 삭제", "해당 질문지를 삭제하시겠습니까?", new Common.OnAlertAfter() {
                @Override
                public void onAfterOk() {
                    doDeleteQuestion(data.getIdx(), i);
                }

                @Override
                public void onAfterCancel() {
                }
            });
        });
    }

    private void doDeleteQuestion(String q_idx, int position) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            list.remove(position);
                            notifyDataSetChanged();
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
        server.setTag("Delete Question");
        server.addParams("dbControl", NetUrls.QUESTION_DELETE);
        server.addParams("q_idx", q_idx);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer_my;
        LinearLayout root, title_area;
        FrameLayout btn_revise, btn_delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer_my = itemView.findViewById(R.id.answer_my);
            root = itemView.findViewById(R.id.root);
            title_area = itemView.findViewById(R.id.title_area);
            btn_revise = itemView.findViewById(R.id.btn_revise);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
