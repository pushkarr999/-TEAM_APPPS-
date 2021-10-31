package uidai.hackathon.Address;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CONST_Variables {
    public static FirebaseDatabase database;
    public static DatabaseReference UsersDr;
    public static DataSnapshot UserByUID,UserByVID,UserByMobile;
    public static long uidNumber;
    public static long vidNumber;
    public static String uuid;
    public static String TXNID;
    public static String txnId;
    public static final String URL_OTP = "https://stage1.uidai.gov.in/onlineekyc/getOtp/";
    public static final String URL_AUTH ="https://stage1.uidai.gov.in/onlineekyc/getAuth/";
    public static final String URL_EKYC ="https://stage1.uidai.gov.in/onlineekyc/getEkyc/";
    public static String FbToken;
    public static String Token;
    public static String Token1;
    public static String LOGTAG  = "Hackathon Audit";
}
