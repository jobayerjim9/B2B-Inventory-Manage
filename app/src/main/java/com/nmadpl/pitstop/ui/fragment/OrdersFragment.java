package com.nmadpl.pitstop.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.OrdersAdapter;
import com.nmadpl.pitstop.databinding.FragmentOrdersBinding;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;

import java.util.ArrayList;
import java.util.Collections;


public class OrdersFragment extends Fragment {
    private FragmentOrdersBinding binding;
    private ArrayList<OrderModel> orderModels=new ArrayList<>();
    private OrdersAdapter ordersAdapter;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_orders,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        ordersAdapter=new OrdersAdapter(requireContext(),orderModels);
        binding.ordersRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.ordersRecycler.setAdapter(ordersAdapter);

        getOrders();

    }

    private void getOrders() {
        binding.setLoading(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.ORDER_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.noItem.setVisibility(View.VISIBLE);
                }
                else {
                    binding.noItem.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        OrderModel orderModel=dataSnapshot.getValue(OrderModel.class);
                        if (orderModel!=null) {
                            orderModels.add(orderModel);
                            ordersAdapter.notifyDataSetChanged();
                        }
                    }
                    Collections.reverse(orderModels);

                }
                binding.setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}