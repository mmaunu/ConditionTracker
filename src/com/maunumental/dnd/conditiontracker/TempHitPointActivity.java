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

public class TempHitPointActivity extends Activity {

	private Button doneButton;
	private EditText textarea;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_hit_point_activity);        
        
        doneButton = (Button)findViewById(R.id.temp_hit_point_activity_button);
        textarea = (EditText)findViewById(R.id.temp_hit_point_activity_edittext);
        
        setClickers();
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
    	String temp = textarea.getText().toString();
		int thp = 0;
		try {
			if(temp.length() > 0)
				thp = Integer.parseInt(temp);
		}
		catch(NumberFormatException nfe)
		{
			Toast.makeText(TempHitPointActivity.this, "you must type numbers only", 
					Toast.LENGTH_SHORT).show();
		}
		if(thp < 0)
		{
			Toast.makeText(TempHitPointActivity.this, "enter amount as a positive number", 
					Toast.LENGTH_SHORT).show();
			thp = -thp;
		}
		Intent data = new Intent();
		data.putExtra("amountEntered", thp);
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
