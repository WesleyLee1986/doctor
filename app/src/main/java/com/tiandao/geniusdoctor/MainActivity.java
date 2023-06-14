package com.tiandao.geniusdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tiandao.geniusdoctor.view.AddRecordActivity;

import java.util.ArrayList;
import java.util.List;

import database.model.TransferConstants;
import database.table.Department;
import database.table.Doctor;
import database.table.Hospital;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        预定义变量
        Spinner spinner;
        List<String> listForSpinner = new ArrayList<>();
        ArrayAdapter<String> adapterForSpinner;
        //变量初始化
        spinner = findViewById(R.id.spinner);// 引用Spinner控件
        //给字符串数组赋初值
        listForSpinner.add("C语言");
        listForSpinner.add("Python");
        listForSpinner.add("Java");
        listForSpinner.add("C++");
        listForSpinner.add("C语言");
        listForSpinner.add("Python");
        listForSpinner.add("Java");
        listForSpinner.add("C++");
        listForSpinner.add("C语言");
        listForSpinner.add("Python");
        listForSpinner.add("Java");
        listForSpinner.add("C++");
        listForSpinner.add("C语言");
        listForSpinner.add("Python");
        listForSpinner.add("Java");
        listForSpinner.add("C++");
        listForSpinner.add("C语言");
        listForSpinner.add("Python");
        listForSpinner.add("Java");
        listForSpinner.add("C++");
        listForSpinner.add("C语言");
        listForSpinner.add("Python");
        listForSpinner.add("Java");
        listForSpinner.add("C++");
        listForSpinner.add("C语言");
        listForSpinner.add("Python");
        listForSpinner.add("Java");
        listForSpinner.add("C++");
        adapterForSpinner = new ArrayAdapter<>(MainActivity.this, R.layout.item_for_custom_spinner, listForSpinner);
        spinner.setAdapter(adapterForSpinner);

        findViewById(R.id.bt_add_hospital).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
                intent.putExtra(TransferConstants.TableName, Hospital.TABLE_NAME);
                intent.putExtra(TransferConstants.recordId, -1);
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_add_department).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
                intent.putExtra(TransferConstants.TableName, Department.TABLE_NAME);
                intent.putExtra(TransferConstants.recordId, -1);
                startActivity(intent);
            }
        });

        findViewById(R.id.bt_add_doctor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
                intent.putExtra(TransferConstants.TableName, Doctor.TABLE_NAME);
                intent.putExtra(TransferConstants.recordId, -1);
                startActivity(intent);
            }
        });

    }
}