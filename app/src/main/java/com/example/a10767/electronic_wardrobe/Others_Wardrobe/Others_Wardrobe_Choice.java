package com.example.a10767.electronic_wardrobe.Others_Wardrobe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.R;

public class Others_Wardrobe_Choice extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout others_wardrobe_choice_return;
    private TextView others_jacket_wardrobe_bt;
    private TextView others_skirt_wardrobe_bt;
    private TextView others_pants_wardrobe_bt;
    private TextView others_shoe_wardrobe_bt;
    private TextView others_suit_wardrobe_bt;
    private TextView others_coat_wardrobe_bt;
    private TextView others_cap_wardrobe_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_wardrobe_choice);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    private void initView() {
        others_wardrobe_choice_return.setOnClickListener(this);
        others_jacket_wardrobe_bt.setOnClickListener(this);
        others_skirt_wardrobe_bt.setOnClickListener(this);
        others_pants_wardrobe_bt.setOnClickListener(this);
        others_shoe_wardrobe_bt.setOnClickListener(this);
        others_suit_wardrobe_bt.setOnClickListener(this);
        others_coat_wardrobe_bt.setOnClickListener(this);
        others_cap_wardrobe_bt.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Others_Wardrobe_Choice.this, Others_Add.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    private void initUI() {
        others_wardrobe_choice_return = findViewById(R.id.others_wardrobe_choice_return);
        others_jacket_wardrobe_bt = findViewById(R.id.others_jacket_wardrobe_bt);
        others_skirt_wardrobe_bt = findViewById(R.id.others_skirt_wardrobe_bt);
        others_pants_wardrobe_bt = findViewById(R.id.others_pants_wardrobe_bt);
        others_shoe_wardrobe_bt = findViewById(R.id.others_shoe_wardrobe_bt);
        others_suit_wardrobe_bt = findViewById(R.id.others_suit_wardrobe_bt);
        others_coat_wardrobe_bt = findViewById(R.id.others_coat_wardrobe_bt);
        others_cap_wardrobe_bt = findViewById(R.id.others_cap_wardrobe_bt);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.others_wardrobe_choice_return:
                startActivity(new Intent(Others_Wardrobe_Choice.this, Others_Add.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.others_jacket_wardrobe_bt:
                startActivity(new Intent(Others_Wardrobe_Choice.this, Others_Jacket.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.others_skirt_wardrobe_bt:
                startActivity(new Intent(Others_Wardrobe_Choice.this, Others_Skirt.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.others_pants_wardrobe_bt:
                startActivity(new Intent(Others_Wardrobe_Choice.this, Others_Pants.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.others_shoe_wardrobe_bt:
                startActivity(new Intent(Others_Wardrobe_Choice.this, Others_Shoe.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.others_suit_wardrobe_bt:
                startActivity(new Intent(Others_Wardrobe_Choice.this, Others_Suit.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.others_coat_wardrobe_bt:
                startActivity(new Intent(Others_Wardrobe_Choice.this,Others_Coat.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.others_cap_wardrobe_bt:
                startActivity(new Intent(Others_Wardrobe_Choice.this,Others_Cap.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
        }
    }
}
