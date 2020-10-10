package com.example.pocketalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
//TODO improve the way the instruction text is shown.
    private EditText idInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idInput = findViewById(R.id.connectInput);
    }

    /**
     * When the connect button is pressed, go back to the previous activity. If an ID was entered, that gets sent back as an extra to be added to the database.
     */
    public void onConnect(View view) {
        Intent replyIntent = new Intent();
        String id = idInput.getText().toString();
        if (id.length() > 0) {
            replyIntent.putExtra("id", id);
            setResult(RESULT_OK, replyIntent);
        } else {
            setResult(RESULT_CANCELED, replyIntent);
        }

        finish();
    }

    /**
     * When the cancel button is pressed, go back to the previous activity.
     */
    public void onCancelConnect(View view) {
        Intent replyIntent = new Intent();
        setResult(RESULT_CANCELED, replyIntent);
        finish();
    }

    public void howToConnect(View view) {
        Intent intent = new Intent(this, RegisterInstructionsActivity.class);
        startActivity(intent);
    }
}