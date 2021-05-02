package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityProductEditBinding;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProductEditActivity extends AppCompatActivity {
    private ActivityProductEditBinding binding;
    private ProductModel productModel;
    final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private ArrayList<Bitmap> bitmaps=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_product_edit);
        initView();

    }

    private void initView() {
        productModel=(ProductModel) getIntent().getSerializableExtra("item");
        binding.setData(productModel);
        if (productModel.getImages().size()>0) {
            if (productModel.getImages().size()>=1) {
                binding.imageView2.setVisibility(View.VISIBLE);
                Picasso.get().load(productModel.getImages().get(0)).placeholder(R.drawable.loading).into(binding.imageView1);
            }
            if (productModel.getImages().size()>=2) {
                binding.imageView3.setVisibility(View.VISIBLE);
                Picasso.get().load(productModel.getImages().get(1)).placeholder(R.drawable.loading).into(binding.imageView2);
            }
            if (productModel.getImages().size()>=3) {
                binding.imageView4.setVisibility(View.VISIBLE);
                Picasso.get().load(productModel.getImages().get(2)).placeholder(R.drawable.loading).into(binding.imageView3);
            }
            if (productModel.getImages().size()>=4) {

                Picasso.get().load(productModel.getImages().get(3)).placeholder(R.drawable.loading).into(binding.imageView4);
            }
        }
        binding.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProductEditActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ProductEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ProductEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(ProductEditActivity.this, permissions, 1);
                } else {
                    //pickImage(1);
                    selectImage(1);
                }
            }
        });
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(2);
            }
        });
        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(3);
            }
        });
        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(4);
            }
        });
        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("imageSize",bitmaps.size()+" "+binding.getData().getImages().size());
                if (binding.getData().isFeatured() && (bitmaps.size()==0 && binding.getData().getImages().size()==0)) {
                    Toast.makeText(ProductEditActivity.this, "Featured Product Require At least 1 Image", Toast.LENGTH_LONG).show();
                }
                else {
                    uploadImage(0);
                }
            }
        });
        binding.checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.getData().setFeatured(isChecked);
            }
        });
        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH).child(productModel.getItemCode());
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductEditActivity.this, "Item Deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void uploadImage(int index) {
        if (index>=bitmaps.size()) {
            updateData();
            return;
        }
        Bitmap bitmap = bitmaps.get(index);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storage = FirebaseStorage.getInstance().getReference("productImage").child(binding.getData().getItemCode()).child(index+".jpg");
        storage.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            binding.getData().getImages().add(index,uri.toString());
                            uploadImage(index+1);
                        }
                    });
                }
            }
        });
    }

    private void updateData() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH).child(binding.getData().getItemCode());
        databaseReference.setValue(binding.getData()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProductEditActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void selectImage(int req) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProductEditActivity.this);
        builder.setTitle("Choose product picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, req);
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, req);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();


                if (uri == null) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    uri = getImageUri(ProductEditActivity.this, imageBitmap);
                }
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmaps.add(bitmap);
                if (requestCode==1) {
                    binding.imageView1.setImageBitmap(bitmap);
                    binding.imageView2.setVisibility(View.VISIBLE);
                }
                else if (requestCode==2) {
                    binding.imageView2.setImageBitmap(bitmap);
                    binding.imageView3.setVisibility(View.VISIBLE);
                }
                else if (requestCode==3) {
                    binding.imageView3.setImageBitmap(bitmap);
                    binding.imageView4.setVisibility(View.VISIBLE);
                }
                else if (requestCode==4) {
                    binding.imageView4.setImageBitmap(bitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}