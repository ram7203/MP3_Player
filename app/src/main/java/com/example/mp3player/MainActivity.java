package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        //prompt user for storage permissions
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Runtime permission granted!", Toast.LENGTH_SHORT).show();
                        ArrayList<File> songs = fetching(Environment.getExternalStorageDirectory());
                        String [] items = new String[songs.size()];
                        for(int i=0;i< songs.size();i++)
                        {
                            items[i] = songs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(MainActivity.this, NowPlaying.class);
                                String currentSong = listView.getItemAtPosition(i).toString();
                                intent.putExtra("songList", songs);
                                intent.putExtra("currentSong", currentSong);
                                intent.putExtra("position", i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                        //if user denies permission the first time ask again when opened next
                    }
                })
                .check();
    }
    public ArrayList<File> fetching(File file)
    {
        ArrayList<File> arraylist = new ArrayList();
        File [] songs = file.listFiles();
        if(songs != null)
        {
            for(File keyfile : songs)
            {
                if(!keyfile.isHidden() && keyfile.isDirectory())
                {
                    arraylist.addAll(fetching(keyfile));
                }
                else
                {
                    if(keyfile.getName().endsWith(".mp3") && !keyfile.getName().startsWith("."))
                    {
                        arraylist.add(keyfile);
                    }
                }
            }
        }
        return arraylist;
    }
}