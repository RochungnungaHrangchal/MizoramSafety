package in.nic.mizoram.mizoramsafety;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class NewRegistration extends AppCompatActivity implements View.OnClickListener {

       // Private Views i.e. Register Button and Input Box for Mobile Number and ProgressBar

        public  FirebaseFirestore firebaseFirestore;
        EditText mPhoneNumberField, mVerificationField,userNameField,userAddressField;
        Button mStartButton, mVerifyButton, mResendButton;
        ProgressDialog progressDialog;
        private FirebaseAuth mAuth;
        private PhoneAuthProvider.ForceResendingToken mResendToken;
        private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
        String mVerificationId;
        private static final String TAG = "PhoneAuthActivity";
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.newregistration);
            progressDialog = new ProgressDialog(this);

            firebaseFirestore=FirebaseFirestore.getInstance();
            checkContactPermission();
          //  checkCallPermission();
           // checkLocationPermission();
            final Vibrator vibrator = (Vibrator) NewRegistration.this.getSystemService(Context.VIBRATOR_SERVICE);



            mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
            userNameField=findViewById(R.id.username);
            userAddressField=findViewById(R.id.address);
            mVerificationField = (EditText) findViewById(R.id.field_verification_code);

            mStartButton = (Button) findViewById(R.id.button_start_verification);
            mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
            mResendButton = (Button) findViewById(R.id.button_resend);

            mStartButton.setOnClickListener(this);
            mVerifyButton.setOnClickListener(this);
            mResendButton.setOnClickListener(this);

            mVerifyButton.setVisibility(View.INVISIBLE);
            mResendButton.setVisibility(View.INVISIBLE);
            mVerificationField.setVisibility(View.INVISIBLE);

            mAuth = FirebaseAuth.getInstance();
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.w(TAG, "onVerificationFailed", e);
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        mPhoneNumberField.setError("Invalid phone number.");
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                                Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    Log.d(TAG, "onCodeSent:" + verificationId);
                    mVerificationId = verificationId;
                    mResendToken = token;
                }
                @Override
                public void onCodeAutoRetrievalTimeOut(String verificationId)
                {
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("Session Time-out");
                    progressDialog.setMessage("Please try after sometime...");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }

                    progressDialog.show();
                    Toast.makeText(getApplicationContext(),"Session Timeout while trying to Retrive your Code ..",Toast.LENGTH_LONG).show();
                }
            };
        }

        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = task.getResult().getUser();
                                //mPhoneNumberField, mVerificationField,userNameField,userAddressField;
                                LoginModel loginModel= new LoginModel(userNameField.getText().toString(),
                                        userAddressField.getText().toString(),mPhoneNumberField.getText().toString());

                                firebaseFirestore.collection("LoginDetails").add(loginModel);
                                startActivity(new Intent(NewRegistration.this,CheckContact.class));
                                finish();
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    mVerificationField.setError("Invalid code.");
                                }
                            }
                        }
                    });
        }


        private void startPhoneNumberVerification(String phoneNumber) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }

        private void verifyPhoneNumberWithCode(String verificationId, String code) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        }

        private void resendVerificationCode(String phoneNumber,
                                            PhoneAuthProvider.ForceResendingToken token) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks,         // OnVerificationStateChangedCallbacks
                    token);             // ForceResendingToken from callbacks
        }

        private boolean validatePhoneNumber() {
            String phoneNumber = "+91" + mPhoneNumberField.getText().toString();
            if (TextUtils.isEmpty(phoneNumber)) {
                mPhoneNumberField.setError("Invalid phone number.");
                return false;
            }
            return true;
        }
        @Override
        public void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(NewRegistration.this, CheckContact.class));
                finish();
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_start_verification:

                    final Vibrator vibrator = (Vibrator) NewRegistration.this.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(400);

                  if(TextUtils.isEmpty(userNameField.getText().toString()))
                  {
                      userNameField.setError("Name is MANDATORY !");
                      return;
                  }

                  if(TextUtils.isEmpty(userAddressField.getText().toString()))
                  {
                      userAddressField.setError("Address is Mandatory !");
                      return;
                  }

                    if (TextUtils.isEmpty(mPhoneNumberField.getText().toString()))
                    {
                        mPhoneNumberField.setError("Phone Number is Mandatory !");
                        return;
                    }

                    if (mPhoneNumberField.length() !=10)
                    {
                        mPhoneNumberField.setError("Phone Number should be 10 DIGITS !");
                        return;

                    }

                    if (!validatePhoneNumber()) {
                        return;
                    }

                    if (ConnectivityCheck.isConnectedToNetwork(this)) {
                        progressDialog.setCancelable(false);
                        progressDialog.setIcon(R.drawable.iconic);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setTitle("Verifying your Mobile Number");
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        startPhoneNumberVerification("+91"+ mPhoneNumberField.getText().toString());
                        break;
                        //Show the connected screen
                    } else {

                        new AlertDialog.Builder(this)
                                .setTitle("Scanning Network Connectivity")
                                .setCancelable(false)
                                .setIcon(R.drawable.iconic)
                                .setMessage("Please enable data connectivity and try again !")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                       // finish();
                                    }
                                })

                                .create()
                                .show();

                    }

                case R.id.button_verify_phone:
                    String code = mVerificationField.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        mVerificationField.setError("Cannot be empty.");
                        return;
                    }

                    verifyPhoneNumberWithCode(mVerificationId, code);
                    break;
                case R.id.button_resend:

                    resendVerificationCode("+91" + mPhoneNumberField.getText().toString(), mResendToken);
                    break;
            }

        }

    public static final int MY_LUCYFIC_REQUEST_CONTACT=21;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public  boolean checkContactPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Contact Read Permission Request")
                        .setMessage("Please Allow for this App..")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(NewRegistration.this,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        MY_LUCYFIC_REQUEST_CONTACT);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_LUCYFIC_REQUEST_CONTACT);
            }
            return false;
        } else {
            return true;
        }
    }
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
                        .setMessage("Please Enable Location for this App..")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(NewRegistration.this,
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


            case MY_PERMISSIONS_REQUEST_LOCATION:{
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

            case MY_LUCYFIC_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 400, 1, this);
                       // GPSTracker gpsTracker= new GPSTracker(this);
                        //gpsTracker.getLocation();

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    }
