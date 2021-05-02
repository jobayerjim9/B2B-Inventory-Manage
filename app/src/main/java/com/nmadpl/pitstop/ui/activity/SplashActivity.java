package com.nmadpl.pitstop.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.models.Constants;
import com.nmadpl.pitstop.models.FirebaseConstants;
import com.nmadpl.pitstop.models.UserDetail;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(FirebaseConstants.USER_PATH).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetail userDetail=snapshot.getValue(UserDetail.class);
                    if (userDetail!=null) {
                        if (userDetail.getType().equals(Constants.CUSTOMER_TYPE)) {
                            startActivity(new Intent(SplashActivity.this,CustomerActivity.class));
                        }
                        else if (userDetail.getType().equals(Constants.OWNER_TYPE)) {
                            startActivity(new Intent(SplashActivity.this,OwnerActivity.class));
                        }
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
    }
}