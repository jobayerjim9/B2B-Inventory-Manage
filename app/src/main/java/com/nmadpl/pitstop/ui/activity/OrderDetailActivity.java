package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.OrderItemAdapter;
import com.nmadpl.pitstop.databinding.ActivityOrderDetailBinding;
import com.nmadpl.pitstop.models.Constants;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;
import com.nmadpl.pitstop.models.UserDetail;

import java.util.Date;

public class OrderDetailActivity extends AppCompatActivity {
    ActivityOrderDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_order_detail);
        initView();
    }

    private void initView() {
        OrderModel orderModel=(OrderModel) getIntent().getSerializableExtra("item");
        binding.setData(orderModel);
        binding.orderItemRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.orderItemRecycler.setAdapter(new OrderItemAdapter(this,orderModel.getOrderItems()));
        binding.changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(OrderDetailActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.status_change_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Date now=new Date();
                        String placeHolder=now.getDate()+"-"+(now.getMonth()+1)+"-"+(now.getYear()+1900);
                        if (item.getItemId()==R.id.acceptedStatus) {
                            orderModel.setAcceptedDate(placeHolder);
                            updateOrder(orderModel, Constants.ORDER_ACCEPTED);
                        }
                        else if (item.getItemId()==R.id.pendingStatus) {
                            updateOrder(orderModel, Constants.ORDER_PENDING);
                        }
                        else if (item.getItemId()==R.id.deliveredStatus) {
                            orderModel.setDeliveredDate(placeHolder);
                            updateOrder(orderModel, Constants.ORDER_DELIVERED);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.USER_PATH).child(orderModel.getOrderBy());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetail userDetail=snapshot.getValue(UserDetail.class);
                if (userDetail!=null) {
                    binding.setUser(userDetail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateOrder(OrderModel orderModel, String order) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.ORDER_PATH).child(orderModel.getOrderBy()).child(orderModel.getOrderId());
        orderModel.setStatus(order);
        databaseReference.setValue(orderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Status Updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}