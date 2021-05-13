package com.nmadpl.pitstop.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ItemOrdersBinding;
import com.nmadpl.pitstop.databinding.ItemOrdersOwnerBinding;
import com.nmadpl.pitstop.models.Constants;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;
import com.nmadpl.pitstop.models.UserDetail;
import com.nmadpl.pitstop.ui.activity.OrderDetailActivity;

import java.util.ArrayList;
import java.util.Date;

public class OrdersOwnerAdapter extends RecyclerView.Adapter<OrdersOwnerAdapter.ViewHolder>  {
    private Context context;
    private ArrayList<OrderModel> orderModels;

    public OrdersOwnerAdapter(Context context, ArrayList<OrderModel> orderModels) {
        this.context = context;
        this.orderModels = orderModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_orders_owner,parent,false));
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrderModel orderModel = orderModels.get(position);
        holder.binding.setData(orderModel);
        holder.binding.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("item", orderModel);
                context.startActivity(intent);
            }
        });
        holder.binding.setLoading(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.USER_PATH).child(orderModel.getOrderBy());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                holder.binding.setLoading(false);
                UserDetail userDetail = snapshot.getValue(UserDetail.class);
                if (userDetail != null) {
                    holder.binding.setFirmName(userDetail.getFirmName());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        holder.binding.executePendingBindings();
    }



    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrdersOwnerBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
