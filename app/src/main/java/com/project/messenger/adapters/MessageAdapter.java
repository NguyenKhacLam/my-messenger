package com.project.messenger.adapters;


import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.messenger.models.Message;
import com.project.messenger.R;


import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.HolderMessage> {

    private LayoutInflater layoutInflater;
    private ArrayList<Message> data;
    private OnClickMessageListener listener;

    public MessageAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickMessageListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<Message> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_message, parent, false);
        return new HolderMessage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMessage holder, int position) {
        final Message message = data.get(position);
        holder.bindView(message);
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickMessage(message);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onClickLongMessage(message);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class HolderMessage extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView userText;
        private LinearLayout cardView;
        
        public HolderMessage(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            userImage = itemView.findViewById(R.id.senderImage);
            userText = itemView.findViewById(R.id.senderMessage);
            cardView = itemView.findViewById(R.id.cardMessage);
        }

        private void bindView(Message message) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Glide.with(userImage).load(message.getSenderImage()).into(userImage);
            userText.setText(message.getContent());

            if (message.getSenderEmail().equals(user.getEmail())){
                userImage.setVisibility(View.GONE);
                userText.setBackgroundColor(Color.WHITE);
                userText.setTextColor(Color.BLACK);
                cardView.setGravity(Gravity.END);
            }else {
                userImage.setVisibility(View.VISIBLE);
                cardView.setGravity(Gravity.START);
            }
        }
    }

    public interface OnClickMessageListener {
        void onClickMessage(Message message);

        void onClickLongMessage(Message message);
    }

}
