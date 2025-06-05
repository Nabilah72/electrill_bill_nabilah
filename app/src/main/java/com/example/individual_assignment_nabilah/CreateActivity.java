package com.example.individual_assignment_nabilah;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    Button btnSave, btnBack;
    EditText editMonth, editUnit, editRebate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DataHelper(this);

        editMonth = findViewById(R.id.editMonth);
        editUnit = findViewById(R.id.editUnit);
        editRebate = findViewById(R.id.editRebate);
        btnSave = findViewById(R.id.button1);
        btnBack = findViewById(R.id.button2);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = editMonth.getText().toString().trim();
                double unit = Double.parseDouble(editUnit.getText().toString().trim());
                double rebate = Double.parseDouble(editRebate.getText().toString().trim());

                double totalCharges = BillCalculator.calculateTotalCharges(unit);
                double finalCost = BillCalculator.applyRebate(totalCharges, rebate);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO bill (month, unit, rebate, totalCharges, finalCost) VALUES ('" +
                        month + "', '" +
                        unit + "', '" +
                        rebate + "', '" +
                        totalCharges + "', '" +
                        finalCost + "')");

                Toast.makeText(getApplicationContext(), "Data Successfully Added", Toast.LENGTH_SHORT).show();
                MainActivity.ma.RefreshList();
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
