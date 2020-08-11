package kr.co.core.responsepeople.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.BlockData;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<BlockData> list;

    private NumberClickListener listener;

    public interface NumberClickListener {
        void clicked();
    }

    public BlockAdapter(Activity act, ArrayList<BlockData> list, NumberClickListener listener) {
        this.act = act;
        this.list = list;
        this.listener = listener;
    }
    public ArrayList<BlockData> getList() {
        return list;
    }

    public void setList(ArrayList<BlockData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block, parent, false);
        return new BlockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        BlockData data = list.get(i);
        holder.name.setText(data.getName());
        holder.number.setText(data.getNumber());

        holder.itemView.setSelected(data.isSelected());
        holder.itemView.setOnClickListener(v -> {
            data.setSelected(!data.isSelected());
            list.set(i, data);
            notifyItemChanged(i);

            listener.clicked();
            doBlock(data.getNumber());
        });
    }

    private void doBlock(String number) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {

                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        server.setTag("Block");
        server.addParams("dbControl", NetUrls.BLOCK);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("mb_hp", number);
        server.execute(true, false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView number, name;
        View itemView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            number = itemView.findViewById(R.id.number);
            name = itemView.findViewById(R.id.name);
        }
    }
}
