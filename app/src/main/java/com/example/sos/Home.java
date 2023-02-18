package com.example.sos;

import android.Manifest;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    ImageButton help,about,change;
    TextView displayPIN;
    SharedPreferences prefs = null;

    String[] permission = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static DevicePolicyManager dpm;
    public ComponentName cm;
    protected static final int REQUEST_ENABLE = 1;
    protected static final int SET_PASSWORD = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prefs = getSharedPreferences("DB",MODE_PRIVATE);

        displayPIN = findViewById(R.id.displayPIN);
        displayPIN.setText(prefs.getString("PIN","Not Set"));

        about = (ImageButton)findViewById(R.id.aboutus);

        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(Home.this, AboutUs.class);
                startActivity(myIntent);
            }
        });

        help = (ImageButton)findViewById(R.id.help);

        help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(Home.this, Help.class);
                startActivity(myIntent);
            }
        });


        change = (ImageButton)findViewById(R.id.changepin);
        change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(Home.this, ChangePIN.class);
                startActivity(myIntent);
            }
        });

    }

    @Override
    public void onBackPressed()
    {}

    @Override
    public void onStart()
    {
        super.onStart();
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        cm = new ComponentName(this, MyDevicePolicyReceiver.class);

        if(dpm != null &&dpm.isAdminActive(cm)) {
            // admin active
        }

        else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm);
            startActivityForResult(intent, REQUEST_ENABLE);
        }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Home.this, permission, 100);
                return;
            }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE:
                    dpm.setPasswordQuality(cm, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);

                    boolean isSufficient = dpm.isActivePasswordSufficient();

                    if (!isSufficient) {
                        Intent setPasswordIntent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        startActivityForResult(setPasswordIntent, SET_PASSWORD);
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            //check if all permissions are granted
            boolean allgranted = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                }
                else
                    {
                    allgranted = false;
                    break;
                }
            }

            if (!allgranted) {
                ActivityCompat.requestPermissions(Home.this, permission, 100);
            }
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
        startActivity(emailIntent);

        return true;
    }


    public static class MyDevicePolicyReceiver extends DeviceAdminReceiver {

        @Override
        public void onDisabled(Context context, Intent intent) {
            Toast.makeText(context, "Device Admin Disabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            Toast.makeText(context, "Device Admin is now enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            CharSequence disableRequestedSeq = "Requesting to disable Device Admin";
            return disableRequestedSeq;
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
        }

        @Override
        public void onPasswordExpiring(Context context, Intent intent) {
        }

        @Override
        public void onPasswordFailed(Context context, Intent intent) {
        }

        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
        }
    }
}
