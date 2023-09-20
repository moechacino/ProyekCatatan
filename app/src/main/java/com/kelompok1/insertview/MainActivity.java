package com.kelompok1.insertview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Aplikasi Catatan Proyek");

        ListView listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, InsertView.class);
                Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
                intent.putExtra("filename", data.get("name").toString());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
               tampilkanDialogKonfirmasiHapusCatatan(data.get("name").toString());
               return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!periksaIzinPenyimpanan()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            }
        }

        refreshListView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshListView();
                }
                break;
        }
    }

    private boolean periksaIzinPenyimpanan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            return false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }


    private void refreshListView() {
        String path = getExternalFilesDir(null).toString() + "/kominfo.proyek";
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            ArrayList<Map<String, Object>> itemList = new ArrayList<>();

            for (File file : files) {
                Map<String, Object> listItem = new HashMap<>();
                listItem.put("name", file.getName());
                listItem.put("date", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(file.lastModified())));
                itemList.add(listItem);
            }

            String[] from = {"name", "date"};
            int[] to = {android.R.id.text1, android.R.id.text2};
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, itemList, android.R.layout.simple_list_item_2, from, to);

            ListView listView = findViewById(R.id.listView);
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_tambah){
            Intent intent = new Intent(this, InsertView.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    void tampilkanDialogKonfirmasiHapusCatatan(final String filename){
        new  AlertDialog.Builder(this).setTitle("Hapus Catatan ini?").setMessage("Yakin?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                hapusFile(filename);
            }
        }).setNegativeButton(android.R.string.no, null).show();
    }

    void hapusFile(String filename){
        String path = getExternalFilesDir(null).toString() + "/kominfo.proyek";
        File file = new File(path, filename);
        if(file.exists()){
            file.delete();
            refreshListView(); // Refresh list setelah menghapus catatan
        }
    }
}
