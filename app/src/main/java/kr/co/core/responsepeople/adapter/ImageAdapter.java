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
import kr.co.core.responsepeople.util.StringUtil;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<String> list = new ArrayList<>();
    private ButtonClickListener buttonClickListener;

    public interface ButtonClickListener {
        void selectButtonClicked();
        void expand(String image);
        void deleteButtonClicked();
    }


    public ImageAdapter(Activity act, ArrayList<String> list, ButtonClickListener buttonClickListener ) {
        this.act = act;
        this.list = list;
        this.buttonClickListener = buttonClickListener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        String data = list.get(i);

        if(StringUtil.isNull(data)) {
            holder.select_area.setVisibility(View.VISIBLE);
            holder.data_area.setVisibility(View.GONE);
            holder.btn_select.setOnClickListener(v -> {
                buttonClickListener.selectButtonClicked();
            });
        } else {
            if(i == 1) {
                holder.representative.setVisibility(View.VISIBLE);
            } else {
                holder.representative.setVisibility(View.GONE);
            }
            holder.select_area.setVisibility(View.GONE);
            holder.data_area.setVisibility(View.VISIBLE);

            Glide.with(act).load(data).into(holder.image);
            holder.image.setOnClickListener(view -> {
                buttonClickListener.expand(data);
            });

            holder.btn_delete.setOnClickListener(v -> {
                list.remove(data);
                notifyDataSetChanged();
                buttonClickListener.deleteButtonClicked();
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btn_select;
        TextView representative;
        FrameLayout btn_delete, select_area, data_area;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            btn_select = itemView.findViewById(R.id.btn_select);
            representative = itemView.findViewById(R.id.representative);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            select_area = itemView.findViewById(R.id.select_area);
            data_area = itemView.findViewById(R.id.data_area);
        }
    }
}
