package com.cscorner.cse_489_sabit;


import com.cscorner.cse_489_sabit.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    private EditText etName, etEmail, etPhone,etPassword,etConfirmPassword;
    private CheckBox cbRememberUser,cbRememberLogin;
    private Button btnHaveAccount,btnSignup,btnExit;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent in = getIntent();
        String value = in.getStringExtra("key");

// Check if the retrieved value matches the expected value
        if (value != null && value.equalsIgnoreCase("value")) {
            // Stay on the Signup activity (since you are already there, no need to navigate)
        } else {
            // Retrieve SharedPreferences
            SharedPreferences sp = this.getSharedPreferences("my_sp", MODE_PRIVATE);
            String email = sp.getString("USER-EMAIL", "NOT-YET-CREATED");

            // Check if the email exists in SharedPreferences
            if (!email.equals("NOT-YET-CREATED")) {
                System.out.println("Moving to Login");

                // Navigate to Login activity
                Intent i = new Intent(this, Login.class);
                startActivity(i);

                // Finish all the previous activities
                finishAffinity();
            }
        }




        setContentView(R.layout.activity_signup);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        cbRememberUser = findViewById(R.id.cbRememberUser);
        cbRememberLogin = findViewById(R.id.cbRememberLogin);

        btnHaveAccount = findViewById(R.id.btnHaveAccount);
        btnSignup = findViewById(R.id.btnSignup);
        btnExit = findViewById(R.id.btnExit);


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });




        Button btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name=etName.getText().toString().trim();
                String Email=etEmail.getText().toString().trim();
                String Phone=etPhone.getText().toString().trim();
                String Password =etPassword.getText().toString().trim();
                String ConfirmPassword=etConfirmPassword.getText().toString().trim();
                System.out.println(Name);
                System.out.println(Email);
                System.out.println(Phone);
                System.out.println(Password);
                System.out.println(ConfirmPassword);



                if(Name.length() < 4){
                    Toast.makeText(Signup.this, "Username should be 4-8 letters", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Pattern.matches("[_a-zA-Z0-9]+(\\.[A-Za-z0-9]*)*@[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]*)*", Email)){
                    Toast.makeText(Signup.this, "Invalid email id", Toast.LENGTH_LONG).show();
                    return;
                }
                if(Phone.length() < 8){
                    Toast.makeText(Signup.this, "Phone number should be 8-13 digits", Toast.LENGTH_LONG).show();
                    return;
                }
                if(Password.length() < 4){
                    Toast.makeText(Signup.this, "Password must have 4 digits", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!Password.equals(ConfirmPassword)){
                    Toast.makeText(Signup.this, "Confirm Password didn't match", Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences.Editor e = sp.edit();
                e.putString("USER-EMAIL", Email);
                e.putString("USER-NAME", Name);
                e.putString("USER-PHONE", Phone);
                e.putString("PASSWORD", Password);
                e.putBoolean("REMEMBER-USER", cbRememberUser.isChecked());
                e.putBoolean("REMEMBER-LOGIN", cbRememberLogin.isChecked());
                e.apply();

                Intent i = new Intent(Signup.this, Login.class);
                startActivity(i);
                finishAffinity();
            }
        });

        btnHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Signup.this,Login.class);
                startActivity(i);
            }
        });

    }
}