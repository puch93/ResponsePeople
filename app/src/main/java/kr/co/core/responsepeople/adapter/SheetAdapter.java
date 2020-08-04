package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;

public class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<String> list;
    private DeleteListener deleteListener;

    public interface DeleteListener {
        void onDelete();
    }

    public SheetAdapter(Activity act, ArrayList<String> list, DeleteListener deleteListener) {
        this.act = act;
        this.list = list;
        this.deleteListener = deleteListener;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public ArrayList<String> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sheet, parent, false);
        return new SheetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String data = list.get(i);
        holder.sheet.setText(data);
        holder.btn_delete.setOnClickListener(v -> {
            list.remove(data);
            notifyDataSetChanged();

            deleteListener.onDelete();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox sheet;
        FrameLayout btn_delete;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            sheet = (CheckBox) itemView.findViewById(R.id.sheet);
            btn_delete = (FrameLayout) itemView.findViewById(R.id.btn_delete);
        }
    }
}
