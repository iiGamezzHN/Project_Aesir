package com.uva.aesir.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uva.aesir.Database.PresetDatabase;
import com.uva.aesir.Database.WeightsDatabase;
import com.uva.aesir.Model.Weights;
import com.uva.aesir.R;

import java.util.ArrayList;

public class PresetExercise extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> weights = new ArrayList<String>();
    PresetDatabase db;
    WeightsDatabase database;
    Spinner one, two, three, four;
    String exerciseName, title;
    ProgressBar progressBar;
    private int progressStatus = 0;
    MediaPlayer ring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_exercise);
        database = WeightsDatabase.getInstance(getApplicationContext());
        db = PresetDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        exerciseName = intent.getStringExtra("exercise_name");
        title = intent.getStringExtra("title");

        Cursor cursor = db.selectExercises(exerciseName);
        cursor.moveToFirst();

        TextView txt = findViewById(R.id.entry_title);
        TextView description = findViewById(R.id.entry_description);
        ImageView image = findViewById(R.id.entry_image);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(progressStatus);

        txt.setText(cursor.getString(cursor.getColumnIndex("title")));
        description.setText(cursor.getString(cursor.getColumnIndex("description")));
        Picasso.get().load(cursor.getString(cursor.getColumnIndex("imgUrl")))
                .resize(400, 600)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .into(image);

        one = (Spinner) findViewById(R.id.set_1);
        two = (Spinner) findViewById(R.id.set_2);
        three = (Spinner) findViewById(R.id.set_3);
        four = (Spinner) findViewById(R.id.set_4);

        for (int i = 0; i < 40; i++) { // temp value, must eventually hold more than just 40, but gets ugly if list gets to long
            weights.add("" + i + "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weights);

        one.setAdapter(adapter);
        one.setOnItemSelectedListener(this);

        two.setAdapter(adapter);
        two.setOnItemSelectedListener(this);

        three.setAdapter(adapter);
        three.setOnItemSelectedListener(this);

        four.setAdapter(adapter);
        four.setOnItemSelectedListener(this);

        cursor.close();

        ring= MediaPlayer.create(PresetExercise.this, R.raw.epic);
        ring.start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timer, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.timer:
                startActivity(new Intent(this, TimerActivity.class));
        }
        return (super.onOptionsItemSelected(item));
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String txt = adapterView.getItemAtPosition(i).toString();
        progressStatus = 4;
        int check1 = 0;
        int check2 = 0;
        int check3 = 0;
        int check4 = 0;

        // update process bar
        for (int check = 0; check < 4; check++) {
            if (one.getSelectedItem().equals("0") && check1 == 0) {
                check1 += 1;
                progressStatus -= 1;
            } else if (two.getSelectedItem().equals("0") && check2 == 0) {
                check2 += 1;
                progressStatus -= 1;
            } else if (three.getSelectedItem().equals("0") && check3 == 0) {
                check3 += 1;
                progressStatus -= 1;
            } else if (four.getSelectedItem().equals("0") && check4 == 0) {
                check4 += 1;
                progressStatus -= 1;
            }
        }
        progressBar.setProgress(progressStatus);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onClickSubmit(View view) {
        ring.stop();
        Weights weights = new Weights(Integer.parseInt(one.getSelectedItem().toString()), Integer.parseInt(two.getSelectedItem().toString()), Integer.parseInt(three.getSelectedItem().toString()), Integer.parseInt(four.getSelectedItem().toString()), exerciseName);
        database.insert(weights);

        Intent intent = new Intent(this, Preset_detail.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    public void onBackPressed(){
        ring.stop();
        Intent intent = new Intent(this, Preset_detail.class);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}

