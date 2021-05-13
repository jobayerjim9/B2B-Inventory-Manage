package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.ProductsAdapter;
import com.nmadpl.pitstop.controllers.adapters.ProductsCustomerAdapter;
import com.nmadpl.pitstop.controllers.listener.ItemSelectListener;
import com.nmadpl.pitstop.databinding.ActivityCompanyProductBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.nmadpl.pitstop.ui.fragment.CategoryDialog;
import com.nmadpl.pitstop.ui.fragment.ProductsFragment;

import java.util.ArrayList;

public class CompanyProductActivity extends AppCompatActivity implements ItemSelectListener {
    private ActivityCompanyProductBinding binding;
    private ArrayList<ProductModel> productModels = new ArrayList<>();
    private ProductsCustomerAdapter productsCustomerAdapter;
    private ArrayList<ProductModel> productModelsBackup = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private CategoryDialog categoryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_company_product);
        initView();
    }

    CompanyModel companyModel;

    private void initView() {
        companyModel = (CompanyModel) getIntent().getSerializableExtra("item");
        binding.companyName.setText("Showing Company " + companyModel.getMfgCode() + " Products");
        binding.companyProductRecycler.setLayoutManager(new LinearLayoutManager(this));
        productsCustomerAdapter = new ProductsCustomerAdapter(this, productModels);
        binding.companyProductRecycler.setAdapter(productsCustomerAdapter);
        binding.categorySelectButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog = new CategoryDialog(categories, CompanyProductActivity.this);
                categoryDialog.show(CompanyProductActivity.this.getSupportFragmentManager(), "category");
            }
        });
        getCompanyProducts();
        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] regrex = s.toString().split(",");
                binding.setLoading(true);
                productModels.clear();
                // String searchTerm=binding.editTextTextPersonName.getText().toString().toLowerCase();
                if (s.toString().isEmpty()) {
                    binding.searchTermText5.setVisibility(View.GONE);
                    if (category != null) {
                        for (ProductModel productModel : productModelsBackup) {
                            if (productModel.getTypeName().trim().toLowerCase().equals(category.trim().toLowerCase())) {
                                productModels.add(productModel);
                            }
                        }
                        productsCustomerAdapter.notifyDataSetChanged();
                    } else {
                        productModels.addAll(productModelsBackup);
                        productsCustomerAdapter.notifyDataSetChanged();
                    }
                } else {
                    for (ProductModel productModel : productModelsBackup) {
                        Log.d("regexSize", regrex.length + "");
                        for (String s1 : regrex) {
                            String[] split = s1.toLowerCase().split(" ");
                            boolean found = false;
                            for (String t : split) {
                                if (productModel.getDescription().toLowerCase().trim().contains(t) || productModel.getItemName().toLowerCase().contains(t) || productModel.getMfgCode().toLowerCase().contains(s1.toLowerCase().trim())) {
                                    found = true;
                                } else {
                                    found = false;
                                    break;
                                }
                            }
                            if (category != null && found) {
                                if (category.toLowerCase().equals(productModel.getTypeName().toLowerCase())) {
                                    productModels.add(productModel);
                                }
                            } else if (found) {
                                productModels.add(productModel);
                            }
//                                if (productModel.getDescription().toLowerCase().trim().matches(s1.toLowerCase().trim()) || productModel.getItemName().toLowerCase().contains(s1.toLowerCase().trim()) || productModel.getMfgCode().toLowerCase().contains(s1.toLowerCase().trim())) {
//                                    productModels.add(productModel);
//                                }

                        }
                    }
                    productsCustomerAdapter = new ProductsCustomerAdapter(CompanyProductActivity.this, productModels);
                    binding.companyProductRecycler.setAdapter(productsCustomerAdapter);
                    binding.searchTermText5.setText("Found " + productModels.size() + " Items");
                    binding.searchTermText5.setVisibility(View.VISIBLE);
                    productsCustomerAdapter.notifyDataSetChanged();
                    binding.searchTermText5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.searchText.setText("");
                        }
                    });
                }
                binding.setLoading(false);
            }
        });
    }

    private void getCompanyProducts() {
        binding.setLoading(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productModels.clear();
                categories.clear();
                productModelsBackup.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                    if (productModel != null) {
                        Log.d("mfgCode", productModel.getMfgCode() + " " + companyModel.getMfgCode());
                        if (productModel.getMfgCode().toLowerCase().trim().equals(companyModel.getMfgCode().toLowerCase().trim())) {
                            productModels.add(productModel);
                            productsCustomerAdapter.notifyDataSetChanged();
                        }
                    }
                }
                productModelsBackup.addAll(productModels);
                for (ProductModel productModel : productModels) {
                    boolean found = false;
                    for (int i = 0; i < categories.size(); i++) {
                        if (productModel.getTypeName().toLowerCase().trim().equals(categories.get(i).toLowerCase().trim())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        categories.add(productModel.getTypeName());
                    }
                }
                binding.setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String category = null;

    @Override
    public void selectedItem(int position) {
        binding.setLoading(true);
        binding.categorySelectButton3.setText("Showing: " + categories.get(position));
        categoryDialog.dismiss();
        productModels.clear();
        category = categories.get(position);
        for (ProductModel productModel : productModelsBackup) {
            if (productModel.getTypeName().trim().toLowerCase().equals(categories.get(position).trim().toLowerCase())) {
                productModels.add(productModel);
            }
        }
        productsCustomerAdapter = new ProductsCustomerAdapter(CompanyProductActivity.this, productModels);
        binding.companyProductRecycler.setAdapter(productsCustomerAdapter);

        binding.setLoading(false);


    }
}