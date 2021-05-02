package com.nmadpl.pitstop.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.ProductsAdapter;
import com.nmadpl.pitstop.controllers.listener.ItemSelectListener;
import com.nmadpl.pitstop.databinding.FragmentProductsBinding;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;

import java.util.ArrayList;

public class ProductsFragment extends Fragment implements ItemSelectListener {
    private FragmentProductsBinding binding;
    private ArrayList<ProductModel> productModels=new ArrayList<>();
    private ArrayList<ProductModel> productModelsBackup=new ArrayList<>();
    private ArrayList<String> categories=new ArrayList<>();
    private CategoryDialog categoryDialog;
    private ProductsAdapter productsAdapter;
    public ProductsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_products,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        productsAdapter=new ProductsAdapter(requireContext(),productModels);
        binding.productsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.productsRecycler.setAdapter(productsAdapter);
        getProducts();
        binding.categorySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog=new CategoryDialog(categories,ProductsFragment.this);
                categoryDialog.show(requireActivity().getSupportFragmentManager(),"category");
            }
        });
        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.setLoading(true);
                productModels.clear();
                // String searchTerm=binding.editTextTextPersonName.getText().toString().toLowerCase();
                if (s.toString().isEmpty()) {
                    binding.searchTermText.setVisibility(View.GONE);
                    if (category!=null) {
                        for (ProductModel productModel:productModelsBackup) {
                            if (productModel.getTypeName().trim().toLowerCase().equals(category.trim().toLowerCase())){
                                productModels.add(productModel);
                            }
                        }
                        productsAdapter.notifyDataSetChanged();
                    }
                    else {
                        productModels.addAll(productModelsBackup);
                        productsAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    for (ProductModel productModel : productModelsBackup) {
                        if (category != null) {
                            if (productModel.getDescription().toLowerCase().contains(s.toString().toLowerCase().trim()) || productModel.getItemName().toLowerCase().contains(s.toString().toLowerCase().trim()) || productModel.getMfgCode().toLowerCase().contains(s.toString().toLowerCase().trim())) {
                                if (category.toLowerCase().equals(productModel.getTypeName().toLowerCase())) {
                                    productModels.add(productModel);
                                }
                            }
                        } else {
                            if (productModel.getDescription().toLowerCase().contains(s.toString().toLowerCase().trim()) || productModel.getItemName().toLowerCase().contains(s.toString().toLowerCase().trim()) || productModel.getMfgCode().toLowerCase().contains(s.toString().toLowerCase().trim())) {
                                productModels.add(productModel);
                            }
                        }
                    }
                    productsAdapter=new ProductsAdapter(requireContext(),productModels);
                    binding.productsRecycler.setAdapter(productsAdapter);
                    binding.searchTermText.setText("Found " + productModels.size() + " Items");
                    binding.searchTermText.setVisibility(View.VISIBLE);
                    productsAdapter.notifyDataSetChanged();
                    binding.searchTermText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (category==null) {
                                binding.searchTermText.setVisibility(View.GONE);
                                productModels.clear();
                                productModels.addAll(productModelsBackup);
                            }
                            else {
                                binding.searchTermText.setVisibility(View.GONE);
                                productModels.clear();
                                Log.d("productSize",productModelsBackup.size()+"");
                                for (ProductModel productModel:productModelsBackup) {
                                    if (productModel.getTypeName().trim().toLowerCase().equals(category.toLowerCase().trim())){
                                        productModels.add(productModel);
                                    }
                                }

                            }
                            productsAdapter=new ProductsAdapter(requireContext(),productModels);
                            binding.productsRecycler.setAdapter(productsAdapter);
                        }
                    });
                }
                binding.setLoading(false);
            }
        });
    }

    private void getProducts() {
        binding.setLoading(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productModels.clear();
                categories.clear();
                productModelsBackup.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    productModels.add(dataSnapshot.getValue(ProductModel.class));
                    productsAdapter.notifyDataSetChanged();
                }
                productModelsBackup.addAll(productModels);
                for (ProductModel productModel:productModels) {
                    boolean found=false;
                    for (int i=0;i<categories.size();i++) {
                        if (productModel.getTypeName().toLowerCase().trim().equals(categories.get(i).toLowerCase().trim())) {
                            found=true;
                            break;
                        }
                    }
                    if (!found) {
                        categories.add(productModel.getTypeName());
                    }
                }
                binding.categorySelectButton.setVisibility(View.VISIBLE);
                Log.d("categorySize",categories.size()+"");
                binding.setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private String category=null;
    @Override
    public void selectedItem(int position) {
        binding.setLoading(true);
        binding.categorySelectButton.setText("Showing: "+categories.get(position));
        categoryDialog.dismiss();
        productModels.clear();
        category=categories.get(position);
        for (ProductModel productModel:productModelsBackup) {
            if (productModel.getTypeName().trim().toLowerCase().equals(categories.get(position).trim().toLowerCase())){
                productModels.add(productModel);
            }
        }
        productsAdapter=new ProductsAdapter(requireContext(),productModels);
        binding.productsRecycler.setAdapter(productsAdapter);

        binding.setLoading(false);


    }
}