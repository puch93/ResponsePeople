package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.NoticeData;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    private Activity act;
    private ArrayList<NoticeData> list;

    public NoticeAdapter(Activity act, ArrayList<NoticeData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        NoticeAdapter.ViewHolder viewHolder = new NoticeAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, final int i) {
        final NoticeData data = list.get(i);

        holder.title.setText(data.getTitle());
        holder.register_date.setText(data.getDate());
        holder.contents.setText(data.getDetail());
        holder.title_area.setSelected(data.isSelect());
        if (data.isSelect()) {
            holder.contents_area.setVisibility(View.VISIBLE);
        } else {
            holder.contents_area.setVisibility(View.GONE);
        }

        holder.title_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setSelect(!data.isSelect());
                notifyItemChanged(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<NoticeData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, contents, register_date;
        LinearLayout title_area, contents_area;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            contents = view.findViewById(R.id.contents);
            register_date = view.findViewById(R.id.register_date);
            title_area = view.findViewById(R.id.title_area);
            contents_area = view.findViewById(R.id.contents_area);

            itemView = view;
        }
    }
}
