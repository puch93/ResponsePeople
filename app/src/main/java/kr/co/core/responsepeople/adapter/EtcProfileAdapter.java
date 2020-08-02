package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.EtcIdealAct;
import kr.co.core.responsepeople.data.EtcData;

public class EtcProfileAdapter extends RecyclerView.Adapter<EtcProfileAdapter.ViewHolder> {
    private Activity act;
    private Fragment frag;
    private ArrayList<EtcData> list = new ArrayList<>();
    private String type;

    private boolean clickable = true;

    public EtcProfileAdapter(Fragment frag, Activity act, ArrayList<EtcData> list, String type) {
        this.act = act;
        this.frag = frag;
        this.list = list;
        this.type = type;
    }


    public void setList(ArrayList<EtcData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etc_profile, parent, false);
        return new EtcProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        EtcData data = list.get(i);

        if (i == 0) {
            holder.contents.setVisibility(View.GONE);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.btn_add.setOnClickListener(v -> {
                if (clickable) {
                    frag.startActivityForResult(new Intent(act, EtcIdealAct.class)
                                    .putExtra("type", type)
                                    .putExtra("list", list)
                            , 1004);
                }
            });
        } else {
            holder.contents.setVisibility(View.VISIBLE);
            holder.btn_add.setVisibility(View.GONE);
            holder.contents.setText(data.getContents());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView contents, btn_add;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            contents = itemView.findViewById(R.id.contents);
            btn_add = itemView.findViewById(R.id.btn_add);
        }
    }
}
