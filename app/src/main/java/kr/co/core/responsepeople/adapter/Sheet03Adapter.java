package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.SheetData;

public class Sheet03Adapter extends RecyclerView.Adapter<Sheet03Adapter.ViewHolder> {
    private Activity act;
    private ArrayList<SheetData> list;

    public Sheet03Adapter(Activity act, ArrayList<SheetData> list, SelectListener listener) {
        this.act = act;
        this.list = list;
        this.listener = listener;
    }

    public void setList(ArrayList<SheetData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<SheetData> getList() {
        return list;
    }

    public interface SelectListener {
        void select(int position);
    }

    SelectListener listener;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sheet02, parent, false);
        return new Sheet03Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        SheetData data = list.get(i);
        holder.sheet.setEnabled(false);
        holder.sheet.setText(data.getData());
        holder.sheet.setChecked(data.isChecked());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox sheet;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            sheet = (CheckBox) itemView.findViewById(R.id.sheet);
        }
    }
}
