package com.oblaco.todolist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import org.xmlpull.v1.sax2.Driver;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.CompoundButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.*;

import java.util.List;
import java.util.TreeSet;

public class MainActivity extends ListActivity implements OnTouchListener{

    //создание адаптара
    private MyCustomAdapter mAdapter;

    //класс с задачами
    public class Todos{
        private int id;
        private String text;
        private Boolean isCompleted;
        private String project_id;
        private String created_at;
        private String updated_at;

        public String getText() {
            return text;
        }

        //методы доступа к полям
        public Boolean getCompleted() {
            return isCompleted;
        }

        public int getId() {
            return id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getProject_id() {
            return project_id;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setProject_id(String project_id) {
            this.project_id = project_id;
        }
    }


    //класс с названием задач
    public class Project{
        private int id;
        private String title;
        private List<Todos> todos;

        public Project(int id, String title, List<Todos> todos)
        {
            this.id = id;
            this.title = title;
            this.todos = todos;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public List<Todos> getTodos() {
            return todos;
        }
    }


    //главная функция
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //кастомный шрифт
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //лист с классами проектов
        final List<Project> projects = new ArrayList<Project>();

        Connection conn = null;
        Statement statement = null;

        /*
        try
        {
            Class.forName("Driver");
            conn = DriverManager.getConnection(
                    "postgres://flxekmxtwhglsc:6b8bb611979c80258c965690b418077ea9f02e8e2c2d8f3b02d1487497111d28@ec2\n-174-129-227-80.compute-1.amazonaws.com:5432/d18poj5em2p0h6",
                    "flxekmxtwhglsc",
                    "6b8bb611979c80258c965690b418077ea9f02e8e2c2d8f3b02d1487497111d28");
            conn.setAutoCommit(false);
            Log.v("Database", "Info -> Database connection successful establishment.");


            statement = conn.createStatement();
            ResultSet result = statement.executeQuery( "SELECT * FROM projects;" );
            String title = result.getString("title");
            result.close();
            statement.close();
            conn.commit();
            Log.v("Check", "Data -> " + title);
        }
        catch (SQLException e)
        {
            Log.v("Database", "Info -> Database connection failed. More: ");
            e.printStackTrace();
        }
        */

        //доступ к JSON
        Ion.with(this)
                .load("http://besttotdolistever.herokuapp.com/todos/json")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                if (result != null) {
                    for (final JsonElement projectJsonElement : result) {
                        projects.add(new Gson().fromJson(projectJsonElement, Project.class));
                    }
                }

                if(projects != null) {
                    final String projectsArray[] = new String[projects.size()];
                    final Bundle bundle = getIntent().getExtras();

                    if (bundle != null) {
                        final String[] newData = bundle.getStringArray("kew_word");
                        Project proj = projects.get(Integer.parseInt(newData[0]));
                        List<Todos> todos = proj.getTodos();
                    }

                    mAdapter = new MyCustomAdapter();
                    for (int i = 0; i < projects.size(); i++) {
                        Project prj = projects.get(i);
                        projectsArray[i] = prj.getTitle();
                        mAdapter.addSeparatorItem(prj.getTitle());
                        List<Todos> todos = prj.getTodos();
                        for (int j = 0; j < todos.size(); j++) {
                            Todos tds = todos.get(j);
                            mAdapter.addItem(tds.getText());
                        }
                    }

                    setListAdapter(mAdapter);

                    FloatingActionButton btnActTwo = (FloatingActionButton) findViewById(R.id.floatingActionButton);
                    btnActTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("EXTRA_SESSION_ID", projectsArray);
                            Intent intent = new Intent(getBaseContext(), projectsList.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    /*
                    ViewHolder holder = new ViewHolder();
                    holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            Todos totoInFocus = (Todos) compoundButton.getTag();
                            if (totoInFocus.isCompleted == isChecked) return;
                        }
                    });
                    */
                }
            }
        });
    }


    //Adapter Class
    private class MyCustomAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;

        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        public int getCount() {
            return mData.size();
        }

        public String getItem(int position) {
            return mData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            int type = getItemViewType(position);
            System.out.println("getView " + position + " " + convertView + " type = " + type);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_ITEM:
                        convertView = mInflater.inflate(R.layout.item1, null);
                        holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);
                        holder.textView = (TextView)convertView.findViewById(R.id.text);
                        break;
                    case TYPE_SEPARATOR:
                        convertView = mInflater.inflate(R.layout.item2, null);
                        holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText(mData.get(position));
            return convertView;
        }

    }

    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }

    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }


}

