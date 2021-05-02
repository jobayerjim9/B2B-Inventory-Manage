package com.nmadpl.pitstop.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.OrderItemAdapter;
import com.nmadpl.pitstop.databinding.ActivityOrderDetailCustomerBinding;
import com.nmadpl.pitstop.models.OrderModel;

public class OrderDetailCustomerActivity extends AppCompatActivity {
    private ActivityOrderDetailCustomerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_order_detail_customer);
        initView();
    }

    private void initView() {
        OrderModel orderModel=(OrderModel) getIntent().getSerializableExtra("item");
        binding.setData(orderModel);
        binding.orderItemRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.orderItemRecycler.setAdapter(new OrderItemAdapter(this,orderModel.getOrderItems()));
    }
}