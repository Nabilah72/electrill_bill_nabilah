package com.example.individual_assignment_nabilah;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String[] register;
    int[] recordIds;
    ListView ListView01;
    protected Cursor cursor;
    DataHelper dbcenter;
    public static MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // This layout includes the header.xml via <include>

        // Set header title text
        TextView title = findViewById(R.id.header_title);
        title.setText("Electricity Bills");

        // Set back button behavior (optional, can finish activity or navigate)
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Or navigate elsewhere if needed
            }
        });

        // Floating Action Button to create new record
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });

        ma = this;
        dbcenter = new DataHelper(this);
        RefreshList();
    }

    public void RefreshList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT no, month, finalCost FROM bill", null);

        register = new String[cursor.getCount()];
        recordIds = new int[cursor.getCount()];

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            recordIds[i] = cursor.getInt(cursor.getColumnIndexOrThrow("no"));
            String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("finalCost"));
            register[i] = month + " - RM " + String.format("%.2f", finalCost);
        }

        ListView01 = findViewById(R.id.listView1);
        ListView01.setDivider(new ColorDrawable(Color.parseColor("#4634A7")));
        ListView01.setDividerHeight(1);

        ListView01.setAdapter(new ArrayAdapter<>(this,R.layout.list_item_black, register));
        ListView01.setSelected(true);

        ListView01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int selectedId = recordIds[position];
                final CharSequence[] dialogitem = {"View Details", "Update Record", "Delete Record"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose an option");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent viewIntent = new Intent(getApplicationContext(), ViewActivity.class);
                                viewIntent.putExtra("id", selectedId);
                                startActivity(viewIntent);
                                break;
                            case 1:
                                Intent updateIntent = new Intent(getApplicationContext(), UpdateActivity.class);
                                updateIntent.putExtra("id", selectedId);
                                startActivity(updateIntent);
                                break;
                            case 2:
                                SQLiteDatabase db = dbcenter.getWritableDatabase();
                                db.execSQL("DELETE FROM bill WHERE no = " + selectedId);
                                Toast.makeText(getApplicationContext(), "Data Successfully Deleted", Toast.LENGTH_SHORT).show();
                                RefreshList();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        TextView title = findViewById(R.id.header_title);
        title.setText("Electricity Bills");

        ImageButton backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> onBackPressed());

        ((ArrayAdapter<?>) ListView01.getAdapter()).notifyDataSetInvalidated();
    }
}
