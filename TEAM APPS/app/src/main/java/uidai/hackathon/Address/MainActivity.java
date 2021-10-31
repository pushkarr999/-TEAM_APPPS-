package uidai.hackathon.Address;

import static uidai.hackathon.Address.CONST_Variables.URL_AUTH;
import static uidai.hackathon.Address.CONST_Variables.URL_OTP;
import static uidai.hackathon.Address.CONST_Variables.txnId;
import static uidai.hackathon.Address.CONST_Variables.uidNumber;
import static uidai.hackathon.Address.CONST_Variables.uuid;
import static uidai.hackathon.Address.CONST_Variables.vidNumber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText Edittext_Adhaar,Edittext_OTP,Edittext_VID;
    Button Button_getOTP,Button_Login;
    TextView Text_OR;
    JSONObject OtpReqJson = null;
    JSONObject authReqJson = null;
    JsonObjectRequest authReq;
    RequestQueue authReqQue;
    Intent login;


//    private static final String URL_CAPTCHA = "https://stage1.uidai.gov.in/unifiedAppAuthService/api/v2/get/captcha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        captcha=findViewById(R.id.imageView);
        Edittext_Adhaar = findViewById(R.id.Adhaar_Edittext);
        Edittext_VID = findViewById(R.id.VID_Edittext);
//        Edittext_Captcha = findViewById(R.id.Captcha_Edittext);
        Edittext_OTP = findViewById(R.id.OTP_Edittext);
        Button_getOTP = findViewById(R.id.Button_GetOTP);
        Button_Login = findViewById(R.id.Button_Login);
        Text_OR =findViewById(R.id.Text_OR);

        Button_getOTP.setOnClickListener(v -> {
            CONST_Variables.uuid = UUID.randomUUID().toString();
            uidNumber = Long.parseLong(Edittext_Adhaar.getText().toString());
            try {
                if(!Edittext_Adhaar.getText().toString().isEmpty()&&Edittext_Adhaar.getText().toString().length()==12){
                    if(!Edittext_VID.getText().toString().isEmpty()&&Edittext_VID.getText().toString().length()==16){
                        vidNumber = Long.parseLong(Edittext_VID.getText().toString());
                        OtpReqJson = new JSONObject("{\n" +
                                " \"uid\": \""+uidNumber+"\",\n" +
                                " \"vid\": \""+vidNumber+"\",\n"+
                                " \"txnId\": \""+uuid+"\"\n" +
                                "}");
                        Edittext_Adhaar.setEnabled(false);
                        Edittext_VID.setEnabled(false);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "VID not Entered  or Not Correctly Entered", Toast.LENGTH_SHORT).show();
                        OtpReqJson = new JSONObject("{\n" +
                                " \"uid\": \""+uidNumber+"\",\n" +
                                " \"txnId\": \""+uuid+"\"\n" +
                                "}");
                        Edittext_VID.setVisibility(View.GONE);
                        Text_OR.setVisibility(View.GONE);
                        Edittext_Adhaar.setEnabled(false);
                    }

                }
                else if(!Edittext_VID.getText().toString().isEmpty()&&Edittext_VID.getText().toString().length()==16){
                    Toast.makeText(getApplicationContext(), "UID not Entered  or Not Correctly Entered", Toast.LENGTH_SHORT).show();
                    OtpReqJson = new JSONObject("{\n" +
                            " \"vid\": \""+vidNumber+"\",\n" +
                            " \"txnId\": \""+uuid+"\"\n" +
                            "}");
                    Edittext_Adhaar.setVisibility(View.GONE);
                        Text_OR.setVisibility(View.GONE);
                    Edittext_VID.setEnabled(false);
                }
                else Toast.makeText(getApplicationContext(), "UID and not Entered  or Not Correctly Entered", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestQueue rQOTP;
            rQOTP = Volley.newRequestQueue(this);
            JsonObjectRequest otpReq= new JsonObjectRequest(
                    Request.Method.POST,
                    URL_OTP,
                    OtpReqJson,
                    (Response.Listener<JSONObject>) rspns ->{
                        try {
                            Log.e("Response",rspns.toString());


                            if(rspns.getString("status").toLowerCase(Locale.ROOT).equals("y")){
                                Edittext_OTP.setVisibility(View.VISIBLE);
                                Button_Login.setVisibility(View.VISIBLE);
                            }
                            else if(rspns.getString("errCode").equals("953")){
                                Toast.makeText(getApplicationContext(), "Wait For A Minute and Send Again", Toast.LENGTH_LONG).show();
                                Edittext_Adhaar.setEnabled(true);
                                Edittext_VID.setEnabled(true);
                                Edittext_Adhaar.setVisibility(View.VISIBLE);
                        Text_OR.setVisibility(View.VISIBLE);
                                Edittext_VID.setVisibility(View.VISIBLE);
                            }
                        else {Toast.makeText(this, "Error:"+rspns.getString("errCode"), Toast.LENGTH_SHORT).show();
                                Edittext_Adhaar.setEnabled(true);
                                Edittext_VID.setEnabled(true);
                                Edittext_Adhaar.setVisibility(View.VISIBLE);
                        Text_OR.setVisibility(View.VISIBLE);
                                Edittext_VID.setVisibility(View.VISIBLE);
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    (Response.ErrorListener) error ->
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()

            );
            rQOTP.add(otpReq);
            rQOTP.start();
        });



        Button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authReqQue = Volley.newRequestQueue(MainActivity.this);
                try {
                    if(!Edittext_Adhaar.getText().toString().isEmpty()&&Edittext_Adhaar.getText().toString().length()==12){
                        if(!Edittext_VID.getText().toString().isEmpty()&&Edittext_VID.getText().toString().length()==16){
                            vidNumber = Long.parseLong(Edittext_VID.getText().toString());
                            authReqJson = new JSONObject("{\n" +
                                    " \"uid\": \""+uidNumber+"\",\n" +
                                    " \"vid\": \""+vidNumber+"\",\n"+
                                    " \"txnId\": \""+uuid+"\"\n" +
                                    " \"otp\": \""+Edittext_OTP.getText().toString()+"\"\n" +
                                    "}");

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "VID not Entered  or Not Correctly Entered", Toast.LENGTH_SHORT).show();
                            authReqJson = new JSONObject("{\n" +
                                    "\"uid\": \""+uidNumber+"\",\n" +
                                    "\"txnId\": \""+uuid+"\",\n" +
                                    "\"otp\": \""+Edittext_OTP.getText().toString()+"\"\n" +
                                    "}");
                        }

                    }
                    else if(!Edittext_VID.getText().toString().isEmpty()&&Edittext_VID.getText().toString().length()==16){
                        Toast.makeText(getApplicationContext(), "UID not Entered  or Not Correctly Entered", Toast.LENGTH_SHORT).show();
                        authReqJson = new JSONObject("{\n" +
                                " \"vid\": \""+vidNumber+"\",\n" +
                                " \"txnId\": \""+uuid+"\"\n" +
                                " \"otp\": \""+Edittext_OTP.getText().toString()+"\"\n" +
                                "}");
                    }
                    else Toast.makeText(getApplicationContext(), "UID and not Entered  or Not Correctly Entered", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.e("JSON",authReqJson.toString());
                authReq = new JsonObjectRequest(
                        Request.Method.POST,
                        URL_AUTH,
                        authReqJson,
                        (Response.Listener<JSONObject>) rspns ->{
                            Log.d("resp",rspns.toString());
                            String respo= null;
                            String errcode = null;
                            try {
                                respo = rspns.getString("status");
                                errcode = rspns.getString("errCode");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(respo.equals("y")){
                                login = new Intent(MainActivity.this,Function_Select.class);
                                login.putExtra("UID",Long.toString(uidNumber));
                                login.putExtra("txnId",txnId);
                                login.putExtra("VID",Long.toString(vidNumber));
                                login.putExtra("OTP",Edittext_OTP.getText().toString());
                                startActivity(login);
                            }
                        },
                        (Response.ErrorListener) error ->
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()

                );
                authReqQue.add(authReq);
                authReqQue.start();


            }
        });
    }
}