package com.cscorner.cse_489_sabit;

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

public class Login extends AppCompatActivity {

    private EditText etEmail,etPassword;
    private CheckBox cbRememberUser,cbRememberLogin;
    private Button btnHaveAccount,btnLogin,btnExit;

    private SharedPreferences sp;
    private String Email, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);



        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        cbRememberUser = findViewById(R.id.cbRememberUser);
        cbRememberLogin = findViewById(R.id.cbRememberLogin);

        btnHaveAccount = findViewById(R.id.btnHaveAccount);
        btnLogin = findViewById(R.id.btnLogin);
        btnExit = findViewById(R.id.btnExit);

        sp = this.getSharedPreferences("my_sp", MODE_PRIVATE);
        Email = sp.getString("USER-EMAIL", "");
        Password = sp.getString("PASSWORD", "");
        boolean remUser = sp.getBoolean("REMEMBER-USER", false);
        boolean remLogin = sp.getBoolean("REMEMBER-LOGIN", false);
        if(remUser){
            etEmail.setText(Email);
            cbRememberUser.setChecked(true);
        }
        if(remLogin){
            etPassword.setText(Password);
            cbRememberLogin.setChecked(true);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String e = etEmail.getText().toString().trim();
                String p = etPassword.getText().toString().trim();
                if(!e.equals(Email)){
                    Toast.makeText(Login.this, "Email didn't match", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!p.equals(Password)){
                    Toast.makeText(Login.this, "Password didn't match", Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("REMEMBER-USER", cbRememberUser.isChecked());
                ed.putBoolean("REMEMBER-LOGIN", cbRememberLogin.isChecked());
                ed.apply();
                Intent i = new Intent(Login.this, ReportlistActivity.class);
                startActivity(i);


            }
        });


        btnHaveAccount.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent in = new Intent(Login.this, Signup.class);
                in.putExtra("key", "value");
                startActivity(in);
            }
        });



        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishAffinity();

            }
        });

    }
}
