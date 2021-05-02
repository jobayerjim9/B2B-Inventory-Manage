package com.nmadpl.pitstop.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.FragmentHomeBinding;
import com.nmadpl.pitstop.models.Constants;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;
import com.nmadpl.pitstop.ui.activity.LoginActivity;

import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ArrayList<OrderModel> orderModels=new ArrayList<>();
    int todayOrder,todayPending,totalPending,todayAccepted,totalAccepted,todayDelivered,totalDelivered;
    double todaySale,totalSale;
    Date today;
    int todayDate,todayMonth,todayYear;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        initView();
        return binding.getRoot();

    }

    private void initView() {
        today=new Date();
        todayDate=today.getDate();
        todayMonth=today.getMonth()+1;
        todayYear=today.getYear()+1900;
        binding.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(requireContext(),v);
                popupMenu.getMenuInflater().inflate(R.menu.logout_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                        requireActivity().finish();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
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
                todayOrder=0;
                todayPending=0;
                totalPending=0;
                todayAccepted=0;
                totalAccepted=0;
                todayDelivered=0;
                totalDelivered=0;
                todaySale=0.0;
                totalSale=0.0;

                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                        OrderModel orderModel = dataSnapshot1.getValue(OrderModel.class);
                        if (orderModel != null) {
                            orderModels.add(orderModel);
                            String[] dateOrderSplit=orderModel.getOrderDate().split("-");
                            int orderDate=Integer.parseInt(dateOrderSplit[0]);
                            int orderMonth=Integer.parseInt(dateOrderSplit[1]);
                            int orderYear=Integer.parseInt(dateOrderSplit[2]);
                            int acceptDate=0,acceptMonth=0,acceptYear=0;
                            if (orderModel.getAcceptedDate()!=null) {
                                String[] dateAcceptSplit = orderModel.getAcceptedDate().split("-");
                                acceptDate = Integer.parseInt(dateAcceptSplit[0]);
                                acceptMonth = Integer.parseInt(dateAcceptSplit[1]);
                                acceptYear = Integer.parseInt(dateAcceptSplit[2]);
                            }
                            int deliverDate=0,deliverMonth=0,deliverYear=0;
                            if (orderModel.getDeliveredDate()!=null) {
                                String[] dateDeliverSplit = orderModel.getDeliveredDate().split("-");
                                deliverDate = Integer.parseInt(dateDeliverSplit[0]);
                                deliverMonth = Integer.parseInt(dateDeliverSplit[1]);
                                deliverYear = Integer.parseInt(dateDeliverSplit[2]);
                            }
                            if (todayDate==orderDate && todayMonth==orderMonth) {
                                todayOrder++;
                            }
                            if (orderModel.getStatus().equals(Constants.ORDER_PENDING)) {
                                if (todayDate==orderDate && todayMonth==orderMonth && orderYear==todayYear) {
                                    todayPending++;
                                }
                                totalPending++;
                            }
                            if (orderModel.getStatus().equals(Constants.ORDER_ACCEPTED)) {
                                if (todayDate==acceptDate && todayMonth==acceptMonth && acceptYear==todayYear) {
                                    todayAccepted++;
                                }
                                totalAccepted++;
                            }
                            Log.d("dates",todayDate+" "+deliverDate+" "+todayMonth+" "+deliverMonth);
                            if (orderModel.getStatus().equals(Constants.ORDER_DELIVERED)) {
                                if (todayDate==deliverDate && todayMonth==deliverMonth && deliverYear==todayYear) {
                                    todayDelivered++;
                                    todaySale=totalSale+orderModel.getOrderTotal();
                                }
                                totalSale=totalSale+orderModel.getOrderTotal();
                                totalDelivered++;
                            }


                        }
                    }
                }
                binding.todayOrderText.setText(todayOrder+"");
                binding.totalOrderText.setText(orderModels.size()+"");
                binding.todaySale.setText(todaySale+"");
                binding.totalSale.setText(totalSale+"");
                binding.todayPending.setText(todayPending+"");
                binding.totalPending.setText(totalPending+"");
                binding.todayAccepted.setText(todayAccepted+"");
                binding.totalAccepted.setText(totalAccepted+"");
                binding.todayDelivered.setText(todayDelivered+"");
                binding.totalDelivered.setText(totalDelivered+"");

                binding.setLoading(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}