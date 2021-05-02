package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityLoginBinding;
import com.nmadpl.pitstop.models.Constants;
import com.nmadpl.pitstop.models.LoginModel;
import com.nmadpl.pitstop.models.UserDetail;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login);
        initView();
    }

    private void initView() {
        LoginModel loginModel=new LoginModel();
        binding.setData(loginModel);
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {
        if (binding.getData().getEmail().isEmpty()){
            binding.emailInput.setError("Enter Your Registered Email");
            binding.emailInput.setEnabled(true);
        }
        else if (binding.getData().getPassword().isEmpty()) {
            binding.passwordInput.setError("Enter Your Password");
            binding.passwordInput.setEnabled(true);
        }
        else {
            login();
        }
    }
    FirebaseAuth firebaseAuth;
    private void login() {
        firebaseAuth=FirebaseAuth.getInstance();
        binding.setLoading(true);
        firebaseAuth.signInWithEmailAndPassword(binding.getData().getEmail(),binding.getData().getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            getDetails();
                            Toast.makeText(LoginActivity.this, "Login Complete", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            binding.setLoading(false);
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getDetails() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("userDetails").child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetail userDetail=snapshot.getValue(UserDetail.class);
                binding.setLoading(false);
                if (userDetail!=null) {
                    if (userDetail.getType().equals(Constants.CUSTOMER_TYPE)) {
                        startActivity(new Intent(LoginActivity.this,CustomerActivity.class));
                    }
                    else {
                        startActivity(new Intent(LoginActivity.this,OwnerActivity.class));
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}