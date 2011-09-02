package com.maunumental.dnd.conditiontracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NotesEditorActivity extends Activity {

	private Button doneButton;
	private EditText titleEdit, bodyEdit;

	
	//set to -1 if there is no position passed in via the intent, this gets passed back to 
	//the condition tracker so that it knows wether to add a new note to the list or replace
	//a note that just got edited
	private int position;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_editor_activity);
        
        titleEdit = (EditText)findViewById(R.id.notes_editor_title_edit);
        bodyEdit = (EditText)findViewById(R.id.notes_editor_body_edit);
        doneButton = (Button)findViewById(R.id.notes_editor_save_note_button);
        
        
        Intent data = getIntent();
        String title = data.getStringExtra("title");
        String body = data.getStringExtra("body");
        position = data.getIntExtra("position", -1);
        if(title != null)
        	titleEdit.setText(title);
        if(body != null)
        	bodyEdit.setText(body);
        
        setClickers();
        
       
        titleEdit.requestFocus();
    }
    
    
    
    private void setClickers()
    {
    	doneButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				sendDataBack();
			}
		});
        
    }
    
    private void sendDataBack()
	{
    	String titleText = titleEdit.getText().toString();
		String bodyText = bodyEdit.getText().toString();
		
		titleText = titleText.trim();
		bodyText = bodyText.trim();
		
		if(titleText.length() == 0)
		{	
			Toast.makeText(NotesEditorActivity.this, "A title is required.", 
					Toast.LENGTH_LONG).show();
			return;
		}
		
		Intent data = new Intent();
		data.putExtra("body", bodyText);
		data.putExtra("title", titleText);
		data.putExtra("position", position);	//important...read comment for field
		setResult(Activity.RESULT_OK, data);
		finish();
	}
	
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
    
    
    // This will be called either automatically for you on 2.0
    // or later, or by the code above on earlier versions of the
    // platform.
    public void onBackPressed() 
    {
		//ask the user if he/she wants to exit and cancel or exit and keep edits
		DialogInterface.OnClickListener dialogClickListener = 
			new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //weird...but I need to call super.onBackPressed() and this is
		        	//how I am going to accomplish it...so take that!
		        	sendDataBack();
		            break;
		        case DialogInterface.BUTTON_NEGATIVE:
		            //weird...but I need to call super.onBackPressed() and this is
		        	//how I am going to accomplish it...so take that!
		        	callSuperOnBackPressed();
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to exit and cancel the edits you have made or " +
				"do you want to save the edits?").
			setPositiveButton("Save Edits.", dialogClickListener).
		    setNegativeButton("Cancel Edits", dialogClickListener).show();    			
 
      
    }

    private void callSuperOnBackPressed()
    {
    	super.onBackPressed();
    }
    
}
