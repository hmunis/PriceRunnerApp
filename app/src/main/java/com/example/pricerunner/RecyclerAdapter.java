package com.example.pricerunner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Information> productList;
    private Context context;

    public RecyclerAdapter(ArrayList<Information>productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.priceTextView.setText(productList.get(position).getPrice());
        holder.titleTextView.setText(productList.get(position).getTitle());
        holder.sellerTextView.setText(productList.get(position).getSeller());
        Picasso.get().load(productList.get(position).getImageURL()).resize(100,80).into(holder.productImageView);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImageView;
        private TextView titleTextView, priceTextView, sellerTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageView = (ImageView) itemView.findViewById(R.id.imageViewProduct);
            titleTextView = (TextView) itemView.findViewById(R.id.tvProductName);
            priceTextView = (TextView) itemView.findViewById(R.id.tvProductPrice);
            sellerTextView = (TextView) itemView.findViewById(R.id.tvSellerName);
        }
    }
}
