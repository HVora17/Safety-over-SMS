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

public class ChangePIN extends AppCompatActivity {

    SharedPreferences prefs = null;
    TextView question;
    EditText answer,newPIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        prefs = getSharedPreferences("DB",MODE_PRIVATE);

        question = findViewById(R.id.ques);
        question.setText(prefs.getString("Question","Null"));
    }

    public void change_pin(View view)
    {
        answer = findViewById(R.id.enterAnswer);
        newPIN = findViewById(R.id.enterPIN);
        prefs = getSharedPreferences("DB",MODE_PRIVATE);

        String oldPIN = prefs.getString("PIN","0000");
        String new_PIN = newPIN.getText().toString();
        String ans = answer.getText().toString();
        String stored_ans = prefs.getString("Answer","null");

        if(ans.equals(stored_ans))
        {
            if(oldPIN.equals(new_PIN))
            {
                Toast.makeText(getApplicationContext(),"New PIN cannot be the same as old PIN",Toast.LENGTH_LONG).show();
            }
            else
            {
                if(new_PIN.matches("^\\d{4}$"))
                {
                    prefs = getSharedPreferences("DB", MODE_PRIVATE);
                    prefs.edit().putString("PIN", new_PIN).apply();
                    Toast.makeText(getApplicationContext(),"PIN Changed",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ChangePIN.this,Home.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"ENTER A 4-DIGIT PIN",Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Wrong Answer",Toast.LENGTH_LONG).show();
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
}
