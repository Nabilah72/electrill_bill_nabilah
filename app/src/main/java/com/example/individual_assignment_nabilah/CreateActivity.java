package com.example.individual_assignment_nabilah;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;

public class CreateActivity extends AppCompatActivity {

    FirebaseAuth auth;
    AutoCompleteTextView autoCompleteTextView;
    EditText editUnit;

    RadioGroup radioGroupRebate;

    Button btnSave, btnCancel;
    DataHelper dbHelper;

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

        auth = FirebaseAuth.getInstance();
        dbHelper = new DataHelper(this);
        autoCompleteTextView = findViewById(R.id.textMonth);
        editUnit = findViewById(R.id.editUnit);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        btnSave = findViewById(R.id.button1);
        btnCancel = findViewById(R.id.btnCancel);

        // Setup dropdown menu
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_black, months);
        autoCompleteTextView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            String month = autoCompleteTextView.getText().toString().trim();
            String unitStr = editUnit.getText().toString().trim();

            if (month.isEmpty() || unitStr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = radioGroupRebate.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getApplicationContext(), "Please select a rebate percentage", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = findViewById(selectedId);
            String rebateText = selectedRadio.getText().toString().replace("%", "");

            int rebate;
            double unit;

            try {
                rebate = Integer.parseInt(rebateText);  // changed here: parse rebate as int
                unit = Double.parseDouble(unitStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalCharges = BillCalculator.calculateTotalCharges(unit);
            double finalCost = BillCalculator.applyRebate(totalCharges, rebate);  // rebate as int

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT INTO bill (month, unit, rebate, totalCharges, finalCost) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{month, unit, rebate, totalCharges, finalCost});

            Toast.makeText(getApplicationContext(), "Data Successfully Added", Toast.LENGTH_SHORT).show();
            MainActivity.ma.RefreshList();
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());

        // Set title and back button in header
        TextView title = findViewById(R.id.header_title);
        title.setText("Add Bill");

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
