package com.example.tanmay.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;
   public static String string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        editText= findViewById(R.id.phrase);
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean res=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
    }


    public void send(View view){
        string= editText.getText().toString().trim();
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
        Intent intent= new Intent(MainActivity.this,camera.class);
//        intent.putExtra("phrase",string);
        startActivity(intent);
    }
}
