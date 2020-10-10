package com.dikshya.otaku_events.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dikshya.otaku_events.R;
import com.dikshya.otaku_events.activities.EventDetailsActivity;
import com.dikshya.otaku_events.model.EventsDto;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private List<EventsDto> mData;
    private List<EventsDto> searchedList;
    private Boolean bookMarked;

    public RecyclerViewAdapter(Context mContext, List<EventsDto> mData, Boolean bookMarked) {
        this.mContext = mContext;
        this.mData = mData;
        searchedList = new ArrayList<>(mData);
        this.bookMarked = bookMarked;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_event, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.txtName.setText(mData.get(position).getName());
        holder.txtSchedule.setText(mData.get(position).getSchedule());

        String imageString = mData.get(position).getImgString();
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.imgEvent.setImageBitmap(decodedImage);

        // Set click Listeners ---
        holder.individualCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, EventDetailsActivity.class);
                i.putExtra("Event_id", mData.get(position).get_id());
                i.putExtra("bookMarked", bookMarked);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<EventsDto> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(searchedList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (EventsDto item: searchedList) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData.clear();
            mData.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtSchedule;
        ImageView imgEvent;
        CardView individualCard;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtSchedule = itemView.findViewById(R.id.txtSchedule);
            imgEvent= itemView.findViewById(R.id.imgEvent);
            individualCard= itemView.findViewById(R.id.individualCard);
        }
    }
}
