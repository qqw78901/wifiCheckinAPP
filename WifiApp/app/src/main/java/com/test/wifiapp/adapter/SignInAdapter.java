package com.test.wifiapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.wifiapp.R;
import com.test.wifiapp.entity.SignIn;

import java.util.List;

public class SignInAdapter extends RecyclerView.Adapter<SignInAdapter.ViewHolder> {

    private List<SignIn> mList;

    @Override
    public SignInAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sign_in_litem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SignInAdapter.ViewHolder holder, int position) {
        SignIn signIn = mList.get(position);
        holder.name.setText(signIn.getName());
        holder.createDt.setText(signIn.getCreateDt());
        holder.mac.setText(signIn.getMac());
        holder.ip.setText(signIn.getCreateIp());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView createDt;
        TextView mac;
        TextView ip;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.user_name);
            createDt = (TextView) itemView.findViewById(R.id.sign_in_time);
            mac = (TextView) itemView.findViewById(R.id.mac_address);
            ip = (TextView) itemView.findViewById(R.id.ip_address);
        }
    }

    public SignInAdapter(List<SignIn> list) {
        mList = list;
    }
}
