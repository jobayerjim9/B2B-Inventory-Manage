package com.nmadpl.pitstop.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.CartAdapter;
import com.nmadpl.pitstop.databinding.FragmentCartBinding;
import com.nmadpl.pitstop.models.CartModel;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.OrderModel;
import com.nmadpl.pitstop.models.UserDetail;

import java.util.ArrayList;
import java.util.Date;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private ArrayList<CartModel> cartModels=new ArrayList<>();
    private CartAdapter cartAdapter;
    private OrderModel orderModel;
    public CartFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_cart,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        orderModel=new OrderModel();
        binding.setData(orderModel);
        binding.getData().setOrderBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
        cartAdapter=new CartAdapter(requireContext(),cartModels);
        binding.cartRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.cartRecycler.setAdapter(cartAdapter);
        getCartItems();
        binding.placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getData().getTransportName().isEmpty()) {
                    binding.firmNameInput.setErrorEnabled(false);
                    binding.firmNameInput.setError("Enter Transport Name");
                }
                else {
                    binding.firmNameInput.setErrorEnabled(false);
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Are you sure?")
                            .setMessage("Placing Order Cannot Be Undone")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    orderPlace();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }

            }
        });

    }

    private void orderPlace() {
        binding.setLoading(true);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(FirebaseConstants.ORDER_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference cartRef=FirebaseDatabase.getInstance().getReference(FirebaseConstants.CART_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String orderId=databaseReference.push().getKey();
        Date today=new Date();
        String placeHolder=today.getDate()+"-"+(today.getMonth()+1)+"-"+(today.getYear()+1900);
        binding.getData().setOrderDate(placeHolder);
        binding.getData().setOrderId(orderId);
        databaseReference.child(orderId).setValue(orderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    cartRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                binding.setLoading(false);
                                Toast.makeText(requireContext(), "Order Placed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        for (OrderModel.OrderItem orderItem:orderModel.getOrderItems()) {
            DatabaseReference items=FirebaseDatabase.getInstance().getReference(FirebaseConstants.ITEM_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(orderItem.getItemId());
            items.child("ordered").setValue(true);
        }
    }

    private void getCartItems() {
        binding.setLoading(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.CART_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference user=FirebaseDatabase.getInstance().getReference(FirebaseConstants.USER_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        binding.setLoading(false);
                        UserDetail userDetail=snapshot.getValue(UserDetail.class);
                        if (userDetail!=null)
                        {
                            Log.d("transportName",userDetail.getTransport());
                            binding.firmNameInput.getEditText().setText(userDetail.getTransport());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (!snapshot.exists()) {
                    Log.d("notExist","not");
                    binding.itemsCart.setVisibility(View.GONE);
                    binding.noItem.setVisibility(View.VISIBLE);
                }
                else {
                    binding.itemsCart.setVisibility(View.VISIBLE);
                    binding.noItem.setVisibility(View.GONE);
                    cartModels.clear();
                    binding.getData().getOrderItems().clear();
                    double total=0.0;
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        CartModel cartModel=dataSnapshot.getValue(CartModel.class);
                        if (cartModel!=null) {
                            total=total+cartModel.getTotalPrice();
                            cartModels.add(cartModel);
                            binding.getData().getOrderItems().add(new OrderModel.OrderItem(cartModel.getProductId(),cartModel.getProductName(),cartModel.getMfgCode(),cartModel.getQty(),cartModel.getTotalPrice()));


                        }
                    }
                    cartAdapter=new CartAdapter(context,cartModels);
                    binding.cartRecycler.setAdapter(cartAdapter);
                    binding.getData().setOrderTotal(total);
                    binding.totalItem.setText("Total Item: "+cartModels.size());
                    String place="Total: "+total+"/-";
                    binding.totalPrice.setText(place);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }
}