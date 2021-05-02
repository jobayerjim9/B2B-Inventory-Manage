package com.nmadpl.pitstop.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.CategoryAdapter;
import com.nmadpl.pitstop.controllers.listener.ItemSelectListener;
import com.nmadpl.pitstop.databinding.DialogCategoryBinding;

import java.util.ArrayList;

public class CategoryDialog extends DialogFragment {
    private ArrayList<String> categories;
    private ItemSelectListener listener;
    private DialogCategoryBinding binding;
    private CategoryAdapter categoryAdapter;
    public CategoryDialog(ArrayList<String> categories, ItemSelectListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder= new AlertDialog.Builder(requireContext());
        LayoutInflater inflater=requireActivity().getLayoutInflater();
        binding= DataBindingUtil.inflate(inflater, R.layout.dialog_category,null,false);

        initView();

        builder.setView(binding.getRoot());
        return builder.create();

    }

    private void initView() {
        binding.categoryRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter=new CategoryAdapter(requireContext(),categories,listener);
        binding.categoryRecycler.setAdapter(categoryAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
