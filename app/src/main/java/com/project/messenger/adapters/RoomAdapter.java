package com.project.messenger.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.messenger.models.Room;
import com.project.messenger.R;


import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.HolderRoom> implements Filterable {

    private LayoutInflater layoutInflater;
    private ArrayList<Room> data;
    private ArrayList<Room> dataFull;
    private OnClickRoomListener listener;

    public RoomAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickRoomListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<Room> data) {
        this.data = data;
        this.dataFull = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public ArrayList<Room> getData() {
        return data;
    }

    @NonNull
    @Override
    public HolderRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_room, parent, false);
        return new HolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRoom holder, int position) {
        final Room room = data.get(position);
        holder.bindView(room);
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickRoom(room);
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
            List<Room> filterdRooms = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filterdRooms.addAll(dataFull);
            }else {
                String filterPattern = constraint.toString().trim();
                for (Room room : dataFull){
                    if (room.getName().contains(filterPattern)){
                        filterdRooms.add(room);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterdRooms;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class HolderRoom extends RecyclerView.ViewHolder {
        private ImageView roomImage;
        private TextView roomName, createdAt;
        public HolderRoom(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            roomImage = itemView.findViewById(R.id.roomImage);
            roomName = itemView.findViewById(R.id.roomName);
            createdAt = itemView.findViewById(R.id.roomCreatedAt);
        }

        private void bindView(Room room) {
            roomName.setText(room.getName());
            Glide.with(roomImage).load(room.getImageUrl()).into(roomImage);
            createdAt.setText("Happy chatting...!");
        }
    }

    public interface OnClickRoomListener {
        void onClickRoom(Room room);
    }

}
