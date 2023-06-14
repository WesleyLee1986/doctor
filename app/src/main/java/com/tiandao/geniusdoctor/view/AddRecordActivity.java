package com.tiandao.geniusdoctor.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tiandao.geniusdoctor.R;

import database.model.TransferConstants;
import database.table.Department;
import database.table.Doctor;
import database.table.Hospital;
import database.table.Table;
import database.table.TableColumnDef;

public class AddRecordActivity extends AppCompatActivity {
    private String tableName;
    private int recordId;
    private Table table;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_record_layout);
        initData();

        findViewById(R.id.icon_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btnSave = findViewById(R.id.btn_save);
        if (recordId > 0) {
            btnSave.setText("修改");
        } else {
            btnSave.setText("保存");
        }

        LinearLayout llFields = findViewById(R.id.ll_fields);
        for (TableColumnDef tableColumnDef : table.getColumnDef()) {
            EditText editText = new EditText(this);
            editText.setHint(tableColumnDef.getColumn_desc());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, dp2px(6.0f), 0, 0);
            llFields.addView(editText);
        }
    }

    private Table getTable() {
        switch (tableName) {
            case Hospital.TABLE_NAME:
                return new Hospital();
            case Department.TABLE_NAME:
                return new Department();
            case Doctor.TABLE_NAME:
                return new Doctor();
        }

        return null;
    }

    /**
     * 密度转换为像素值
     */
    public int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void initData() {
        Intent intent = getIntent();
        tableName = intent.getStringExtra(TransferConstants.TableName);
        recordId = intent.getIntExtra(TransferConstants.recordId, -1);
        table = getTable();
    }
}
