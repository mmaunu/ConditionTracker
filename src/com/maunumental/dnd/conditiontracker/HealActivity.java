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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class HealActivity extends Activity {

	private Button doneButton;
	private EditText textarea;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heal_activity);
        
        textarea = (EditText)findViewById(R.id.heal_activity_textarea);
        doneButton = (Button)findViewById(R.id.heal_activity_button);
        
        //set a default surge option...no surge
        RadioButton noSurge = (RadioButton)findViewById(R.id.noSurgeRadioButton);
        noSurge.setChecked(true);
        
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
		int hp = 0;
		try {
			if(temp.length() > 0)
				hp = Integer.parseInt(temp);
		}
		catch(NumberFormatException nfe)
		{
			Toast.makeText(HealActivity.this, "you must type numbers only", 
					Toast.LENGTH_SHORT).show();
		}
		
		Intent data = new Intent();
		//note...this can be negative!!
		data.putExtra("amountEntered", hp);
		RadioGroup rg = (RadioGroup)findViewById(R.id.surgeRadioGroup);
		int radioID = rg.getCheckedRadioButtonId();
		if(radioID == R.id.noSurgeRadioButton)
		{
			data.putExtra("surge", 0);
		} 
		else if (radioID == R.id.plusSurgeRadioButton)
		{
			data.putExtra("surge", 1);
		}
		else if (radioID == R.id.plusTwoSurgesRadioButton)
		{
			data.putExtra("surge", 2);
		}
		else if (radioID == R.id.asIfSurgeRadioButton)
		{
			data.putExtra("surge", -1);
		}
		else if (radioID == R.id.asIfTwoSurgesRadioButton)
		{
			data.putExtra("surge", -2);
		}
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
