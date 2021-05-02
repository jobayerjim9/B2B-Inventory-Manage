package com.nmadpl.pitstop.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ItemProductsBinding;
import com.nmadpl.pitstop.databinding.ItemProductsCustomerBinding;
import com.nmadpl.pitstop.models.CartModel;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.nmadpl.pitstop.ui.activity.ProductEditActivity;
import com.nmadpl.pitstop.ui.activity.ProductViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductsCustomerAdapter extends RecyclerView.Adapter<ProductsCustomerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ProductModel> productModels;

    public ProductsCustomerAdapter(Context context, ArrayList<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_products_customer,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ProductModel productModel=productModels.get(position);
        holder.binding.setData(productModel);
        holder.binding.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(productModel);
            }
        });
        if (productModel.getImages().size()>0) {
            Picasso.get().load(productModel.getImages().get(0)).placeholder(R.drawable.loading).into(holder.binding.productImage);
        }
        holder.binding.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProductViewActivity.class);
                intent.putExtra("item",productModel);
                context.startActivity(intent);
            }
        });
        holder.binding.executePendingBindings();
    }
    private void addToCart(ProductModel productModel) {
        CartModel cartModel=new CartModel();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.CART_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(productModel.getItemCode());
        cartModel.setProductId(productModel.getItemCode());
        cartModel.setTotalPrice((Double.parseDouble(productModel.getRate())*cartModel.getQty()));
        cartModel.setProductName(productModel.getItemName());
        cartModel.setMfgCode(productModel.getMfgCode());
        if (productModel.getImages().size()>0) {
            cartModel.setThumbNail(productModel.getImages().get(0));
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CartModel model=snapshot.getValue(CartModel.class);
                    if (model!=null) {
                        cartModel.setQty((cartModel.getQty()+model.getQty()));
                        cartModel.setTotalPrice((cartModel.getTotalPrice()+model.getTotalPrice()));
                        databaseReference.setValue(cartModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Cart Added!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else {
                    databaseReference.setValue(cartModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Cart Added!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
        return productModels.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductsCustomerBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
