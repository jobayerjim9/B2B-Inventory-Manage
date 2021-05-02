package com.nmadpl.pitstop.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.DialogProductUploadBinding;
import com.nmadpl.pitstop.ui.activity.CreateCompanyActivity;
import com.nmadpl.pitstop.ui.activity.CreateProductActivity;
import com.nmadpl.pitstop.ui.activity.ExcelProductUploadActivity;

public class ProductUploadDialog extends DialogFragment {
    private DialogProductUploadBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder= new AlertDialog.Builder(requireContext());
        LayoutInflater inflater=requireActivity().getLayoutInflater();
        binding= DataBindingUtil.inflate(inflater, R.layout.dialog_product_upload,null,false);
        binding.uploadFromExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ExcelProductUploadActivity.class));
                dismiss();
            }
        });
        binding.createSingleProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), CreateProductActivity.class));
                dismiss();
            }
        });
        binding.createCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), CreateCompanyActivity.class));
                dismiss();
            }
        });

        builder.setView(binding.getRoot());
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
