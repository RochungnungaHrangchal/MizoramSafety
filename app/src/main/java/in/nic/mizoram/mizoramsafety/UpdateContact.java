package in.nic.mizoram.mizoramsafety;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UpdateContact extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String fireBaseUser;
    private static final int PICK_CONTACT = 1000;
    Button btnPickContact,btnPick2,btnRegisterEmer;
    TextView textView,txtPhone,txtEmer,txtEmerPhone;
    private static final String TAG = EmergencyContact.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int REQUEST_CODE_PICK_CONTACTS1 = 3;
    private Uri uriContact;
    private String contactID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatecontact);
        checkLocationPermission();

        firebaseFirestore=FirebaseFirestore.getInstance();
         firebaseAuth=FirebaseAuth.getInstance();
         fireBaseUser=firebaseAuth.getCurrentUser().getPhoneNumber();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnPickContact=findViewById(R.id.btnPickContact);
        btnRegisterEmer=findViewById(R.id.btnRegisterEmer);
        btnPick2=findViewById(R.id.btnPick2);
        textView=findViewById(R.id.textView);
        txtPhone=findViewById(R.id.txtPhone);
        txtEmer=findViewById(R.id.txtEmer);
        txtEmerPhone=findViewById(R.id.txtEmerPhone);

        btnRegisterEmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(textView.getText().toString()))
                {
                    textView.setError("Mandatory ");
                    return;
                }
                if(TextUtils.isEmpty(txtPhone.getText().toString()))
                {
                    txtPhone.setError("Mandatory ");
                    return;
                }
                if(TextUtils.isEmpty(txtEmer.getText().toString()))
                {
                    txtEmer.setError("Mandatory ");
                    return;
                }
                if(TextUtils.isEmpty(txtEmerPhone.getText().toString()))
                {
                    txtEmerPhone.setError("Mandatory ");
                    return;
                }


                final Vibrator vibrator = (Vibrator) UpdateContact.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);


                // internet a awm m?



                EmergencyContactModel contactModel= new EmergencyContactModel(0,
                        textView.getText().toString(),txtPhone.getText().toString(),
                        txtEmer.getText().toString(),txtEmerPhone.getText().toString());


                try
                {

                    if (ConnectivityCheck.isConnectedToNetwork(UpdateContact.this)) {
                        //Show the connected screen
                        firebaseFirestore.collection("Users").
                                document(fireBaseUser).collection("EmergencyContact")
                                .document("1").set(contactModel);

                        Toast.makeText(getApplicationContext(),"Successfully Updated !",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(UpdateContact.this,MapsActivity.class));
                        finish();
                    } else {

                        new AlertDialog.Builder(UpdateContact.this)
                                .setTitle("Scanning Network Connectivity")
                                .setCancelable(false)
                                .setIcon(R.drawable.iconic)
                                .setMessage("Please enable data connectivity and try again !")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        startActivity(new Intent(UpdateContact.this,MapsActivity.class));
                                        finish();
                                    }
                                })

                                .create()
                                .show();



                    }
                    // String id=firebaseFirestore.collection("Users").
                         //   document(fireBaseUser).collection("EmergencyContact").document().getId();
                            //.add(contactModel);


                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Something Went Wrong !, Please try again",Toast.LENGTH_LONG).show();

                }


            }
        });

        btnPickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Vibrator vibrator = (Vibrator) UpdateContact.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS);
            }
        });

        btnPick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Vibrator vibrator = (Vibrator) UpdateContact.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS1);

            }
        });


    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
            //retrieveContactPhoto();

        }

        if (reqCode == REQUEST_CODE_PICK_CONTACTS1 && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            retrieveContactName1();
            retrieveContactNumber1();
            //retrieveContactPhoto();

        }
    }



    private void retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            txtPhone.setText(contactNumber.toString());
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            textView.setText(contactName.toString());
        }

        cursor.close();

        Log.d(TAG, "Contact Name: " + contactName);

    }

    private void retrieveContactNumber1() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            txtEmerPhone.setText(contactNumber.toString());
        }

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactName1() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            txtEmer.setText(contactName.toString());
        }

        cursor.close();

        Log.d(TAG, "Contact Name: " + contactName);

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Request")
                        .setIcon(R.drawable.iconic)
                        .setMessage("Please Enable Location for this App..")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(UpdateContact.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {


            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 400, 1, this);
                        GPSTracker gpsTracker= new GPSTracker(this);
                        gpsTracker.getLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Cancelling Updates...")
                .setIcon(R.drawable.iconic)
                .setMessage("Do you Want to CANCEL Emergency Contact Update?")
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(UpdateContact.this,MapsActivity.class));
                        finish();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Emergency Contact kan Update Duh .. backPressed palh a ni e
                    }
                })
                .create()
                .show();


    }
}
