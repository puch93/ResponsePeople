package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.BaseData;
import kr.co.core.responsepeople.data.EtcData;

public class EtcAdapter extends RecyclerView.Adapter<EtcAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<EtcData> list = new ArrayList<>();
    private ArrayList<String> list_selected = new ArrayList<>();

    public EtcAdapter(Activity act, ArrayList<EtcData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<EtcData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedList() {
        return this.list_selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etc, parent, false);
        return new EtcAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        EtcData data = list.get(i);

        holder.itemView.setSelected(data.isSelected());
        holder.contents.setText(data.getContents());
        holder.contents.setOnClickListener(v -> {
            if(data.isSelected()) {
                data.setSelected(false);
                list_selected.remove(data.getContents());
            } else {
                data.setSelected(true);
                list_selected.add(data.getContents());
            }
            list.set(i, data);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView contents;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            contents = itemView.findViewById(R.id.contents);
        }
    }
}
