package com.example.individual_assignment_nabilah;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ViewActivity extends AppCompatActivity {

    FirebaseAuth auth;
    protected Cursor cursor;
    DataHelper dbHelper;
    Button btnBack;
    TextView textMonth, textUnit, textTotalCharges, textRebate, textFinalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        dbHelper = new DataHelper(this);

        textMonth = findViewById(R.id.textMonth);
        textUnit = findViewById(R.id.textUnit);
        textTotalCharges = findViewById(R.id.textTotalCharges);
        textRebate = findViewById(R.id.textRebate);
        textFinalCost = findViewById(R.id.textFinalCost);
        btnBack = findViewById(R.id.buttonBack);

        int id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM bill WHERE no = " + id, null);
            if (cursor.moveToFirst()) {
                textMonth.setText(cursor.getString(cursor.getColumnIndexOrThrow("month")));
                textUnit.setText(cursor.getString(cursor.getColumnIndexOrThrow("unit")));

                // Format numeric values
                double totalChargesVal = cursor.getDouble(cursor.getColumnIndexOrThrow("totalCharges"));
                double finalCostVal = cursor.getDouble(cursor.getColumnIndexOrThrow("finalCost"));
                String rebateVal = cursor.getString(cursor.getColumnIndexOrThrow("rebate"));

                textTotalCharges.setText("RM " + String.format("%.2f", totalChargesVal));
                textFinalCost.setText("RM " + String.format("%.2f", finalCostVal));
                textRebate.setText(rebateVal + "%");
            }
        }

        btnBack.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.header_title);
        title.setText("View Bill");

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> onBackPressed());

        ImageButton logoutBtn = findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(v -> signout());
    }

    public void signout() {
        auth.signOut();
        Toast.makeText(this, "Logged Out Successfully.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }
}
