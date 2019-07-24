package com.oblaco.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class projectsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_projects_list);


        ListView lst = (ListView) findViewById(R.id.projectlist);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
        Bundle bundle = getIntent().getExtras();
        String[] projectsArray = bundle.getStringArray("EXTRA_SESSION_ID");
        adapter.addAll(projectsArray);
        lst.setAdapter(adapter);

        EditText editText = (EditText) findViewById(R.id.editText);
        final String text = editText.getText().toString();
        final String id = "";

        lst.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                id = position;
            }
        });

        final String str[] = new String[]{id, text};

        ImageButton btnActFirst = (ImageButton) findViewById(R.id.imageButton);
        btnActFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putStringArray("kew_word", str);
                Intent intent=new Intent(projectsList.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ImageButton btnActSecond = (ImageButton) findViewById(R.id.imageButton2);
        btnActSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(projectsList.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
