package com.nmadpl.pitstop.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.CompanyOwnerAdapter;
import com.nmadpl.pitstop.databinding.FragmentCompanyBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.models.FirebaseConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CompanyFragment extends Fragment {
    private FragmentCompanyBinding binding;
    private ArrayList<CompanyModel> companyModels=new ArrayList<>();
    private CompanyOwnerAdapter companyOwnerAdapter;
    public CompanyFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_company,container,false);
        initView();

        return binding.getRoot();
    }

    private void initView() {
        binding.companyRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        companyOwnerAdapter=new CompanyOwnerAdapter(requireContext(),companyModels);
        binding.companyRecycler.setAdapter(companyOwnerAdapter);
        getCompanyData();
    }

    private void getCompanyData() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.COMPANY_PATH);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companyModels.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    CompanyModel companyModel=dataSnapshot.getValue(CompanyModel.class);
                    if (companyModel!=null) {
                        companyModels.add(companyModel);
                    }
                }
                Collections.sort(companyModels, new Comparator<CompanyModel>() {
                    @Override
                    public int compare(CompanyModel o1, CompanyModel o2) {
                        if (o1.getRank()>o2.getRank()) {
                            return 1;
                        }
                        else if (o1.getRank()<o2.getRank()) {
                            return -1;
                        }
                        else {
                            return 0;
                        }
                    }
                });
                companyOwnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}