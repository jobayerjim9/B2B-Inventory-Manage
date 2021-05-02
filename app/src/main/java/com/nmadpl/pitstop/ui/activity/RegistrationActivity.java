package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.databinding.ActivityRegistrationBinding;
import com.nmadpl.pitstop.models.RegistrationModel;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_registration);
        initView();
    }

    private void initView() {
        RegistrationModel registrationModel=new RegistrationModel();
        binding.setData(registrationModel);
        firebaseAuth=FirebaseAuth.getInstance();
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.ccp.registerCarrierNumberEditText(binding.phoneNumberInput.getEditText());
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private void validateData() {
        if (binding.getData().getFullName().isEmpty()) {
            binding.fullNameInput.setError("Enter Your Full Name");
            binding.fullNameInput.setErrorEnabled(true);
        }
        else if (binding.getData().getEmail().isEmpty()) {
            binding.emailInput.setError("Enter Your Email");
            binding.emailInput.setErrorEnabled(true);
        }
        else if (binding.getData().getFirmName().isEmpty()) {
            binding.firmName.setError("Enter Your Firm Name");
            binding.firmName.setErrorEnabled(true);
        }
        else if (binding.getData().getCity().isEmpty()) {
            binding.cityInput.setError("Enter Your City Name");
            binding.cityInput.setErrorEnabled(true);
        }
        else if (binding.getData().getTransport().isEmpty()) {
            binding.transPortName.setError("Enter Transport Name");
            binding.transPortName.setErrorEnabled(true);
        }
        else if (binding.getData().getPassword().isEmpty()) {
            binding.passwordInput.setError("Enter Password");
            binding.passwordInput.setErrorEnabled(true);
        }
        else if (binding.getData().getConfirmPassword().isEmpty()) {
            binding.confirmPasswordInput.setError("Confirm Password");
            binding.confirmPasswordInput.setErrorEnabled(true);
        }
        else if (!binding.getData().getPassword().equals(binding.getData().getConfirmPassword()))
        {
            binding.confirmPasswordInput.setError("Password Doesn't Match");
            binding.confirmPasswordInput.setErrorEnabled(true);
        }
        else if (binding.getData().getPhone().isEmpty()) {
            binding.phoneNumberInput.setError("Enter A Valid Phone Number");
            binding.phoneNumberInput.setErrorEnabled(true);;
        }
        else if (!binding.ccp.isValidFullNumber()) {
            binding.phoneNumberInput.setError("Enter A Valid Phone Number");
            binding.phoneNumberInput.setErrorEnabled(true);
        }
        else {
            binding.setLoading(true);
            binding.getData().setPhone(binding.ccp.getFullNumber());
            binding.fullNameInput.setErrorEnabled(false);
            binding.emailInput.setErrorEnabled(false);
            binding.confirmPasswordInput.setErrorEnabled(false);
            binding.passwordInput.setErrorEnabled(false);
            binding.phoneNumberInput.setErrorEnabled(false);
            binding.firmName.setErrorEnabled(false);
            binding.transPortName.setErrorEnabled(false);
            binding.cityInput.setErrorEnabled(false);
            createAuth();

        }



    }

    private void createAuth() {
        firebaseAuth.createUserWithEmailAndPassword(binding.getData().getEmail(),binding.getData().getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            storeToDatabase(firebaseAuth.getCurrentUser().getUid());
                        }
                        else {
                            binding.setLoading(false);
                            Toast.makeText(RegistrationActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void storeToDatabase(String uid) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("userDetails").child(uid);
        databaseReference.setValue(binding.getData()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                binding.setLoading(false);
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    finish();
                    Toast.makeText(RegistrationActivity.this, "Registration Complete!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegistrationActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}