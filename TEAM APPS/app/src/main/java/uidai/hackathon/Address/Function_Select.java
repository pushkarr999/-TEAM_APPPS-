package uidai.hackathon.Address;

import static android.content.ContentValues.TAG;
import static uidai.hackathon.Address.CONST_Variables.database;
import static uidai.hackathon.Address.CONST_Variables.UserByVID;
import static uidai.hackathon.Address.CONST_Variables.UsersDr;
import static uidai.hackathon.Address.CONST_Variables.UserByMobile;
import static uidai.hackathon.Address.CONST_Variables.UserByUID;
import static uidai.hackathon.Address.CONST_Variables.FbToken;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;

public class Function_Select extends AppCompatActivity {
    private FirebaseAuth mAuth;
Intent login;
TextView UserName_text;
Button button_updateAddress,button_adUpDoc,button_adUpUser;
Intent UserNotify,DocumentUpload;
String UID,VID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_select);
        initialiseUI();
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
      database = FirebaseDatabase.getInstance();
        login = getIntent();
        saveData();
        getTkn();
        UsersDr = database.getReference("USERS");
        InitialiseOnDataChangeUSers();
        button_updateAddress.setOnClickListener(v -> {
            button_adUpDoc.setVisibility(View.VISIBLE);
            button_adUpUser.setVisibility(View.VISIBLE);
            button_updateAddress.setVisibility(View.GONE);
        });
        button_adUpUser.setOnClickListener(v -> {
            UserNotify = new Intent(Function_Select.this,Uses_Info.class);
            startActivity(UserNotify); });
        //Code to DOne
        button_adUpDoc.setOnClickListener(v -> {
         //code to be done
        });
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(Function_Select.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void getTkn() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        while(task.getResult()==null);
                        FbToken = task.getResult();
                        updateCloud();

                        Toast.makeText(Function_Select.this, "Token Saved ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void InitialiseOnDataChangeUSers(){
    UsersDr.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            UserByUID = dataSnapshot.child("BYUID");
            UserByMobile = dataSnapshot.child("BYMOBILE");
            UserByVID = dataSnapshot.child("BYVID");
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    });

}
    private void updateCloud() {
        if(UID!=null)
            if(UID.length()==12){
        UsersDr.child("BYUID").child(UID).setValue(FbToken);
    Log.e("Rest","Updated UID on CLoud as "+ FbToken);}
        if(VID!=null)
        if(VID.length()==16)
        UsersDr.child("BYVID").child(VID).setValue(FbToken);

    }

    private void saveData() {
        if(login.getStringExtra("UID").length()==12)
        {UID = login.getStringExtra("UID");
        UserName_text.setText("Welcome "+UID.substring(0,2)+"xxxxxxxxxx");}
        else if(login.getStringExtra("VID").length()==16)
        {VID = login.getStringExtra("VID");
            UserName_text.setText("Welcome "+VID.substring(0,2)+"xxxxxxxxxxxxxx");}
        else if(login.getStringExtra("VID").length()==16&&login.getStringExtra("UID").length()==12)
        {VID = login.getStringExtra("VID");
            UserName_text.setText("Welcome "+VID.substring(0,2)+"xxxxxxxxxxxxxx");
            UID = login.getStringExtra("UID");
            UserName_text.setText("Welcome "+UID.substring(0,2)+"xxxxxxxxxx");}
        else{
            Toast.makeText(Function_Select.this, "VID AND UID BOTH ARENT PARCEd", Toast.LENGTH_SHORT).show();

        }    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        getTkn();
    }



    private void initialiseUI() {
    UserName_text = findViewById(R.id.Text_UserName);
    button_updateAddress = findViewById(R.id.Button_Address_Update);
    button_adUpDoc = findViewById(R.id.Button_Verify_Using_Document);
    button_adUpUser = findViewById(R.id.Button_Verify_Using_Another_User);
    }
}