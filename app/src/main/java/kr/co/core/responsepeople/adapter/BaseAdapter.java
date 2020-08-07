package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.BaseData;

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {
    private Activity act;

    private ArrayList<BaseData> list;

    public BaseAdapter(Activity act, ArrayList<BaseData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<BaseData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public ArrayList<BaseData> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etc, parent, false);
        return new BaseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        BaseData data = list.get(i);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
