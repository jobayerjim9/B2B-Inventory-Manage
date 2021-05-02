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
import com.nmadpl.pitstop.databinding.ItemCompanyCustomerBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.ui.activity.CompanyProductActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CompanyCustomerAdapter extends RecyclerView.Adapter<CompanyCustomerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CompanyModel> companyModels;

    public CompanyCustomerAdapter(Context context, ArrayList<CompanyModel> companyModels) {
        this.context = context;
        this.companyModels = companyModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_company_customer,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(companyModels.get(position).getLogo()).into(holder.binding.companyImage);
        holder.binding.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CompanyProductActivity.class);
                intent.putExtra("item",companyModels.get(position));
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return companyModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemCompanyCustomerBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
    }
}
