package com.nmadpl.pitstop.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityCustomerBinding;

public class CustomerActivity extends AppCompatActivity {
    private ActivityCustomerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_customer);
        initView();
    }

    private void initView() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.customer_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavigationView2, navHostFragment.getNavController());


    }
}