package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityProductViewBinding;
import com.nmadpl.pitstop.models.CartModel;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ProductViewActivity extends AppCompatActivity {
    private ActivityProductViewBinding binding;
    private ProductModel productModel;
    private CartModel cartModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_product_view);
        initView();
    }

    private void initView() {
        List<CarouselItem> list = new ArrayList<>();
        final ArrayList<Uri> images=new ArrayList<>();
        productModel=(ProductModel) getIntent().getSerializableExtra("item");
        binding.setData(productModel);
        for (String s:productModel.getImages()) {
            list.add(new CarouselItem(s,""));
            images.add(Uri.parse(s));
        }
        cartModel=new CartModel();
        binding.carousel.addData(list);
        binding.increamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartModel.setQty(cartModel.getQty()+1);
                binding.qty.setText(cartModel.getQty()+"");
            }
        });
        binding.decreamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModel.getQty()>1) {
                    cartModel.setQty(cartModel.getQty()-1);
                    binding.qty.setText(cartModel.getQty()+"");
                }
            }
        });
        binding.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
        binding.carousel.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int i, CarouselItem carouselItem) {

                new StfalconImageViewer.Builder<>(ProductViewActivity.this, images, new ImageLoader<Uri>() {
                    @Override
                    public void loadImage(ImageView imageView, Uri image) {
                        Picasso.get().load(image).into(imageView);
                    }
                }).withStartPosition(i).show();
            }

            @Override
            public void onLongClick(int i, CarouselItem carouselItem) {

            }
        });
        
    }

    private void addToCart() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.CART_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(binding.getData().getItemCode());
        cartModel.setProductId(binding.getData().getItemCode());
        cartModel.setProductName(binding.getData().getItemName());
        cartModel.setMfgCode(binding.getData().getMfgCode());
        if (binding.getData().getImages().size()>0) {
            cartModel.setThumbNail(binding.getData().getImages().get(0));
        }
        cartModel.setTotalPrice((Double.parseDouble(binding.getData().getRate())*cartModel.getQty()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CartModel model=snapshot.getValue(CartModel.class);
                    if (model!=null) {
                        cartModel.setQty((cartModel.getQty()+model.getQty()));
                        cartModel.setTotalPrice((cartModel.getTotalPrice()+model.getTotalPrice()));
                        databaseReference.setValue(cartModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProductViewActivity.this, "Cart Added!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                }
                else {
                    databaseReference.setValue(cartModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductViewActivity.this, "Cart Added!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}