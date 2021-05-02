package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.ProductsCustomerAdapter;
import com.nmadpl.pitstop.databinding.ActivityCompanyProductBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;

import java.util.ArrayList;

public class CompanyProductActivity extends AppCompatActivity {
    private ActivityCompanyProductBinding binding;
    private ArrayList<ProductModel> productModels=new ArrayList<>();
    private ProductsCustomerAdapter productsCustomerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_company_product);
        initView();
    }
    CompanyModel companyModel;
    private void initView() {
        companyModel=(CompanyModel) getIntent().getSerializableExtra("item");
        binding.companyName.setText("Showing Company "+companyModel.getMfgCode()+" Products");
        binding.companyProductRecycler.setLayoutManager(new LinearLayoutManager(this));
        productsCustomerAdapter=new ProductsCustomerAdapter(this,productModels);
        binding.companyProductRecycler.setAdapter(productsCustomerAdapter);
        getCompanyProducts();

    }

    private void getCompanyProducts() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productModels.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                     if (productModel != null) {
                         Log.d("mfgCode",productModel.getMfgCode()+" "+companyModel.getMfgCode());

                         if (productModel.getMfgCode().toLowerCase().trim().equals(companyModel.getMfgCode().toLowerCase().trim())) {
                             productModels.add(productModel);
                            productsCustomerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}