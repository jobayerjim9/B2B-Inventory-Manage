package com.nmadpl.pitstop.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.OrdersOwnerAdapter;
import com.nmadpl.pitstop.databinding.FragmentDeliveredBinding;
import com.nmadpl.pitstop.models.Constants;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;

import java.util.ArrayList;

public class DeliveredFragment extends Fragment {
    private FragmentDeliveredBinding binding;
    private ArrayList<OrderModel> orderModels=new ArrayList<>();
    private OrdersOwnerAdapter ordersOwnerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_delivered,container,false);

        initView();
        return binding.getRoot();
    }

    private void initView() {
        ordersOwnerAdapter=new OrdersOwnerAdapter(requireContext(),orderModels);
        binding.ordersRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.ordersRecycler.setAdapter(ordersOwnerAdapter);
        getUpcomingOrder();

    }

    private void getUpcomingOrder() {
        binding.setLoading(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.ORDER_PATH);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderModels.clear();
                binding.setLoading(true);
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            OrderModel orderModel = dataSnapshot1.getValue(OrderModel.class);
                            if (orderModel != null) {
                                if (orderModel.getStatus().equals(Constants.ORDER_DELIVERED)) {
                                    orderModels.add(orderModel);
                                    binding.noItem.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                }
                if (orderModels.size()==0) {
                    binding.noItem.setVisibility(View.VISIBLE);
                }
                ordersOwnerAdapter.notifyDataSetChanged();
                binding.setLoading(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}