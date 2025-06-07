package com.example.individual_assignment_nabilah;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class UpdateActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DataHelper dbHelper;
    Button btnUpdate, btnCancel;
    AutoCompleteTextView editMonth;
    EditText editUnit;
    TextView textId;
    RadioGroup radioGroupRebate;

    String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        dbHelper = new DataHelper(this);
        textId = findViewById(R.id.editText1);
        editMonth = findViewById(R.id.editText2);
        editUnit = findViewById(R.id.editText3);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        btnUpdate = findViewById(R.id.button1);
        btnCancel = findViewById(R.id.btnCancel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_black, months);
        editMonth.setAdapter(adapter);
        editMonth.setOnClickListener(v -> editMonth.showDropDown());
        editMonth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) editMonth.showDropDown();
        });

        int id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            loadBillData(id);
        }

        btnUpdate.setOnClickListener(v -> updateBill(id));
        btnCancel.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.header_title);
        title.setText("Update Bill");

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> onBackPressed());

        ImageButton logoutBtn = findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(v -> signout());
    }

    private void loadBillData(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bill WHERE no = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            textId.setText(String.valueOf(id));
            editMonth.setText(cursor.getString(cursor.getColumnIndexOrThrow("month")), false);
            editUnit.setText(cursor.getString(cursor.getColumnIndexOrThrow("unit")));

            int rebateValue = cursor.getInt(cursor.getColumnIndexOrThrow("rebate"));  // get rebate as int
            for (int i = 0; i < radioGroupRebate.getChildCount(); i++) {
                View child = radioGroupRebate.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    // Compare rebate int to rb text after removing % sign
                    String rbText = rb.getText().toString().replace("%", "").trim();
                    try {
                        int rbValue = Integer.parseInt(rbText);
                        if (rbValue == rebateValue) {
                            rb.setChecked(true);
                            break;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        cursor.close();
    }

    private void updateBill(int id) {
        String month = editMonth.getText().toString().trim();
        String unitStr = editUnit.getText().toString().trim();
        int selectedRebateId = radioGroupRebate.getCheckedRadioButtonId();

        if (month.isEmpty() || unitStr.isEmpty() || selectedRebateId == -1) {
            Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isValidMonth = false;
        for (String m : months) {
            if (m.equalsIgnoreCase(month)) {
                isValidMonth = true;
                break;
            }
        }

        if (!isValidMonth) {
            Toast.makeText(this, "Please select a valid month from the list.", Toast.LENGTH_SHORT).show();
            return;
        }

        double unit;
        int rebate;
        try {
            unit = Double.parseDouble(unitStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for unit.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRebateId);
        try {
            String rebateText = selectedRadioButton.getText().toString().replace("%", "").trim();
            rebate = Integer.parseInt(rebateText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rebate value.", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalCharges = BillCalculator.calculateTotalCharges(unit);
        double finalCost = BillCalculator.applyRebate(totalCharges, rebate);

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "UPDATE bill SET month=?, unit=?, rebate=?, totalCharges=?, finalCost=? WHERE no=?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindString(1, month);
            stmt.bindDouble(2, unit);
            stmt.bindLong(3, rebate);  // rebate stored as int
            stmt.bindDouble(4, totalCharges);
            stmt.bindDouble(5, finalCost);
            stmt.bindLong(6, id);
            stmt.executeUpdateDelete();

            Toast.makeText(this, "Data Successfully Updated", Toast.LENGTH_SHORT).show();
            MainActivity.ma.RefreshList();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void signout() {
        auth.signOut();
        Toast.makeText(this, "Logged Out Successfully.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }
}
