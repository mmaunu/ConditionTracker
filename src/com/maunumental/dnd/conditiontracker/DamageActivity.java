package com.maunumental.dnd.conditiontracker;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class DamageActivity extends Activity {

	private Button doneButton;
	private EditText textarea;
	private Spinner loseSurgeSpinner;
	private int loseSurgeSpinnerCounter = 0;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.damage_activity);
        
        textarea = (EditText)findViewById(R.id.damage_activity_textarea);
        doneButton = (Button)findViewById(R.id.damage_activity_done_button);
        
        String[] numbers = {"1", "2", "3", "4", "5"};
        
        loseSurgeSpinner = (Spinner) findViewById(R.id.damage_lose_surges_number_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, numbers);
        loseSurgeSpinner.setAdapter(adapter);
        
        setClickers();
    }
    
    private void setClickers()
    {
    	
    	doneButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				sendDataBack();
			}
		});
    	
    	
    	
    	loseSurgeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, 
		    		int position, long id) 
		    {
		    	if(loseSurgeSpinnerCounter < 1)
		    		loseSurgeSpinnerCounter++;
		    	else
		    	{
			    	CheckBox bonusBox = (CheckBox)findViewById(
			    					R.id.damage_activity_lose_surge_checkbox);
			    	bonusBox.setChecked(true);
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // do nothing!!
		    }

		});
    	
    }
    
    
    private void sendDataBack()
	{
    	String temp = textarea.getText().toString();
		int hp = 0;
		try {
			if( temp.length() > 0 )
				hp = Integer.parseInt(temp);
		}
		catch(NumberFormatException nfe)
		{
			Toast.makeText(DamageActivity.this, "Only numbers are allowed", 
					Toast.LENGTH_SHORT).show();
		}
		if(hp < 0)
		{
			Toast.makeText(DamageActivity.this, "Enter the amount as a positive number", 
					Toast.LENGTH_SHORT).show();
			hp = -hp;
		}
		Intent data = new Intent();
		data.putExtra("amountEntered", hp);
		CheckBox surgeXBox = (CheckBox)findViewById(R.id.damage_activity_lose_surge_checkbox);
		boolean loseSurge = surgeXBox.isChecked();
		if(loseSurge)
		{
			int pos = ((Spinner)findViewById(R.id.damage_lose_surges_number_spinner)).
					getSelectedItemPosition();
			int number = pos + 1;
			data.putExtra("loseSurge", number);
		}
		else
		{
			data.putExtra("loseSurge", 0);
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
