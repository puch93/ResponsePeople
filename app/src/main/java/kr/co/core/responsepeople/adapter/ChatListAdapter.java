package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ChatAct;
import kr.co.core.responsepeople.data.ChatListData;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<ChatListData> list;

    public ChatListAdapter(Activity act, ArrayList<ChatListData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<ChatListData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ChatListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ChatListData data = list.get(i);

        Common.processProfileImageCircle(act, holder.profile_img, data.getY_profile_img(), data.isY_profile_img_ok(), 2, 5);
        holder.nick.setText(data.getY_nick());
        holder.age.setText(data.getY_age());
        holder.contents.setText(data.getContents());
        holder.send_date.setText(data.getSend_date());
        if(StringUtil.isNull(data.getUnread_count()) || data.getUnread_count().equalsIgnoreCase("0")) {
            holder.unread_count.setVisibility(View.INVISIBLE);
        } else {
            holder.unread_count.setVisibility(View.VISIBLE);
            holder.unread_count.setText(data.getUnread_count());
        }

        holder.itemView.setOnClickListener(v -> {
            act.startActivity(new Intent(act, ChatAct.class)
                    .putExtra("t_idx", data.getY_idx())
                    .putExtra("room_idx", data.getRoom_idx())
            );
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_img;
        TextView nick, age, contents, send_date, unread_count;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.profile_img);
            nick = itemView.findViewById(R.id.nick);
            age = itemView.findViewById(R.id.age);
            contents = itemView.findViewById(R.id.contents);
            send_date = itemView.findViewById(R.id.send_date);
            unread_count = itemView.findViewById(R.id.unread_count);
        }
    }
}
