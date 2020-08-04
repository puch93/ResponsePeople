package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.SheetData;

public class Sheet02Adapter extends RecyclerView.Adapter<Sheet02Adapter.ViewHolder> {
    private Activity act;
    private ArrayList<SheetData> list;

    public Sheet02Adapter(Activity act, ArrayList<SheetData> list, SelectListener listener) {
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
        return new Sheet02Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        SheetData data = list.get(i);
        holder.sheet.setText(data.getData());

        holder.sheet.setChecked(data.isChecked());
        holder.sheet.setOnClickListener(v -> {
            for (int j = 0; j < list.size(); j++) {
                SheetData sheetData = list.get(j);
                sheetData.setChecked(false);
                list.set(j, sheetData);
            }

            data.setChecked(true);
            list.set(i, data);
            notifyDataSetChanged();

            listener.select(i);
        });


//        holder.sheet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    for (int j = 0; j < list.size(); j++) {
//                        SheetData sheetData = list.get(j);
//                        sheetData.setChecked(false);
//                        list.set(j, sheetData);
//                    }
//
//                    data.setChecked(true);
//                    list.set(i, data);
//                }
//            }
//        });
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
