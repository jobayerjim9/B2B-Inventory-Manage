package com.nmadpl.pitstop.ui.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.SalesViewPagerAdapter;
import com.nmadpl.pitstop.databinding.FragmentSalesStatusBinding;

public class SalesStatusFragment extends Fragment {
    private FragmentSalesStatusBinding binding;
    public SalesStatusFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_sales_status,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        SalesViewPagerAdapter salesViewPagerAdapter=new SalesViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(salesViewPagerAdapter);
        binding.tabLayout2.setupWithViewPager(binding.viewPager);
    }
}