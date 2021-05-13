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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityCreateProductBinding;
import com.nmadpl.pitstop.databinding.ActivityProductEditBinding;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateProductActivity extends AppCompatActivity {
    private ActivityCreateProductBinding binding;
    private ProductModel productModel;
    final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private ArrayList<Bitmap> bitmaps=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_create_product);
        initView();

    }

    private void initView() {
        ProductModel productModel=new ProductModel();
        binding.setData(productModel);
        binding.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CreateProductActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CreateProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CreateProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(CreateProductActivity.this, permissions, 1);
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
                validateData();
             //   uploadImage(0);
            }
        });
        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.getData().setFeatured(isChecked);
            }
        });
    }

    private void validateData() {
        if (binding.getData().getItemName().isEmpty()) {
            binding.itemName.setError("Enter Item Name");
            binding.itemName.setErrorEnabled(true);
        }
        else if (binding.getData().getDescription().isEmpty()) {
            binding.itemDescription.setError("Enter Description");
            binding.itemDescription.setErrorEnabled(true);
        }
        else if (binding.getData().getItemCode().isEmpty()) {
            binding.itemCode.setError("Enter Item Code");
            binding.itemCode.setErrorEnabled(true);
        }
        else if (binding.getData().getMfgCode().isEmpty()) {
            binding.mfgCode.setError("Enter MFG Code");
            binding.mfgCode.setErrorEnabled(true);
        }
        else if (binding.getData().getModelName().isEmpty()) {
            binding.modelName.setError("Enter Model Name");
            binding.modelName.setErrorEnabled(true);
        }
        else if (binding.getData().getRate().isEmpty()) {
            binding.rate.setError("Enter Rate");
            binding.rate.setErrorEnabled(true);
        }
        else if (binding.getData().getTypeName().isEmpty()) {
            binding.typeName.setError("Enter Type Name");
            binding.typeName.setErrorEnabled(true);
        }
        else if (binding.getData().getUnit().isEmpty()) {
            binding.unit.setError("Enter Unit");
            binding.unit.setErrorEnabled(true);
        }
        else if (binding.getData().isFeatured() && bitmaps.size()==0) {
            Toast.makeText(this, "Feature Product Require At least 1 Image!", Toast.LENGTH_LONG).show();
        }
        else {
            binding.setLoading(true);
            binding.unit.setErrorEnabled(false);
            binding.typeName.setErrorEnabled(false);
            binding.rate.setErrorEnabled(false);
            binding.modelName.setErrorEnabled(false);
            binding.mfgCode.setErrorEnabled(false);
            binding.itemCode.setErrorEnabled(false);
            binding.itemDescription.setErrorEnabled(false);
            binding.itemName.setErrorEnabled(false);
            validateItemCode();
        }
    }

    private void validateItemCode() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH).child(binding.getData().getItemCode());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.setLoading(false);
                    binding.itemCode.setError("Code Already Exist");
                    binding.itemCode.setErrorEnabled(true);
                }
                else {
                    uploadImage(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void uploadImage(int index) {
        if (index>=bitmaps.size()) {
            createProduct();
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

    private void createProduct() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH).child(binding.getData().getItemCode());
        databaseReference.setValue(binding.getData()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.setLoading(false);
                    Toast.makeText(CreateProductActivity.this, "Product Created", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }


    private void selectImage(int req) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProductActivity.this);
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
                    uri = getImageUri(CreateProductActivity.this, imageBitmap);
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