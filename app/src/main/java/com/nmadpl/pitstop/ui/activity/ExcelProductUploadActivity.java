package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityExcelProductUploadBinding;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.ProductModel;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelProductUploadActivity extends AppCompatActivity {
    private ActivityExcelProductUploadBinding binding;
    private boolean isUploading,cancelUpload;
    private ArrayList<ProductModel> productModels=new ArrayList<>();
    final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_excel_product_upload);
        initView();
    }

    private void initView() {

        if (ContextCompat.checkSelfPermission(ExcelProductUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ExcelProductUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ExcelProductUploadActivity.this, permissions, 1);
        }
        binding.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productModels.size()>0) {
                    new MaterialAlertDialogBuilder(ExcelProductUploadActivity.this)
                            .setTitle("Are You Sure?")
                            .setMessage("Uploading Product Cannot be undo.")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    binding.setLoading(true);
                                    binding.uploadStatusCard.setVisibility(View.VISIBLE);
                                    uploadProduct(0);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }
                else {
                    Toast.makeText(ExcelProductUploadActivity.this, "Selected File Doesn't Contain Any Product", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.chooseFileExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChooserDialog(ExcelProductUploadActivity.this)
                        .withFilter(false, false, "xls")
                        .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .withResources(R.string.title_choose_file, R.string.title_choose, R.string.dialog_cancel)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                String [] split=path.split("/");

                                binding.chooseFileExcel.setText("Choose: "+split[split.length-1]);
                                getProducts(pathFile);
                                //Toast.makeText(ExcelProductUploadActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build()
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isUploading) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Still Uploading!!")
                    .setMessage("Do you want to cancel?")
                    .setPositiveButton("Cancel Upload", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelUpload = true;
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    int duplicate=0;
    private ArrayList<ProductModel> failedItems=new ArrayList<>();
    private void uploadProduct(final int index) {
        if (cancelUpload) {
            return;
        }
        Log.d("indexOfUpload",index+"");
        if (index>=productModels.size()) {
            binding.setLoading(false);
            isUploading=false;
            return;
        }
        isUploading = true;
        String placeHolder = "Uploaded " + (index + 1) + "/" + productModels.size();
        binding.uploadTextProgress.setText(placeHolder);
        binding.uploadProgress.setProgress(index);
        placeHolder = "Duplicate " + duplicate;
        binding.duplicateText.setText(placeHolder);
        placeHolder = "Failed " + failedItems.size();
        binding.failedText.setText(placeHolder);
        final ProductModel productModel = productModels.get(index);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.PRODUCT_PATH).child(productModel.getItemCode());
        databaseReference.setValue(productModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                uploadProduct(index + 1);
                if (!task.isSuccessful()) {
                    failedItems.add(productModel);
                }
            }
        });


    }

    private void getProducts(File pathFile) {
        try {
            productModels.clear();
            FileInputStream fis = new FileInputStream(pathFile);
            Workbook workbook=Workbook.getWorkbook(fis);
            Sheet sheet=workbook.getSheet(0);
            int rows=sheet.getRows();
            binding.totalData.setText("Total Product: "+(rows-1));
            int column=sheet.getColumns();
            for (int i=1;i<rows;i++) {
                StringBuilder stringBuilder=new StringBuilder();
                String tag="Row "+i;
                productModels.add(new ProductModel(sheet.getCell(0,i).getContents(),sheet.getCell(1,i).getContents(),sheet.getCell(2,i).getContents(),sheet.getCell(3,i).getContents(),sheet.getCell(4,i).getContents(),sheet.getCell(5,i).getContents(),sheet.getCell(6,i).getContents(),sheet.getCell(7,i).getContents()));
            }
            binding.uploadProgress.setMax(productModels.size());
            Log.d("productSize",productModels.size()+"");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

    }
}