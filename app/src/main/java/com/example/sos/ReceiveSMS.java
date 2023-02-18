package com.example.sos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class ReceiveSMS extends BroadcastReceiver{

    public static final String SMS_BUNDLE = "pdus";
    String[] command;
    TelephonyManager telephonyManager;
    static String smsBody, address;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    SharedPreferences prefs = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                smsBody = smsMessage.getMessageBody();
                address = smsMessage.getOriginatingAddress();

                command = smsBody.split(" ");
            }

            prefs = context.getSharedPreferences("DB",MODE_PRIVATE);
            String pin = prefs.getString("PIN","Null");

            if(command[1].equals(pin))
            {
                if(command[0].equalsIgnoreCase("sosimei"))
                {
                    sendIMEI(address, telephonyManager);
                }


                if(command[0].equalsIgnoreCase("soscon"))
                {
                    String search = "";

                    for (int j = 2; j < command.length; j++) {
                        search += command[j];

                        if ((j != command.length - 1)) {
                            search += " ";
                        }
                    }
                    showContact(search, address, context);
                }

                if(command[0].equalsIgnoreCase("sosrecord"))
                {
                    int duration = Integer.parseInt(command[2]);
                    recordAudio(context, duration);
                }

                if(command[0].equalsIgnoreCase("sosring"))
                {
                    try {

                        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                        }

                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        final Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
                        ringtone.setLooping(true);
                        ringtone.play();

                        Thread th = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(30000);  //30000 is for 30 seconds, 1 sec =1000
                                    if (ringtone.isPlaying())
                                        ringtone.stop();   // for stopping the ringtone
                                } catch (InterruptedException e) {
                                }
                            }
                        });
                        th.start();
                    } catch (Exception e) {
                    }
                }

                if(command[0].equalsIgnoreCase("sossms"))
                {
                    String searchRec = "";
                    String message = "";
                    int j;

                    for (j = 2; j < command.length; j++) {
                        if (command[j].charAt(0) != ':') {
                            searchRec += command[j];
                            if (!(command[j + 1].charAt(0) == ':')) {
                                searchRec += " ";
                            }
                        }
                        else
                            break;
                    }

                    for (int i = j; i < command.length; i++) {
                        message += command[i];
                        if (i == j) {
                            message = message.substring(1, message.length());
                        }

                        if (!(i == j - 1)) {
                            message += " ";
                        }
                    }

                    sendSMS(searchRec, message, context);
                }

                if(command[0].equalsIgnoreCase("soslocate"))
                {
                    context.startService(new Intent(context,Locate.class));
                }

                if(command[0].equalsIgnoreCase("soslock"))
                {
                    context.startService(new Intent(context,Lock.class));
                }
            }
        }
    }

    public void sendIMEI(String address, TelephonyManager telephonyManager) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address, null, "IMEI: " + telephonyManager.getDeviceId(), null, null);
    }

    public void showContact(String search, String address, Context context) {
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        String contact = "";
        String num = "";
        String name = "";

        try {
            while (!name.equals(search)) {
                assert c != null;
                c.moveToNext();
                name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            }

            Cursor phones = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                    new String[]{name}, null);

            while (phones.moveToNext()) {
                num += phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "\n";
            }
        } catch (Exception e) {
        }

        if (name.equals(search)) {
            contact += name + ":\n";
            contact += num;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(address, null, contact, null, null);
        }
        c.close();
    }

    public void sendSMS(String searchRec, String message, Context context) {
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        String num = "";
        String name = "";

        try {
            while (!name.equals(searchRec)) {
                assert c != null;
                c.moveToNext();
                name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            }

            Cursor phones = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                    new String[]{name}, null);
            while (phones.moveToNext()) {
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                if (type == 2)
                    num = phones.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        } catch (Exception e) {
        }

        if (name.equals(searchRec)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(num, null, message, null, null);
        }
        c.close();
    }


    public void recordAudio(final Context context, final int duration) {
        Random ran = new Random();
        int r = ran.nextInt();
        AudioSavePathInDevice =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        "AudioRecording" + r + ".mp3";

        MediaRecorderReady();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(context, "Recording Started", Toast.LENGTH_LONG).show();

            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Looper.prepare();
                        Thread.sleep((duration * 1000)+1000);
                        mediaRecorder.stop();
                        Toast.makeText(context, "Recording Stopped", Toast.LENGTH_LONG).show();
                    } catch (InterruptedException e) {
                    }
                }
            });
            th.start();

        } catch (Exception e) {
        }
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public static void sendLocation(String url)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address, null, url, null, null);
    }
}