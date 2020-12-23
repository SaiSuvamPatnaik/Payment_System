package com.example.maps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText amt,id,nm,nt;
    Button btn;
    final int UPI_PAYMENT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amt=findViewById(R.id.amt);
        id=findViewById(R.id.id);
        nm=findViewById(R.id.nm);
        nt=findViewById(R.id.nt);
        btn=findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = amt.getText().toString();
                String note = nt.getText().toString();
                String name = nm.getText().toString();
                String upiId = id.getText().toString();
                payUsingUpi(amount,note,name,upiId);
            }
        });




    }

    private void payUsingUpi(String amount, String note, String name, String upiId) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa",upiId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser=Intent.createChooser(upiPayIntent,"Pay With");

        if (null!=chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser,UPI_PAYMENT);
        }
        else {
            Toast.makeText(MainActivity.this,"No App Found",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case UPI_PAYMENT:
                if ((RESULT_OK==resultCode) || (resultCode==11)){
                    if (data!=null){
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI","onActivityResult: "+trxt);
                        ArrayList<String> datalist = new ArrayList<>();
                        datalist.add(trxt);
                        upiPaymentDataOperation(datalist);
                    }
                    else {
                        Log.d("UPI","onActivityResult: "+"Return data is null");
                        ArrayList<String> datalist = new ArrayList<>();
                        datalist.add("nothing");
                        upiPaymentDataOperation(datalist);
                    }
                }
                else{
                    Log.d("UPI","onActivityResult: "+"Return data is null");
                    ArrayList<String> datalist = new ArrayList<>();
                    datalist.add("nothing");
                    upiPaymentDataOperation(datalist);
                }
                break;
        }
    }



    private void upiPaymentDataOperation(ArrayList<String> data) {
        String str = data.get(0);
        Log.d("UPIPAY","upiPaymentDataOperation: "+str);
        String paymentCancel = "";
        if (str==null) str="discard";
        String status="";
        String approvalRefNo="";
        String response[] = str.split("&");
        for (int i=0;i<response.length;i++){
            String equalStr[] = response[i].split("=");
            if (equalStr.length>=2){
                if (equalStr[0].toLowerCase().equals("Status".toLowerCase())){
                    status=equalStr[1].toLowerCase();


                }
                else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())){
                    approvalRefNo=equalStr[1];

                }
            }
            else {
                paymentCancel="Payment cancelled by user.";

            }
        }

        if (status.equals("success")){
            Toast.makeText(MainActivity.this,"Successful",Toast.LENGTH_SHORT).show();

        }
        else if ("Payment cancelled by user.".equals(paymentCancel)){
            Toast.makeText(MainActivity.this,"Cancelled By User",Toast.LENGTH_SHORT).show();

        }
    }
}