package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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
import com.nmadpl.pitstop.databinding.ActivityCompanyEditBinding;
import com.nmadpl.pitstop.databinding.ActivityProductEditBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CompanyEditActivity extends AppCompatActivity {
    private ActivityCompanyEditBinding binding;
    final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    CompanyModel companyModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_company_edit);
        initView();
    }

    private void initView() {
        companyModel=(CompanyModel) getIntent().getSerializableExtra("item");
        binding.mfgCodeInput.getEditText().setText(companyModel.getMfgCode());
        binding.rankInput.getEditText().setText(companyModel.getRank()+"");
        Picasso.get().load(companyModel.getLogo()).into(binding.imageView);
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CompanyEditActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CompanyEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CompanyEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(CompanyEditActivity.this, permissions, 1);
                } else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyModel.setRank(Integer.parseInt(binding.rankInput.getEditText().getText().toString()));
                if (bitmap!=null) {
                    uploadCompany(companyModel);
                }
                else {
                    createCompany(companyModel);
                }
            }
        });
        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference(FirebaseConstants.COMPANY_PATH).child(companyModel.getMfgCode());
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CompanyEditActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }
    Bitmap bitmap;
    private void uploadCompany(CompanyModel companyModel) {
        binding.setLoading(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storage = FirebaseStorage.getInstance().getReference("companyImage").child(companyModel.getMfgCode()+".jpg");
        storage.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            companyModel.setLogo(uri.toString());
                            createCompany(companyModel);
                        }

                    });
                }
            }
        });
    }

    private void createCompany(CompanyModel companyModel) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.COMPANY_PATH).child(companyModel.getMfgCode());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(companyModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.setLoading(false);
                            if (task.isSuccessful()) {

                                Toast.makeText(CompanyEditActivity.this, "Company Created Successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                    uri = getImageUri(CompanyEditActivity.this, imageBitmap);
                }
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                if (requestCode==1) {
                    binding.imageView.setImageBitmap(bitmap);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}