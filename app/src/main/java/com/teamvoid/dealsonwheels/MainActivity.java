package com.teamvoid.dealsonwheels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LinearLayout menu;
    ImageView AdImg;
    EditText AdImgUrlF;
    EditText AdTitleF;
    EditText AdDetailsF;
    EditText AdPriceF;
    Button AdPostBtn;
    CardView card;
    CardView AdCard;
    TextView heading;
    TextView opt;
    EditText nameF;
    EditText phF;
    EditText passF;
    Button goBtn;
    Button logoutBtn;

    static String status = "";


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference("Users");
    static List<String> User = new ArrayList<>();
    static List<String> UserAds = new ArrayList<>();
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        menu = findViewById(R.id.menu);
        card = findViewById(R.id.card);
        AdCard = findViewById(R.id.AdCard);
        heading = findViewById(R.id.Heading);
        opt = findViewById(R.id.opt);
        nameF = findViewById(R.id.Name);
        phF = findViewById(R.id.Nmbr);
        passF = findViewById(R.id.pass);
        goBtn = findViewById(R.id.goBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        AdImg = findViewById(R.id.AdImg);
        AdImgUrlF = findViewById(R.id.ImgUrlF);
        AdTitleF = findViewById(R.id.AdTitleF);
        AdDetailsF = findViewById(R.id.AdDetailsF);
        AdPriceF = findViewById(R.id.AdPriceF);
        AdPostBtn = findViewById(R.id.AdPostBtn);


        loading = new ProgressDialog(MainActivity.this);
        loading.setCancelable(false);
        loading.setMessage("Please Wait.");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        showLogin();

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("login")){
                    loading.show();
                    UserAds = new ArrayList<>();
                    if (!phF.getText().toString().trim().isEmpty()){
                        status = "MainLogin"+phF.getText().toString();
                        UsersRef.child(phF.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (status.equals("MainLogin"+phF.getText().toString())) {
                                    status = "login";
                                    List<String> userDetails = new ArrayList<String>();
                                    if (snapshot.getValue()!=null) {
                                        for (DataSnapshot value : snapshot.getChildren()) {
                                            if (value.getValue() != null) {
                                                if (!value.getKey().equals("Ads"))
                                                    userDetails.add(value.getValue().toString());
                                                else {
                                                    for (DataSnapshot ad : value.getChildren()){
                                                        UserAds.add(ad.getKey());
                                                    }
                                                }
                                            }
                                        }
                                        if (passF.getText().toString().trim().equals(userDetails.get(1))) {
                                            User = userDetails;
                                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                            loading.dismiss();
                                            showMenu();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                                            loading.dismiss();
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                }
                                else loading.dismiss();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                loading.dismiss();
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                }else if (status.equals("signup")){
                    //
                    if (!(phF.getText().toString().trim().isEmpty() && nameF.getText().toString().trim().isEmpty() && passF.getText().toString().trim().isEmpty())) {
                        Map<String, String> Data = new HashMap<String, String>();
                        Data.put("Name", nameF.getText().toString().trim());
                        Data.put("Phone", phF.getText().toString().trim());
                        Data.put("Pass", passF.getText().toString().trim());
                        Data.put("Ads", "");
                        UsersRef.child(phF.getText().toString().trim()).setValue(Data);
                        status = "MainSignUp"+phF.getText().toString();
                        UsersRef.child(phF.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (status.equals("MainSignUp"+phF.getText().toString())) {
                                    status = "signup";
                                    User = new ArrayList<>();
                                    for (DataSnapshot detail: snapshot.getChildren()) {
                                        User.add(detail.getValue().toString().trim());
                                    }
                                    Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    loading.dismiss();
                                    showLogin();
                                    new android.app.AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Welcome "+User.get(0)+",")
                                            .setMessage("You can use "+User.get(2)+" number to login next time.")
                                            .setPositiveButton("Start Exploring", null)
                                            .setIcon(R.drawable.icon)
                                            .show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                loading.dismiss();
                            }
                        });


                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter Details", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                    loading.dismiss();
                    //
                }
            }
        });
        opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("login")){
                    showSignup();
                }else if (status.equals("signup")){
                    showLogin();
                }
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");
                User = new ArrayList<>();
                UserAds = new ArrayList<>();
                nameF.setText("");
                passF.setText("");
                phF.setText("");
                }
        });

        findViewById(R.id.postBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdCard();
                findViewById(R.id.AdDeleteBtn).setVisibility(View.GONE);
                AdTitleF.setEnabled(true);
                findViewById(R.id.ImgLoadBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(AdImgUrlF.getText().toString().trim()).into(AdImg);
                    }
                });
            }
        });
        AdPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (!(AdTitleF.getText().toString().trim().isEmpty() && AdPriceF.getText().toString().trim().isEmpty() && AdDetailsF.getText().toString().trim().isEmpty() && AdImgUrlF.getText().toString().trim().isEmpty())) {
                    Map<String, String> Data = new HashMap<String, String>();
                    Data.put("Title", AdTitleF.getText().toString().trim());
                    Data.put("ImgUrl", AdImgUrlF.getText().toString().trim());
                    Data.put("Details", AdDetailsF.getText().toString().trim());
                    Data.put("Price", AdPriceF.getText().toString().trim());
                    Data.put("xName", User.get(0));
                    Data.put("xPhone", User.get(2));
                    UsersRef.child(User.get(2)).child("Ads").child(AdTitleF.getText().toString().trim()).setValue(Data);
                    status = "PostAd"+AdTitleF.getText().toString();
                    UsersRef.child(User.get(2)).child("Ads").child(AdTitleF.getText().toString().trim()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (status.equals("PostAd"+AdTitleF.getText().toString())) {
                                status = "menu";
                                Toast.makeText(getApplicationContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                showMenu();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loading.dismiss();
                        }
                    });


                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter All Details", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
                loading.dismiss();
                //
            }
        });
        findViewById(R.id.adsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(User.get(2)).child("Ads").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserAds = new ArrayList<>();
                            for (DataSnapshot ad : snapshot.getChildren() ) {
                                UserAds.add(ad.getKey());
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Ad to Edit");
                String[] opt = new String[UserAds.size()];
                for (int i=0;i<UserAds.size();i++){
                    opt[i] = UserAds.get(i);
                }
                builder.setItems(opt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        status = User.get(2)+opt[which];
                        UsersRef.child(User.get(2)).child("Ads").child(opt[which]).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (status.equals(User.get(2)+opt[which])){
                                    status = "menu";
                                    List <String> AdDetails = new ArrayList<>();
                                    for (DataSnapshot detail: snapshot.getChildren()) {
                                        AdDetails.add(detail.getValue().toString());
                                    }
                                    showAdCard();

                                    AdImgUrlF.setText(AdDetails.get(1));
                                    AdTitleF.setText(AdDetails.get(3));
                                    AdTitleF.setEnabled(false);
                                    AdDetailsF.setText(AdDetails.get(0));
                                    AdPriceF.setText(AdDetails.get(2));
                                    Picasso.get().load(AdDetails.get(1)).into(AdImg);

                                    findViewById(R.id.AdDeleteBtn).setVisibility(View.VISIBLE);
                                    findViewById(R.id.AdDeleteBtn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UsersRef.child(User.get(2)).child("Ads").child(opt[which]).removeValue();
                                            status = "DeleteAd"+AdTitleF.getText().toString();
                                            UsersRef.child(User.get(2)).child("Ads").child(opt[which]).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (status.equals("DeleteAd"+AdTitleF.getText().toString())) {
                                                        status = "menu";
                                                        Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                        loading.dismiss();
                                                        showMenu();
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    loading.dismiss();
                                                }
                                            });
                                            loading.dismiss();
                                            //
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        }
                });
                builder.create().show();
            }
        });

        findViewById(R.id.HomeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (status.equals("menu"))
            super.onBackPressed();
        else
            showMenu();
    }

    void showLogin(){
        menu.setVisibility(View.GONE);
        card.setVisibility(View.VISIBLE);
        AdCard.setVisibility(View.GONE);
        heading.setText("LOGIN USER");
        nameF.setVisibility(View.GONE);
        opt.setText("Create Account?");
        goBtn.setText("Login");
        status="login";
    }
    void showSignup(){
        menu.setVisibility(View.GONE);
        card.setVisibility(View.VISIBLE);
        AdCard.setVisibility(View.GONE);
        heading.setText("REGISTER NEW USER");
        nameF.setVisibility(View.VISIBLE);
        opt.setText("Already Have Account?");
        goBtn.setText("Save");
        status="signup";

    }
    void showMenu(){
        menu.setVisibility(View.VISIBLE);
        card.setVisibility(View.GONE);
        AdCard.setVisibility(View.GONE);
        status="menu";
    }

    void showAdCard(){
        menu.setVisibility(View.GONE);
        card.setVisibility(View.GONE);
        AdCard.setVisibility(View.VISIBLE);
        AdImgUrlF.setText("");
        AdTitleF.setText("");
        AdDetailsF.setText("");
        AdPriceF.setText("");
        status="AdCard";
    }
}