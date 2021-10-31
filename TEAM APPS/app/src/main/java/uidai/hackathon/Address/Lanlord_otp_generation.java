package uidai.hackathon.Address;

import static uidai.hackathon.Address.CONST_Variables.URL_OTP;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Lanlord_otp_generation extends AppCompatActivity {
Intent gotNotif;
String notifierUID;
String notifierVID;
TextView info;
EditText adhaar_or_vid,otp_ediitext;
private String uid_vid;
private Button getOTP_button,Submit;
JSONObject otp =null;
JSONObject kyc = null;
String Ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_generate_otp);
        initialiseUI();
        extractedIntent();
        checkAndSetTextView();
        disableOrEnableInputViews(checkUIDandVIDexist());
        RequestHTTPOTP();
        uuid = UUID.randomUUID().toString();
        getOTP_button.setOnClickListener(v -> {
            if(uid_vid.equals("null")){
                if(adhaar_or_vid.getText().toString().length()==12) {
                    uidNumber = Long.parseLong(adhaar_or_vid.getText().toString());
                    try {
                        otp = new JSONObject("{\n" +
                                "\"uid\": \""+uidNumber+"\",\n" +
                                "\"txnId\": \""+uuid+"\"\n" +
                                "}");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(adhaar_or_vid.getText().toString().length()==12)
                    vidNumber = Long.parseLong(adhaar_or_vid.getText().toString());
                try {
                    otp = new JSONObject("{\n" +
                            "\"vid\": \""+vidNumber+"\",\n" +
                            "\"txnId\": \""+uuid+"\"\n" +
                            "}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            if(uid_vid.equals("both"))
                try {
                    otp = new JSONObject("{\n" +
                            "\"uid\": \""+uidNumber+"\",\n" +
                            "\"vid\": \""+vidNumber+"\",\n" +
                            "\"txnId\": \""+uuid+"\"\n" +
                            "}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            if(uid_vid.equals("uid"))
                try {
                    otp = new JSONObject("{\n" +
                            "\"uid\": \""+uidNumber+"\",\n" +
                            "\"txnId\": \""+uuid+"\"\n" +
                            "}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            if(uid_vid.equals("vid"))
                try {
                    otp = new JSONObject("{\n" +
                            "\"vid\": \""+vidNumber+"\",\n" +
                            "\"txnId\": \""+uuid+"\"\n" +
                            "}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            else
                Toast.makeText(getApplicationContext(), "UID and VID not Found", Toast.LENGTH_SHORT).show();
        });
        Submit.setOnClickListener(v -> {
            RequestHTTPKYC();
        });

    }

    private void RequestHTTPOTP() {
        RequestQueue rqstOTP;
        rqstOTP = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest otpJsonReq = new JsonObjectRequest(Request.Method.POST,
                URL_OTP,
                otp,
                (Response.Listener<JSONObject>) response ->{
            try{
                    if(response.getString("status").toLowerCase(Locale.ROOT).equals("y")){
                        adhaar_or_vid.setEnabled(false);
                        otp_ediitext.setVisibility(View.VISIBLE);
                        Submit.setVisibility(View.VISIBLE);
                    }
                    else if(response.getString("errCode").equals("953")){
                        Toast.makeText(getApplicationContext(), "Wait For A Minute and Send Again", Toast.LENGTH_LONG).show();
                        adhaar_or_vid.setEnabled(false);
                    }
                    else {Toast.makeText(this, "Error:"+response.getString("errCode"), Toast.LENGTH_SHORT).show();
                        adhaar_or_vid.setEnabled(true);
                    }
                } catch (JSONException e) {
            e.printStackTrace();
        }
                },
                (Response.ErrorListener) error ->
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show());
        rqstOTP.add(otpJsonReq);
        rqstOTP.start();
    }



    private void RequestHTTPKYC() {
        try {
            kyc = otp.put("otp","\""+otp_ediitext.getText().toString()+"\"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rqstOTP;
        rqstOTP = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest otpJsonReq = new JsonObjectRequest(Request.Method.POST,
                URL_OTP,
                kyc,
                (Response.Listener<JSONObject>) response ->{
                    Document doc = convertStringToXMLDocument(response.toString());
                    NodeList nodes = doc.getElementsByTagName("Uidata");
                    Element element = (Element) nodes;
                    NodeList nl = element.getElementsByTagName("Poa");
                    Element ele = (Element) nl;

                    Ad = "co = "+ele.getAttribute("co")+"country = " +ele.getAttribute("country")+
                            "\ndist" +ele.getAttribute("dist")+
                            "\nhouse" +ele.getAttribute("house")+
                            "\nlm" +ele.getAttribute("lm")+
                            "\nloc" +ele.getAttribute("loc")+
                            "\npc" +ele.getAttribute("pc")+
                            "\nstate" +ele.getAttribute("state")+
                            "\nstreet" +ele.getAttribute("street")+
                            "\nvtc"+ele.getAttribute("vtc");
                    Log.e("st",Ad);
                },
                (Response.ErrorListener) error ->
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show());
        rqstOTP.add(otpJsonReq);
        rqstOTP.start();
    }

    private static Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private void disableOrEnableInputViews(@NonNull String checkUIDandVIDexist) {
        if (checkUIDandVIDexist.equals("null")) {
        adhaar_or_vid.setVisibility(View.VISIBLE);
        uid_vid = "null";
        }
        else if(checkUIDandVIDexist.equals("both")){
            adhaar_or_vid.setVisibility(View.VISIBLE);
            uid_vid = "both";
        }
        else if(checkUIDandVIDexist.equals("UID")){
            adhaar_or_vid.setVisibility(View.VISIBLE);
            uid_vid = "uid";

        }
        else if(checkUIDandVIDexist.equals("VID")){
            adhaar_or_vid.setVisibility(View.VISIBLE);
            uid_vid = "vid";
        }
    }

    @NonNull
    private String checkUIDandVIDexist() {
    if(Long.toString(uidNumber).length()==12&&Long.toString(vidNumber).length()==16)
        return "both";
    else if(Long.toString(uidNumber).length()==12)
        return "UID";
    else if(Long.toString(vidNumber).length()==16)
        return "VID";
    else
        return "null";

    }

    private void checkAndSetTextView() {
        if(notifierVID.length()==16){
            info.setText("You Have Received \nan Address Sharing Request From \nUser VID "+notifierVID);
        }
        else if(notifierUID.length()==12){

            info.setText("You Have Received \nan Address Sharing Request From \nUser UID "+notifierUID);
        }
        else{
            Toast.makeText(getApplicationContext(),"got an anynomus request",Toast.LENGTH_SHORT);
            info.setText("We Couldnt Find Data error");
        }
    }

    private void extractedIntent() {
        gotNotif = getIntent();
        notifierUID = Long.toString(gotNotif.getLongExtra("UID",0));
        notifierVID = Long.toString(gotNotif.getLongExtra("VID",0));
    }

    private void initialiseUI() {
    info = findViewById(R.id.Info);
    adhaar_or_vid = findViewById(R.id.AdhaarEdit);
    getOTP_button = findViewById(R.id.Button_GetOTP);
    otp_ediitext = findViewById(R.id.OTP_Edittext);
    Submit = findViewById(R.id.Button_Login);
    }
}