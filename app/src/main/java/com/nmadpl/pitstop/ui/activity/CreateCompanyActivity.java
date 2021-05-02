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
import com.nmadpl.pitstop.databinding.ActivityCreateCompanyBinding;
import com.nmadpl.pitstop.models.CompanyModel;
import com.nmadpl.pitstop.models.FirebaseConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateCompanyActivity extends AppCompatActivity {
    ActivityCreateCompanyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_create_company);
        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CreateCompanyActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CreateCompanyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CreateCompanyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(CreateCompanyActivity.this, permissions, 1);
                } else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
            }
        });
        binding.setLoading(false);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.mfgCodeInput.getEditText().getText().toString().isEmpty()) {
                    binding.mfgCodeInput.setError("Enter Mfg Code");
                    binding.mfgCodeInput.setErrorEnabled(true);
                }
                else if (binding.rankInput.getEditText().getText().toString().isEmpty()) {
                    binding.rankInput.setError("Enter Rank");
                    binding.rankInput.setErrorEnabled(true);
                }
                else if (bitmap==null) {
                    Toast.makeText(CreateCompanyActivity.this, "Select Company Logo", Toast.LENGTH_SHORT).show();
                }
                else {
                    CompanyModel companyModel=new CompanyModel(binding.mfgCodeInput.getEditText().getText().toString(),Integer.parseInt(binding.rankInput.getEditText().getText().toString()));
                    uploadCompany(companyModel);
                }
            }
        });
    }

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
                if (!snapshot.exists()) {
                    databaseReference.setValue(companyModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            binding.setLoading(false);
                            if (task.isSuccessful()) {

                                Toast.makeText(CreateCompanyActivity.this, "Company Created Successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }
                    });
                }
                else {
                    binding.setLoading(false);
                    binding.mfgCodeInput.setError("This Code is already exist");
                    binding.mfgCodeInput.setErrorEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    Bitmap bitmap;
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
                    uri = getImageUri(CreateCompanyActivity.this, imageBitmap);
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