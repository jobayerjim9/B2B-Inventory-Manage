package com.nmadpl.pitstop.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.airbnb.lottie.L;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.controllers.adapters.CompanyCustomerAdapter;
import com.nmadpl.pitstop.databinding.FragmentProfileBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.nmadpl.pitstop.models.UserDetail;
import com.nmadpl.pitstop.ui.activity.LoginActivity;
import com.nmadpl.pitstop.ui.activity.ProductViewActivity;

import org.eazegraph.lib.models.PieModel;
import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ArrayList<ProductModel> featuredProducts=new ArrayList<>();
    List<CarouselItem> carouselItems = new ArrayList<>();
    private ArrayList<CompanyModel> companyModels=new ArrayList<>();
    private CompanyCustomerAdapter companyCustomerAdapter;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        companyCustomerAdapter=new CompanyCustomerAdapter(requireContext(),companyModels);
        binding.companyRecycler.setLayoutManager(new GridLayoutManager(requireContext(),3));
        binding.companyRecycler.setAdapter(companyCustomerAdapter);
        getInfo();

    }

    private void getInfo() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.ITEM_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final long ordered=snapshot.getChildrenCount();
                binding.piechart.addPieSlice(new PieModel(
                        "Ordered Item",
                        ordered,
                        Color.parseColor("#FFA726")
                ));
                binding.totalOrder.setText("Total Order: "+snapshot.getChildrenCount());
                DatabaseReference products=FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH);
                products.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        featuredProducts.clear();
                        carouselItems.clear();
                        binding.piechart.addPieSlice(new PieModel(
                                "Total Item",
                                (snapshot.getChildrenCount()-ordered),
                                Color.parseColor("#66BB6A")
                        ));
                        binding.totalItem.setText("Total Item: "+snapshot.getChildrenCount());
                        binding.piechart.startAnimation();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            ProductModel productModel=dataSnapshot.getValue(ProductModel.class);
                            if (productModel!=null) {
                                if (productModel.isFeatured()) {
                                    binding.carousel.setVisibility(View.VISIBLE);
                                    featuredProducts.add(productModel);
                                    carouselItems.add(new CarouselItem(
                                       productModel.getImages().get(0),
                                       productModel.getItemName()
                                    ));
                                }
                            }
                        }
                        binding.carousel.addData(carouselItems);
                        binding.carousel.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onClick(int i, CarouselItem carouselItem) {
                                Intent intent=new Intent(requireContext(), ProductViewActivity.class);
                                intent.putExtra("item",featuredProducts.get(i));
                                startActivity(intent);
                            }

                            @Override
                            public void onLongClick(int i, CarouselItem carouselItem) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.menuButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(requireContext(),v);
                popupMenu.getMenuInflater().inflate(R.menu.logout_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                        requireActivity().finish();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        DatabaseReference user=FirebaseDatabase.getInstance().getReference(FirebaseConstants.USER_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetail userDetail=snapshot.getValue(UserDetail.class);
                if (userDetail!=null) {
                    binding.nameText.setText("Hello, "+userDetail.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference company=FirebaseDatabase.getInstance().getReference(FirebaseConstants.COMPANY_PATH);
        company.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companyModels.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    CompanyModel companyModel=dataSnapshot.getValue(CompanyModel.class);
                    if (companyModel!=null) {
                        Log.d("company",companyModel.getMfgCode());
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
                companyCustomerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}