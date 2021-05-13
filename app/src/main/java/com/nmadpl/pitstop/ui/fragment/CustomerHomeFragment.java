package com.nmadpl.pitstop.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.ProductsAdapter;
import com.nmadpl.pitstop.controllers.adapters.ProductsCustomerAdapter;
import com.nmadpl.pitstop.controllers.listener.ItemSelectListener;
import com.nmadpl.pitstop.databinding.FragmentCustomerHomeBinding;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.nmadpl.pitstop.ui.activity.LoginActivity;

import java.util.ArrayList;


public class CustomerHomeFragment extends Fragment implements ItemSelectListener {
    private FragmentCustomerHomeBinding binding;
    private ArrayList<ProductModel> productModels=new ArrayList<>();
    private ArrayList<ProductModel> productModelsBackup=new ArrayList<>();
    private ArrayList<String> categories=new ArrayList<>();
    private CategoryDialog categoryDialog;
    private ProductsCustomerAdapter productsAdapter;
    public CustomerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_customer_home,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        productsAdapter=new ProductsCustomerAdapter(requireContext(),productModels);
        binding.productsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.productsRecycler.setAdapter(productsAdapter);

        binding.categorySelectButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog=new CategoryDialog(categories,CustomerHomeFragment.this);
                categoryDialog.show(requireActivity().getSupportFragmentManager(),"category");
            }
        });

        getProducts();
    }

    private void getProducts() {
        binding.setLoading(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productModels.clear();
                productModelsBackup.clear();
                categories.clear();
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
                binding.categorySelectButton2.setVisibility(View.VISIBLE);
                Log.d("categorySize",categories.size()+"");
                binding.setLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                String[] regex = s.toString().split(",");
                binding.setLoading(true);
                productModels.clear();
                // String searchTerm=binding.editTextTextPersonName.getText().toString().toLowerCase();
                if (s.toString().isEmpty()) {
                    binding.searchTermText2.setVisibility(View.GONE);
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
                        Log.d("regexSize", regex.length + "");
                        for (String s1 : regex) {
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
                    productsAdapter=new ProductsCustomerAdapter(requireContext(),productModels);
                    binding.productsRecycler.setAdapter(productsAdapter);
                    binding.searchTermText2.setText("Found " + productModels.size() + " Items");
                    binding.searchTermText2.setVisibility(View.VISIBLE);
                    productsAdapter.notifyDataSetChanged();
                    binding.searchTermText2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (category==null) {
                                binding.searchTermText2.setVisibility(View.GONE);
                                productModels.clear();
                                productModels.addAll(productModelsBackup);
                            }
                            else {
                                binding.searchTermText2.setVisibility(View.GONE);
                                productModels.clear();
                                Log.d("productSize",productModelsBackup.size()+"");
                                for (ProductModel productModel:productModelsBackup) {
                                    if (productModel.getTypeName().trim().toLowerCase().equals(category.toLowerCase().trim())){
                                        productModels.add(productModel);
                                    }
                                }

                            }
                            productsAdapter=new ProductsCustomerAdapter(requireContext(),productModels);
                            binding.productsRecycler.setAdapter(productsAdapter);
                        }
                    });
                }
                binding.setLoading(false);
            }
        });
    }
    private String category=null;
    @Override
    public void selectedItem(int position) {
        binding.setLoading(true);
        binding.categorySelectButton2.setText("Showing: "+categories.get(position));
        categoryDialog.dismiss();
        productModels.clear();
        category=categories.get(position);
        for (ProductModel productModel:productModelsBackup) {
            if (productModel.getTypeName().trim().toLowerCase().equals(categories.get(position).trim().toLowerCase())){
                productModels.add(productModel);
            }
        }
        productsAdapter=new ProductsCustomerAdapter(requireContext(),productModels);
        binding.productsRecycler.setAdapter(productsAdapter);
        binding.categoryCloseButton.setVisibility(View.VISIBLE);

        binding.categoryCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productModels.clear();
                productModels.addAll(productModelsBackup);
                productsAdapter=new ProductsCustomerAdapter(requireContext(),productModels);
                binding.productsRecycler.setAdapter(productsAdapter);
                binding.categorySelectButton2.setText("Select Category");
                binding.categoryCloseButton.setVisibility(View.GONE);
                category=null;
            }
        });
        binding.setLoading(false);


    }
}