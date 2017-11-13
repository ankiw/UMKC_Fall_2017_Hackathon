package com.example.android.shushme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.android.shushme.R.id.button2;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
    }

    public void goToActivity2(View view) {
        Intent intent = new Intent( MainActivity.this, HomeActivity.class );
        startActivity( intent );
    }

}