package com.master.ringtoneapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.master.ringtoneapp.R;

import java.io.File;

public class RingtoneUtils {

    public static void setRingtone(Context context, File file) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, file.getName().replace(".mp3", ""));
        values.put(MediaStore.MediaColumns.SIZE, file.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, file.getName().replace(".mp3", ""));
        values.put(MediaStore.Audio.Media.DURATION, 230);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

//Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE,
                newUri
        );
    }

    public static void setNotification(Context context, File file) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, file.getName().replace(".mp3", ""));
        values.put(MediaStore.MediaColumns.SIZE, file.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, file.getName().replace(".mp3", ""));
        values.put(MediaStore.Audio.Media.DURATION, 230);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

//Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_NOTIFICATION,
                newUri
        );
    }

    public static void setAlarm(Context context, File file) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, file.getName().replace(".mp3", ""));
        values.put(MediaStore.MediaColumns.SIZE, file.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, file.getName().replace(".mp3", ""));
        values.put(MediaStore.Audio.Media.DURATION, 230);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

//Insert it into the database
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Uri newUri = context.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_ALARM,
                newUri
        );
    }

    public static void setRingtoneForContact(Context context, Uri contactUri, File file) {
        String phoneNo = null;
        String name = null;

        Cursor cursor = context.getContentResolver().query(contactUri, null, null, null, null);

        if (cursor.moveToFirst()) {
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);


            setForContact(context,file,phoneNo);

        }
        cursor.close();
    }

    private static void setForContact(Context context, File f, String contact) {
        // Create File object for the specified ring tone path
        // contact_selected_ringtone is a global variable which has the full path to ringtone
//        File f=new File(contact_selected_ringtone);
        // Insert the ring tone to the content provider
        ContentValues content_value = new ContentValues();
        content_value.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        content_value.put(MediaStore.MediaColumns.TITLE, f.getName());
        content_value.put(MediaStore.MediaColumns.SIZE, f.length());
        content_value.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        content_value.put(MediaStore.Audio.Media.ARTIST, context.getString(R.string.app_name));
        content_value.put(MediaStore.Audio.Media.IS_ALARM, false);
        content_value.put(MediaStore.Audio.Media.IS_MUSIC, false);
        content_value.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        content_value.put(MediaStore.Audio.Media.IS_RINGTONE, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        try {
            context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + f.getAbsolutePath() + "\"", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri newUri = context.getContentResolver().insert(uri, content_value);
//        newUri=FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider", f);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // The Uri used to look up a contact by phone number
        final Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contact);

        final String[] projection = new String[]{
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
        };

        final Cursor data = context.getContentResolver().query(lookupUri, projection, null, null, null);
        data.moveToFirst();
        try {
            // Get the contact lookup Uri
            final long contactId = data.getLong(0);
            final String lookupKey = data.getString(1);
            final Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
            String str = data.getString(data.getColumnIndexOrThrow("_id"));
            Uri localUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, str);
            if (contactUri == null) {
                // Invalid arguments
                return;
            }

//            final File file = new File(contact_selected_ringtone);

            // Apply the custom ringtone_app
            final ContentValues values = new ContentValues();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
            values.put(ContactsContract.Data.CUSTOM_RINGTONE, newUri.toString());
            try {
                int rows = context.getContentResolver().update(localUri, values, null, null);
                if (rows > 0) {

//                    Toasty.info(getApplicationContext(),getResources().getString(R.string.ringtone_contact_success),Toast.LENGTH_LONG).show();
                }
            } catch (NullPointerException ex) {
                Log.e("contact_exception", ex.toString());
            }

        } finally {
            data.close();
        }
    }

}
