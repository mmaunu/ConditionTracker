package com.maunumental.dnd.conditiontracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigureCharacterActivity extends Activity {
	
	private EditText maxHPTextArea, surgeValueTextArea, maxSurgesTextArea, nameTextArea,
		maxPowerPtsTextArea, actionPtReminderTextArea;
	
	private boolean isNewCharacter;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_settings_activity);
        
        maxHPTextArea = (EditText)findViewById(R.id.config_max_hit_points);        
        surgeValueTextArea = (EditText)findViewById(R.id.config_surge_value);
        maxSurgesTextArea = (EditText)findViewById(R.id.config_max_number_surges); 
        actionPtReminderTextArea = (EditText)findViewById(R.id.config_action_point_reminder);
        nameTextArea = (EditText)findViewById(R.id.config_character_name);
        maxPowerPtsTextArea = (EditText)findViewById(R.id.config_power_points);
        
        Intent data = getIntent();
        int maxHitPoints = data.getIntExtra("maxHitPoints", -1);
        int maxSurges = data.getIntExtra("maxSurges", -1);
        int surgeValue = data.getIntExtra("surgeValue", -1);
        int maxPowerPts = data.getIntExtra("maxPowerPoints", -1);
        String charName = data.getStringExtra("name");
        String actionPointReminder = data.getStringExtra("actionPointReminder");
        //default to true...if it is false, then you can't edit the name edittext
        isNewCharacter = data.getBooleanExtra("isNewCharacter", true);
        
        if(isNewCharacter)
        	nameTextArea.setEnabled(true);
        else
        	nameTextArea.setEnabled(false);
        
        if(maxHitPoints != -1 && maxHitPoints != 0)
        	maxHPTextArea.setText(""+maxHitPoints);
        if(maxSurges != -1 && maxSurges != 0)
        	maxSurgesTextArea.setText(""+maxSurges);
        if(surgeValue != -1 && surgeValue !=0)
        	surgeValueTextArea.setText(""+surgeValue);
        if(maxPowerPts != -1 && maxPowerPts !=0)
        	maxPowerPtsTextArea.setText(""+maxPowerPts);
        if(charName != null && !(charName.equals("default")))
        	nameTextArea.setText(charName);
        if(actionPointReminder != null)
        	actionPtReminderTextArea.setText(actionPointReminder);
        
        setClickers();
        saveDataToPrefs();
    }

    
    private void setClickers()
    {
    	Button done = (Button)findViewById(R.id.configure_settings_activity_done_button);
    	done.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) 
			{
				String tempName = nameTextArea.getText().toString().trim();
				
				//name is required...
				if(tempName.length() == 0)
				{
					Toast.makeText(ConfigureCharacterActivity.this, "A name is required.", 
							Toast.LENGTH_LONG).show();
					return;
				}
				
				
				//if this is actually a new character that we are creating, check to see that
				//the name is unique...if a character already exists with that name, warn
				//the user, give option of cancelling and renaming or overwriting old character
				if(!isNewCharacter)
					sendDataBack();
				else
				{
					ArrayList<String> existingCharacterNames = FileIOHelper.
											getAllCharacterNames();
					
					//if unique name, close activity, otherwise ask user for guidance
					if(!(existingCharacterNames.contains(tempName)))
						sendDataBack();
					else
					{
						DialogInterface.OnClickListener dialogClickListener = 
		        			new DialogInterface.OnClickListener() {
		        		    @Override
		        		    public void onClick(DialogInterface dialog, int which) {
		        		        switch (which){
		        		        case DialogInterface.BUTTON_POSITIVE:
		        		            //if they want to, overwrite the old character
		        		        	sendDataBack();
		        		            break;

		        		        case DialogInterface.BUTTON_NEGATIVE:
		        		        	//do nothing...
		        		            break;
		        		            		        		        
		        		        }
		        		    }
		        		};

		        		AlertDialog.Builder builder = new AlertDialog.Builder(
		        										ConfigureCharacterActivity.this);
		        		builder.setMessage("A character already exists with the name " + 
		        				tempName + ". If you press \'Continue\', you will lose the " +
		        				"old character but keep this one. If you press \'Cancel\', " +
		        				"you can edit this character\'s name; this will allow you " +
		        				"to keep both the old character and this one. Character names" +
		        				" must be unique.").
		        			setPositiveButton("Continue", dialogClickListener).
		        		    setNegativeButton("Cancel", dialogClickListener).show();
					}//end of if for name already used
				}
			}
		});
    }
    
    
    private void sendDataBack()
    {
    	int iMaxHP = 0, iSurgeValue = 0, iMaxSurges = 0, iMaxPowerPoints = 0;
		String tempMHP = maxHPTextArea.getText().toString();				
		String tempSV = surgeValueTextArea.getText().toString();
		String tempMS = maxSurgesTextArea.getText().toString();
		String tempPP = maxPowerPtsTextArea.getText().toString();
		String tempName = nameTextArea.getText().toString().trim();
		String tempAPR = actionPtReminderTextArea.getText().toString().trim();
		
		try {
			iMaxHP = Integer.parseInt(tempMHP);
			
			if(tempSV.length() > 0)
				iSurgeValue = Integer.parseInt(tempSV);
			else
				iSurgeValue = iMaxHP/4;
			iMaxSurges = Integer.parseInt(tempMS);
			if(tempPP.length() > 0)
				iMaxPowerPoints = Integer.parseInt(tempPP);
		}
		catch(NumberFormatException nfe)
		{
			Toast.makeText(ConfigureCharacterActivity.this, 
					"Use numbers only for numeric types; "+
					"max hit points and max surges are required fields.",
					Toast.LENGTH_LONG).show();
			return;
		}
		if(iMaxHP < 0 || iMaxSurges < 0 || iMaxPowerPoints < 0)
		{
			Toast.makeText(ConfigureCharacterActivity.this, 
					"The fields for max hit points and max number of surges should be " 
					+ "positive values.", 
					Toast.LENGTH_SHORT).show();
			iMaxHP = Math.abs(iMaxHP);
			iMaxSurges = Math.abs(iMaxSurges);
			iMaxPowerPoints = Math.abs(iMaxPowerPoints);					
		}
		Intent data = new Intent();
		data.putExtra("maxHP", iMaxHP);
		data.putExtra("surgeValue", iSurgeValue);
		data.putExtra("maxSurges", iMaxSurges);
		data.putExtra("maxPowerPoints", iMaxPowerPoints);
		data.putExtra("name", tempName);
		data.putExtra("actionPointReminder", tempAPR);				
		setResult(Activity.RESULT_OK, data);
		finish();
    }
    
    
    //saved in the prefs file since these are just temporary values used only when this
    //activity is up...then they are overwritten the next time this activity comes up...not 
    //character based data, not permanent data
	 private void saveDataToPrefs()
	 {
    	SharedPreferences settings = getSharedPreferences(
    				FileIOHelper.DATA_FILE, 0);    	
    	SharedPreferences.Editor editor = settings.edit();    	    
    	
    	String maxHP = maxHPTextArea.getText().toString();
    	String surgeValue = surgeValueTextArea.getText().toString();
    	String maxSurges = maxSurgesTextArea.getText().toString();
    	String name = nameTextArea.getText().toString();
    	String maxPP = maxPowerPtsTextArea.getText().toString();
    	String apRemind = actionPtReminderTextArea.getText().toString();
    	
    	editor.putString("maxHP", maxHP);
    	editor.putString("surgeValue", surgeValue);
    	editor.putString("maxSurges", maxSurges);
    	editor.putString("name", name);
        editor.putString("maxPP", maxPP);
        editor.putString("apRemind", apRemind);
	        
	     // Commit the edits!
        editor.commit();
	 }
	 
	 
	//saved in the base file since these are just temporary values used only when this
    //activity is up...then they are overwritten the next time this activity comes up...not 
    //character based data, not permanent data
	 private void loadDataFromPrefs()
	 {
    	SharedPreferences settings = getSharedPreferences(
    				FileIOHelper.DATA_FILE, 0);
    	
    	maxHPTextArea.setText(settings.getString("maxHP", ""));
    	surgeValueTextArea.setText(settings.getString("surgeValue", ""));
    	maxSurgesTextArea.setText(settings.getString("maxSurges", ""));
    	nameTextArea.setText(settings.getString("name", ""));
    	maxPowerPtsTextArea.setText(settings.getString("maxPP", ""));
    	actionPtReminderTextArea.setText(settings.getString("apRemind", ""));
        
	 }
    
	 
	 public void onPause()
	 {    	
		 super.onPause();
		 saveDataToPrefs();
	 }
	
	 public void onStop()
	 {
		 super.onStop();
		 saveDataToPrefs();
	 }
	 
	 public void onResume()
	 {
		 super.onResume();
		 loadDataFromPrefs();
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
		builder.setMessage("Do you want to exit and cancel the edits you have made or do you want to save the edits?").
			setPositiveButton("Save Edits.", dialogClickListener).
		    setNegativeButton("Cancel Edits", dialogClickListener).show();    			
 
      
    }

    private void callSuperOnBackPressed()
    {
    	super.onBackPressed();
    }
	 
}
