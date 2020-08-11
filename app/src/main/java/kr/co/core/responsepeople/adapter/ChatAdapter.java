package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.EnlargeAct;
import kr.co.core.responsepeople.data.ChatData;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int TYPE_ME_TEXT = 1;
    private static final int TYPE_ME_IMAGE = 3;
    private static final int TYPE_YOU_TEXT = 2;
    private static final int TYPE_YOU_IMAGE = 4;
    private static final int TYPE_DATE_LINE = 100;

    private Activity act;
    private ArrayList<ChatData> list;

    private String otherImage, otherNick, otherAge;
    private boolean otherImageOk;

    private String room_idx;

    public void setOtherInfo(String otherImage, String otherNick, String otherAge, boolean otherImageOk) {
        this.otherImage = otherImage;
        this.otherNick = otherNick;
        this.otherAge = otherAge;
        this.otherImageOk = otherImageOk;
    }

    public ChatAdapter(Activity act, String room_idx, ArrayList<ChatData> list) {
        this.act = act;
        this.list = list;
        this.room_idx = room_idx;
    }


    public void addItem(ChatData item) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(item);
        notifyDataSetChanged();
    }

    public void setItem(ChatData item) {
        list.set(list.size() - 1, item);
        notifyDataSetChanged();
    }

    public void setList(ArrayList<ChatData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ME_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me_text, parent, false);
                return new ViewHolder1(view);
            case TYPE_YOU_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other_text, parent, false);
                return new ViewHolder2(view);

            case TYPE_ME_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me_image, parent, false);
                return new ViewHolder3(view);
            case TYPE_YOU_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other_image, parent, false);
                return new ViewHolder4(view);


            case TYPE_DATE_LINE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_dateline, parent, false);
                return new ViewHolder100(view);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatData data = list.get(position);

        String type = data.getData_type();

        String midx = AppPreference.getProfilePref(act, AppPreference.PREF_MIDX);

        switch (type) {
            case "text":
            case "out":
                if (data.getUser_idx().equalsIgnoreCase(midx))
                    return TYPE_ME_TEXT;
                else
                    return TYPE_YOU_TEXT;

            case "image":
                if (data.getUser_idx().equalsIgnoreCase(midx))
                    return TYPE_ME_IMAGE;
                else
                    return TYPE_YOU_IMAGE;

            case "dateline":
                return TYPE_DATE_LINE;

            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
        final ChatData data = list.get(i);
        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE_ME_TEXT:
                ViewHolder1 holder1 = (ViewHolder1) viewHolder;

                // set text contents
                holder1.contents.setText(data.getContents());

                // set send time
                holder1.send_time.setText(data.getSend_time());

                // 읽음처리
                if (data.isRead()) {
                    holder1.read.setVisibility(View.GONE);
                } else {
                    holder1.read.setVisibility(View.VISIBLE);
                }

                break;

            case TYPE_YOU_TEXT:
                ViewHolder2 holder2 = (ViewHolder2) viewHolder;

                // set other data
                setOtherData(holder2.profile_img, holder2.nick, holder2.age);

                // set text contents
                holder2.contents.setText(data.getContents());

                // set send time
                holder2.send_time.setText(data.getSend_time());


                break;

            case TYPE_ME_IMAGE:
                ViewHolder3 holder3 = (ViewHolder3) viewHolder;

                // set image contents
                Glide.with(act)
                        .load(data.getContents())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                        .into(holder3.send_image);

                // set send time
                holder3.send_time.setText(data.getSend_time());

                holder3.send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", data.getContents()));
                    }
                });

                // 읽음처리
                if (data.isRead()) {
                    holder3.read.setVisibility(View.GONE);
                } else {
                    holder3.read.setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_YOU_IMAGE:
                ViewHolder4 holder4 = (ViewHolder4) viewHolder;

                // set other data
                setOtherData(holder4.profile_img, holder4.nick, holder4.age);

                // set image contents
                Glide.with(act)
                        .load(data.getContents())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                        .into(holder4.send_image);

                holder4.send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", data.getContents()));
                    }
                });

                // set send time
                holder4.send_time.setText(data.getSend_time());

                break;

            case TYPE_DATE_LINE:
                ViewHolder100 holder100 = (ViewHolder100) viewHolder;

                // set date
                holder100.date.setText(data.getDate_line());
                break;
        }

    }

    private void setOtherData(ImageView imageView, TextView nick, TextView age) {
        Common.processProfileImageCircle(act, imageView, otherImage, otherImageOk, 2, 5);

        nick.setText(otherNick);
        age.setText(otherAge);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View view) {
            super(view);
        }
    }

    class ViewHolder1 extends ViewHolder {
        TextView contents, send_time, read;

        ViewHolder1(@NonNull View view) {
            super(view);
            contents = view.findViewById(R.id.contents);
            send_time = view.findViewById(R.id.send_time);
            read = view.findViewById(R.id.read);
        }
    }

    class ViewHolder2 extends ViewHolder {
        ImageView profile_img;
        TextView nick, age, send_time, contents;

        ViewHolder2(@NonNull View view) {
            super(view);
            contents = view.findViewById(R.id.contents);
            send_time = view.findViewById(R.id.send_time);
            profile_img = view.findViewById(R.id.profile_img);
            nick = view.findViewById(R.id.nick);
            age = view.findViewById(R.id.age);

        }
    }

    class ViewHolder3 extends ViewHolder {
        TextView send_time, read;
        ImageView send_image;

        ViewHolder3(@NonNull View view) {
            super(view);
            send_time = view.findViewById(R.id.send_time);
            read = view.findViewById(R.id.read);
            send_image = (ImageView) view.findViewById(R.id.send_image);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                send_image.setClipToOutline(true);
            }
        }
    }

    class ViewHolder4 extends ViewHolder {
        ImageView profile_img, send_image;
        TextView nick, age, send_time;

        ViewHolder4(@NonNull View view) {
            super(view);
            profile_img = view.findViewById(R.id.profile_img);
            send_time = view.findViewById(R.id.send_time);
            send_image = (ImageView) view.findViewById(R.id.send_image);
            nick = view.findViewById(R.id.nick);
            age = view.findViewById(R.id.age);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                send_image.setClipToOutline(true);
            }
        }
    }

    class ViewHolder100 extends ViewHolder {
        TextView date;

        ViewHolder100(@NonNull View view) {
            super(view);
            date = view.findViewById(R.id.date);
        }
    }
}
