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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.HolderUser> implements Filterable {

    private LayoutInflater layoutInflater;
    private ArrayList<User> data;
    private ArrayList<User> dataFull;
    private OnClickUserListener listener;

    public UserAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickUserListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<User> data) {
        this.data = data;
        this.dataFull = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_user, parent, false);
        return new HolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderUser holder, int position) {
        final User user = data.get(position);
        holder.bindView(user);
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickUser(user);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Filter getFilter() {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filterUsers = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filterUsers.addAll(dataFull);
            }else {
                String filterPattern = constraint.toString().trim();

                for (User user : dataFull) {
                    if (user.getEmail().toLowerCase().contains(filterPattern)){
                        filterUsers.add(user);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterUsers;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class HolderUser extends RecyclerView.ViewHolder {
        private CircleImageView imageUser;
        private TextView username, email;
        private CheckBox checkBox;

        public HolderUser(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            imageUser = itemView.findViewById(R.id.userImage);
            username = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
            checkBox = itemView.findViewById(R.id.checkBoxUser);
        }

        private void bindView(User user) {
            Glide.with(imageUser).load(user.getImageUrl()).into(imageUser);
            email.setText(user.getEmail());
            username.setText(user.getUsername());

        }
    }

    public interface OnClickUserListener {
        void onClickUser(User user);
    }

}
