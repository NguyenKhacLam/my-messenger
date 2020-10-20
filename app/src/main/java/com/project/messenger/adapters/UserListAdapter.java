package com.project.messenger.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.messenger.R;
import com.project.messenger.models.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.HolderUser>{

    private LayoutInflater layoutInflater;
    private ArrayList<String> data;
    private OnClickUserListener listener;

    public UserListAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickUserListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_list_user, parent, false);
        return new HolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUser holder, final int position) {
        final String userEmail = data.get(position);
        holder.bindView(userEmail);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class HolderUser extends RecyclerView.ViewHolder {
        private TextView  email;

        public HolderUser(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            email = itemView.findViewById(R.id.userListEmail);
        }

        private void bindView(String emailString) {
            email.setText(emailString);
        }
    }

    public interface OnClickUserListener {
        void onClickUser(User user, int position);
    }

}
