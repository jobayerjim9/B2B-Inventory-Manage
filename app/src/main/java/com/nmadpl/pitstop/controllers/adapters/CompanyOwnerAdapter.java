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
import com.nmadpl.pitstop.databinding.ItemCompanyBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.ui.activity.CompanyEditActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CompanyOwnerAdapter extends RecyclerView.Adapter<CompanyOwnerAdapter.ViewHolder>{
    private Context context;
    private ArrayList<CompanyModel> companyModels;

    public CompanyOwnerAdapter(Context context, ArrayList<CompanyModel> companyModels) {
        this.context = context;
        this.companyModels = companyModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_company,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CompanyModel companyModel=companyModels.get(position);
        holder.binding.setData(companyModel);
        Picasso.get().load(companyModel.getLogo()).into(holder.binding.productImage);
        holder.binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CompanyEditActivity.class);
                intent.putExtra("item",companyModel);
                context.startActivity(intent);
            }
        });
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return companyModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemCompanyBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
