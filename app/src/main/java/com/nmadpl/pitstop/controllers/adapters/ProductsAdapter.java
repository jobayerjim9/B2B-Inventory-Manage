package com.nmadpl.pitstop.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ItemProductsBinding;
import com.nmadpl.pitstop.models.ProductModel;
import com.nmadpl.pitstop.ui.activity.ProductEditActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ProductModel> productModels;

    public ProductsAdapter(Context context, ArrayList<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_products,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ProductModel productModel=productModels.get(position);
        holder.binding.setData(productModel);
        holder.binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProductEditActivity.class);
                intent.putExtra("item",productModel);
                context.startActivity(intent);
            }
        });
        if (productModel.getImages().size()>0) {
            Picasso.get().load(productModel.getImages().get(0)).placeholder(R.drawable.loading).into(holder.binding.productImage);
        }
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
        return productModels.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductsBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
