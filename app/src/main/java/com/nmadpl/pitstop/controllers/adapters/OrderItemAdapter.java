package com.nmadpl.pitstop.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ItemOrderProductBinding;
import com.nmadpl.pitstop.models.OrderModel;
import com.nmadpl.pitstop.models.ProductModel;

import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OrderModel.OrderItem> orderItems;

    public OrderItemAdapter(Context context, ArrayList<OrderModel.OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_product,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       final OrderModel.OrderItem orderItem=orderItems.get(position);
       holder.binding.setData(orderItem);
       holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderProductBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
