package com.teamvoid.dealsonwheels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    LinearLayout List;
    ImageView BigImg;
    LinearLayout BigImgCard;
    TextView Details;
    Button CallBtn;
    EditText key;

    static boolean searched = false;
    LayoutInflater inflater;
    DatabaseReference UsersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        List = findViewById(R.id.List);
        BigImg = findViewById(R.id.BigImage);
        BigImgCard = findViewById(R.id.bigCard);
        Details = findViewById(R.id.Detail);
        CallBtn = findViewById(R.id.callBtn);
        key = findViewById(R.id.searchKey);
        inflater = LayoutInflater.from(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        UsersRef = database.getReference("Users");

        Refresh("");

        findViewById(R.id.searchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searched = true;
                Refresh(key.getText().toString().trim());
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (BigImgCard.getVisibility() == View.VISIBLE){
            BigImgCard.setVisibility(View.GONE);
        }
        else if (searched){
            searched = false;
            Refresh("");
        }
        else
            super.onBackPressed();
    }

    private View AddProduct(View listView, List<String> productDetails) {
        ImageView img = listView.findViewById(R.id.image);
        TextView detail = listView.findViewById(R.id.Detail);
        Picasso.get().load(productDetails.get(1)).placeholder(android.R.drawable.ic_menu_gallery).into(img);
        detail.setText(productDetails.get(3)+"\nRs."+productDetails.get(2));
        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get().load(productDetails.get(1)).placeholder(android.R.drawable.ic_menu_gallery).into(BigImg);
                Details.setText(productDetails.get(3)+"\nDetails:"+productDetails.get(0)+"\nPrice: Rs."+productDetails.get(2)+"\nOwner: "+productDetails.get(4));
                CallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + productDetails.get(5))));
                    }
                });
                BigImgCard.setVisibility(View.VISIBLE);
            }
        });
        return listView;
    }

    void Refresh(String keyword){

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List.removeAllViews();
                if (snapshot.getValue() != null) {
                    //Toast.makeText(Home.this, snapshot.getKey().toString(), Toast.LENGTH_SHORT).show();
                    for (DataSnapshot value : snapshot.getChildren()) {
                        if (value.getValue() != null) {
                            //Toast.makeText(Home.this, value.getKey().toString(), Toast.LENGTH_SHORT).show();
                            for (DataSnapshot data: value.getChildren()) {
                                if (data.getKey().equals("Ads")) {
                                    // Toast.makeText(Home.this, data.getKey().toString(), Toast.LENGTH_SHORT).show();
                                    for (DataSnapshot ad : data.getChildren()) {
                                        //Toast.makeText(Home.this, ad.getKey().toString(), Toast.LENGTH_SHORT).show();
                                        List<String> adDetails = new ArrayList<String>();
                                        for (DataSnapshot adAttrb: ad.getChildren()) {
                                            if (adAttrb.exists()){
                                                //Toast.makeText(Home.this, adAttrb.getKey().toString(), Toast.LENGTH_SHORT).show();
                                                adDetails.add(adAttrb.getValue().toString());
                                            }
                                        }
                                        View listView = inflater.inflate(R.layout.item, List, false);
                                        if (keyword.equals("") || adDetails.get(0).toLowerCase().contains(keyword.trim().toLowerCase()) || adDetails.get(3).toLowerCase().contains(keyword.trim().toLowerCase()))
                                            List.addView(AddProduct(listView, adDetails));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}