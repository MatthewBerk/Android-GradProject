package com.example.aspectideaone;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.matthew.gradProject.DataStorage;
import org.json.JSONException;

import java.io.IOException;


//todo remake test where app tries to send out users input since original accidentally got deleted.
public class UserInputActivity extends AppCompatActivity {

    private TextView textView1;
    private EditText editText1;
    private Button aButton;
    private Button aButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);
        textView1 = (TextView) findViewById(R.id.Instructions);
        textView1.setText("Please type in what you consider sensitive data. For example your ssn number.");

        editText1 = (EditText) findViewById(R.id.userInput);

        aButton = (Button) findViewById(R.id.grabText);
        aButton2 = (Button) findViewById(R.id.goBack);

        aButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String temp = editText1.getText().toString();
                try {
                    DataStorage.userInputAsSensitiveData(temp,"UsersChoice");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        aButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

    }
}
