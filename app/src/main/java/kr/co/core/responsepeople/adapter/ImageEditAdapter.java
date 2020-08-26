package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
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
import kr.co.core.responsepeople.data.ImageEditData;
import kr.co.core.responsepeople.util.StringUtil;


public class ImageEditAdapter extends RecyclerView.Adapter<ImageEditAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<ImageEditData> list = new ArrayList<>();
    private ButtonClickListener buttonClickListener;

    public interface ButtonClickListener {
        void selectButtonClicked();
        void expand(String image);
        void deleteButtonClicked();
    }


    public ImageEditAdapter(Activity act, ArrayList<ImageEditData> list, ButtonClickListener buttonClickListener ) {
        this.act = act;
        this.list = list;
        this.buttonClickListener = buttonClickListener;
    }

    public void setList(ArrayList<ImageEditData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<ImageEditData> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageEditAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ImageEditData data = list.get(i);

        if(data == null) {
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

            Glide.with(act).load(data.getImageUrl()).into(holder.image);
            holder.image.setOnClickListener(view -> {
                buttonClickListener.expand(data.getImageUrl());
            });

            holder.btn_delete.setOnClickListener(v -> {
                list.remove(i);
                notifyDataSetChanged();
                buttonClickListener.deleteButtonClicked();
            });




            if(StringUtil.isNull(data.getImageState())) {
                holder.state.setVisibility(View.GONE);
            } else if(data.getImageState().equalsIgnoreCase("Y")) {
                holder.state.setVisibility(View.VISIBLE);
                holder.state.setText("통과");
                holder.state.setTextColor(Color.parseColor("#80cdf0"));
            } else if(data.getImageState().equalsIgnoreCase("N")) {
                holder.state.setVisibility(View.VISIBLE);
                holder.state.setText("심사중");
                holder.state.setTextColor(Color.parseColor("#f0e980"));
            } else if(data.getImageState().equalsIgnoreCase("F")) {
                holder.state.setVisibility(View.VISIBLE);
                holder.state.setText("불합격");
                holder.state.setTextColor(Color.parseColor("#f08080"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btn_select;
        TextView representative, state;
        FrameLayout btn_delete, select_area, data_area;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            btn_select = itemView.findViewById(R.id.btn_select);
            representative = itemView.findViewById(R.id.representative);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            select_area = itemView.findViewById(R.id.select_area);
            data_area = itemView.findViewById(R.id.data_area);
            state = itemView.findViewById(R.id.state);
        }
    }
}
