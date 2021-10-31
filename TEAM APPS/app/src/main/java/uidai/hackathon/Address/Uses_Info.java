package uidai.hackathon.Address;

import static android.content.ContentValues.TAG;
import static uidai.hackathon.Address.CONST_Variables.LOGTAG;
import static uidai.hackathon.Address.CONST_Variables.Token;
import static uidai.hackathon.Address.CONST_Variables.Token1;
import static uidai.hackathon.Address.CONST_Variables.UserByMobile;
import static uidai.hackathon.Address.CONST_Variables.UserByUID;
import static uidai.hackathon.Address.CONST_Variables.UserByVID;
import static uidai.hackathon.Address.CONST_Variables.UsersDr;
import static uidai.hackathon.Address.CONST_Variables.database;
import static uidai.hackathon.Address.CONST_Variables.uidNumber;
import static uidai.hackathon.Address.CONST_Variables.vidNumber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Uses_Info extends AppCompatActivity {
EditText vid_or_uid,mobile_number;
Button send_notif_Button;

    private final int REQUEST_GOOGLE_PLAY_SERVICES=01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uses_info);
        firebaseinit();
        initialiseUI();
        setOnDataChange();
        send_notif_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Button Clic");
                if(checkIsUID()){
                   Token = UserByUID.child(vid_or_uid.getText().toString()).getValue(String.class);
                sendNotifi();}
                if(checkIsVID())
                    if (checkIsMobile()){
                        Token = UserByVID.child(vid_or_uid.getText().toString()).getValue(String.class);
                        Token1 = UserByUID.child(mobile_number.getText().toString()).getValue(String.class);
                        sendNotifi();
                    }
                    else
                        Toast.makeText(Uses_Info.this, "Enter A Mobile Number", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(Uses_Info.this,"Enter Valid UID or VID",Toast.LENGTH_LONG).show();

            }
        });

    }

    private void sendNotifi() {
        if(checkIsUID()){
        FcmNotificationsSender fcm = new FcmNotificationsSender(Token,"Request for Address Access ","Request Form UID : "+String.valueOf(uidNumber).substring(0,2)+"xxxxxxxxxx",getApplicationContext(),this);
        fcm.SendNotifications();
            Log.d(LOGTAG,"Sent Notification to user with token  : "+ Token);
            Log.d(LOGTAG,"Notification "+ "Request for Address Access "+"Request Form UID from"+String.valueOf(uidNumber).substring(0,2)+"xxxxxxxxxx");}
        if(checkIsVID()){
            FcmNotificationsSender fcm = new FcmNotificationsSender(Token,"Request for Address Access ","Request Form vid from"+String.valueOf(vidNumber),getApplicationContext(),this);
            fcm.SendNotifications();
            Log.d(LOGTAG,"Sent Notification to user with token  : "+ Token);
            Log.d(LOGTAG,"Notification "+ "Request for Address Access "+"Request Form VID from"+String.valueOf(vidNumber).substring(0,2)+"xxxxxxxxxxxxx");}
        if(checkIsMobile()){
            FcmNotificationsSender fcm = new FcmNotificationsSender(Token,"Request for Address Access ","Request Form vid from"+String.valueOf(mobile_number),getApplicationContext(),this);
            fcm.SendNotifications();
            Log.d(LOGTAG,"Sent Notification to user with token  : "+ Token);
            Log.d(LOGTAG,"Notification "+ "Request for Address Access "+"Request Form Mobile from"+String.valueOf(mobile_number).substring(0,2)+"xxxxxxxx");
        }
    }


    private void setOnDataChange() {
    UsersDr.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            UserByUID = snapshot.child("BYUID");
            UserByMobile = snapshot.child("BYMOBILE");
            UserByVID = snapshot.child("BYVID");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }

    private void firebaseinit() {
    database = FirebaseDatabase.getInstance();
    UsersDr = database.getReference("USERS");
    }

    private void initialiseUI() {
    vid_or_uid =findViewById(R.id.editText_VID);
    mobile_number = findViewById(R.id.editText_Mobile);
    send_notif_Button = findViewById(R.id.button_Notify_User);
    }
    private  boolean checkIsUID(){
        return vid_or_uid.getText().length() == 12;}
    private  boolean checkIsVID(){
        return vid_or_uid.getText().length() == 16;}
    private boolean checkIsMobile(){
        return vid_or_uid.getText().length() == 10; }
    private void startRegistrationService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
        } else if (api.isUserResolvableError(code) &&
                api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES)) {
            // wait for onActivityResult call (see below)
        } else {
            Toast.makeText(this, api.getErrorString(code), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    Intent i = new Intent(this, Uses_Info.class);
                    startService(i); // OK, init GCM
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
