package com.nmadpl.pitstop.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityOwnerBinding;
import com.nmadpl.pitstop.ui.fragment.ProductUploadDialog;

public class OwnerActivity extends AppCompatActivity {
    private ActivityOwnerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_owner);
        initView();
    }

    private void initView() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.getNavController());
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductUploadDialog productUploadDialog=new ProductUploadDialog();
                productUploadDialog.show(getSupportFragmentManager(),"productUpload");
            }
        });








    }
}