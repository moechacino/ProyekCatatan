package com.kelompok1.insertview;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class InsertView extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_STORAGE = 100;
    EditText edtFileName, edtContent;
    Button btnSave;
    String fileName;
    boolean isContentChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        edtFileName = findViewById(R.id.editFilename);
        edtContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        // Listener untuk mengecek perubahan teks di EditText edtContent
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isContentChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileName = extras.getString("filename");
            edtFileName.setText(fileName);
            getSupportActionBar().setTitle("Ubah Catatan");
        } else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (periksaIzinPenyimpanan()) {
//                bacaFile();
//            }
//        } else {
//            bacaFile();
//        }
        bacaFile();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSave) {

            if (isContentChanged) {
                btnSave.setBackgroundColor(Color.RED);
                tampilkanDialogKonfirmasiPenyimpanan();
            }
        }
    }

//    public boolean periksaIzinPenyimpanan() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bacaFile();
                }
                break;
        }
    }

    void bacaFile() {
        String path = getExternalFilesDir(null).toString() + "/kominfo.proyek";
        File file = new File(path, edtFileName.getText().toString());
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            edtContent.setText(text.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void buatDanUbah() {
        String path = getExternalFilesDir(null).toString() + "/kominfo.proyek";
        File parent = new File(path);
        if (!parent.exists()) {
            parent.mkdirs(); // Buat direktori jika belum ada
        }
        File file = new File(path, edtFileName.getText().toString());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            streamWriter.write(edtContent.getText().toString());
            streamWriter.flush();
            streamWriter.close();
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "Catatan disimpan di " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void tampilkanDialogKonfirmasiPenyimpanan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Simpan Catatan");
        builder.setMessage("Apakah Anda yakin ingin menyimpan Catatan ini?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                buatDanUbah();
            }
        });
        builder.setNegativeButton("TIDAK", null).show();
    }

    @Override
    public void onBackPressed() {
        if (isContentChanged) {
            tampilkanDialogKonfirmasiPenyimpanan();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
