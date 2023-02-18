package com.example.sos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Question extends AppCompatActivity {

    SharedPreferences prefs = null;
    EditText question = null;
    EditText answer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = (EditText) findViewById(R.id.editQuestion);
        answer = (EditText) findViewById(R.id.editAnswer);

        prefs = getSharedPreferences("DB", MODE_PRIVATE);

        if(!(prefs.getString("Question","null").equals("null")))
        {
            Intent i = new Intent(Question.this,PIN.class);
            startActivity(i);
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

    public void btn_click(View view)
    {
        if(!(question.getText().toString().equals("") && answer.getText().toString().equals("")))
        {
            prefs = getSharedPreferences("DB", MODE_PRIVATE);
            prefs.edit().putString("Question", question.getText().toString()).apply();
            prefs.edit().putString("Answer", answer.getText().toString()).apply();

            Intent i = new Intent(Question.this,PIN.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Enter Security Question and Answer",Toast.LENGTH_LONG).show();
        }
    }
}