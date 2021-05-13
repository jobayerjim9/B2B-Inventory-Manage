package com.nmadpl.pitstop.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ItemOrdersBinding;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;
import com.nmadpl.pitstop.models.UserDetail;
import com.nmadpl.pitstop.ui.activity.OrderDetailCustomerActivity;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>  {
    private Context context;
    private ArrayList<OrderModel> orderModels;

    public OrdersAdapter(Context context, ArrayList<OrderModel> orderModels) {
        this.context = context;
        this.orderModels = orderModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_orders,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrderModel orderModel=orderModels.get(position);
        holder.binding.setData(orderModel);
        if (orderModel.getAcceptedDate()!=null) {
            holder.binding.acceptedDate.setText("Accepted Date: "+orderModel.getAcceptedDate());
            holder.binding.acceptedDate.setVisibility(View.VISIBLE);
        }
        if (orderModel.getDeliveredDate()!=null) {
            holder.binding.deliveryDate.setText("Delivery Date: "+orderModel.getDeliveredDate());
            holder.binding.deliveryDate.setVisibility(View.VISIBLE);
        }
        holder.binding.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OrderDetailCustomerActivity.class);
                intent.putExtra("item",orderModel);
                context.startActivity(intent);
            }
        });

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrdersBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
