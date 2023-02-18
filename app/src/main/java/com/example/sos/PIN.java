package com.example.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PIN extends AppCompatActivity {

    SharedPreferences prefs = null;
    EditText pin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        prefs = getSharedPreferences("DB",MODE_PRIVATE);
        if(!(prefs.getString("PIN","null")).equals("null"))
        {
            startActivity(new Intent(PIN.this,Home.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"safetyoversms@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Safety Over SMS - Feedback");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (Exception ex) {
        }
        return true;
    }

    public void onClick(View v)
    {
        pin = findViewById(R.id.editPIN);
        String regex = "^\\d{4}$";
        String PIN = pin.getText().toString();

        if(PIN.matches(regex))
        {
            prefs = getSharedPreferences("DB", MODE_PRIVATE);
            prefs.edit().putString("PIN", PIN).apply();
            Intent i = new Intent(PIN.this,Home.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"ENTER A 4-DIGIT PIN",Toast.LENGTH_LONG).show();
        }
    }
}
