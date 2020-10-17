package com.project.messenger.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.messenger.models.Request;
import com.project.messenger.R;


import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.HolderRequest> {

    private LayoutInflater layoutInflater;
    private ArrayList<Request> data;
    private OnClickRequestListener listener;

    public RequestAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickRequestListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<Request> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HolderRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_request, parent, false);
        return new HolderRequest(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRequest holder, int position) {
        final Request request = data.get(position);
        holder.bindView(request);
        if (listener != null) {
            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAcceptRequest(request);
                }
            });
            holder.denyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDenyRequest(request);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class HolderRequest extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView from, message, acceptOrNot;
        private Button acceptBtn, denyBtn;

        public HolderRequest(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            imageView = itemView.findViewById(R.id.requestFromImage);
            from = itemView.findViewById(R.id.requestFromName);
            message = itemView.findViewById(R.id.requestFromMessage);
            acceptOrNot = itemView.findViewById(R.id.acceptOrNot);
            acceptBtn = itemView.findViewById(R.id.requestAcceptBtn);
            denyBtn = itemView.findViewById(R.id.requestDenyBtn);
        }

        private void bindView(Request request) {
            Glide.with(imageView).load(request.getFromUrl()).into(imageView);
            from.setText(request.getFrom());
            message.setText(request.getMessage());
            if (request.getStatus()){
                message.setVisibility(View.INVISIBLE);
                acceptOrNot.setVisibility(View.VISIBLE);
                acceptBtn.setVisibility(View.GONE);
                denyBtn.setVisibility(View.GONE);
            }else {
                message.setVisibility(View.VISIBLE);
                acceptOrNot.setVisibility(View.INVISIBLE);
            }
        }
    }

    public interface OnClickRequestListener {
        void onAcceptRequest(Request request);
        void onDenyRequest(Request request);
    }

}
