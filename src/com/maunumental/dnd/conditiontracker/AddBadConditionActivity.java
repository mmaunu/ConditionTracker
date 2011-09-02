package com.maunumental.dnd.conditiontracker;

import java.util.ArrayList;

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
import android.widget.AdapterView.OnItemSelectedListener;

public class AddBadConditionActivity extends Activity {
	
	private String[] conditionEndsList = {Condition.SAVE_ENDS,Condition.END_OF_YOUR_NEXT_TURN,
						Condition.BEGINNING_OF_YOUR_NEXT_TURN,
						Condition.END_OF_ATTACKERS_NEXT_TURN,
						Condition.BEGINNING_OF_ATTACKERS_NEXT_TURN,Condition.END_OF_ENCOUNTER,
						Condition.SUCCEED_ON_CHECK
				};
	
	private String[] penaltyValues, penaltyToWhat;
	private Spinner spinnerPenaltyValue, spinnerPenaltyToWhat;
	private int spinnerPVSelectedCounter = 0;
	private int spinnerPTWSelectedCounter = 0;
	
	private Button finished;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_bad_condition_activity);
		
		//populate spinner list
		Spinner s1 = (Spinner) findViewById(R.id.add_bad_condition_how_condition_ends);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, conditionEndsList);
        s1.setAdapter(adapter);
    
        
        
        
        
        spinnerPenaltyValue = (Spinner) findViewById(
        			R.id.add_bad_condition_spinner_penalty_value);
        penaltyValues = getResources().getStringArray(R.array.badConditionPenaltyValue);
        ArrayAdapter<String> adaptOrDie = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, penaltyValues);
        spinnerPenaltyValue.setAdapter(adaptOrDie);

        
        spinnerPenaltyToWhat = (Spinner) findViewById(R.id.add_bad_condition_spinner_penalty_to_what);
        penaltyToWhat = getResources().getStringArray(R.array.badConditionPenaltyToWhat);
        ArrayAdapter<String> adaptable = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, penaltyToWhat);
        spinnerPenaltyToWhat.setAdapter(adaptable);
        
        
        

        finished = (Button)findViewById(R.id.add_bad_condition_activity_button);
        
        
        setClickers();

        
        CheckBox penaltyBox = (CheckBox)findViewById(R.id.add_bad_penalty_checkbox);
    	penaltyBox.setChecked(false);
    	
    	finished.requestFocus();
	}
	
	
	private void setClickers()
	{
		
		finished.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				sendDataBack();
			}
		});
		
		
		spinnerPenaltyValue.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, 
		    		int position, long id) 
		    {
		    	if(spinnerPVSelectedCounter < 1)
		    		spinnerPVSelectedCounter++;
		    	else
		    	{
			    	CheckBox penaltyBox = (CheckBox)findViewById(R.id.add_bad_penalty_checkbox);
			    	penaltyBox.setChecked(true);
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	//do nothing...
		    }

		});
		
		spinnerPenaltyToWhat.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, 
		    		int position, long id) 
		    {
		    	if(spinnerPTWSelectedCounter < 1)
		    		spinnerPTWSelectedCounter++;
		    	else
		    	{
			    	CheckBox penaltyBox = (CheckBox)findViewById(R.id.add_bad_penalty_checkbox);
			    	penaltyBox.setChecked(true);
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
		ArrayList<String> keywords = new ArrayList<String>();
		
		if( ((CheckBox)findViewById(R.id.add_bad_blinded)).isChecked() )
			keywords.add(getString(R.string.blinded));
		if( ((CheckBox)findViewById(R.id.add_bad_dazed)).isChecked() )
			keywords.add(getString(R.string.dazed));
		if( ((CheckBox)findViewById(R.id.add_bad_dominated)).isChecked() )
			keywords.add(getString(R.string.dominated));
		if( ((CheckBox)findViewById(R.id.add_bad_helpless)).isChecked() )
			keywords.add(getString(R.string.helpless));
		if( ((CheckBox)findViewById(R.id.add_bad_immobilized)).isChecked() )
			keywords.add(getString(R.string.immobilized));
		if( ((CheckBox)findViewById(R.id.add_bad_marked)).isChecked() )
			keywords.add(getString(R.string.marked));
		if( ((CheckBox)findViewById(R.id.add_bad_petrified)).isChecked() )
			keywords.add(getString(R.string.petrified));
		if( ((CheckBox)findViewById(R.id.add_bad_restrained)).isChecked() )
			keywords.add(getString(R.string.restrained));
		if( ((CheckBox)findViewById(R.id.add_bad_slowed)).isChecked() )
			keywords.add(getString(R.string.slowed));
		if( ((CheckBox)findViewById(R.id.add_bad_stunned)).isChecked() )
			keywords.add(getString(R.string.stunned));
		if( ((CheckBox)findViewById(R.id.add_bad_weakened)).isChecked() )
			keywords.add(getString(R.string.weakened));
		if( ((CheckBox)findViewById(R.id.add_bad_grabbed)).isChecked() )
			keywords.add(getString(R.string.grabbed));
		if( ((CheckBox)findViewById(R.id.add_bad_prone)).isChecked() )
			keywords.add(getString(R.string.prone));
		if( ((CheckBox)findViewById(R.id.add_bad_unconscious)).isChecked() )
			keywords.add(getString(R.string.unconscious));
		if( ((CheckBox)findViewById(R.id.add_bad_ongoing_check)).isChecked() ||
				((EditText)findViewById(R.id.add_bad_ongoing_edit)).getText().
				toString().length() > 0	)
		{
			String temp = ((EditText)findViewById(R.id.add_bad_ongoing_edit)).getText().
							toString();
			temp = temp.trim();
			keywords.add(getString(R.string.ongoing_damage)+ " " + temp);
		}
		if( ((CheckBox)findViewById(R.id.add_bad_penalty_checkbox)).isChecked() )
		{
			int posPV = ((Spinner)findViewById(
					R.id.add_bad_condition_spinner_penalty_value)).
					getSelectedItemPosition();
			int posPTW = ((Spinner)findViewById(
					R.id.add_bad_condition_spinner_penalty_to_what)).
					getSelectedItemPosition();
			String temp = penaltyValues[posPV] + " to " + penaltyToWhat[posPTW];
			keywords.add(temp);
		}
		
		
		int pos = ((Spinner)findViewById(R.id.add_bad_condition_how_condition_ends)).
					getSelectedItemPosition();
		String endsOn = "saviore faire ends";
		if(pos >= 0 && pos < conditionEndsList.length)
			endsOn = conditionEndsList[pos];
		
		Intent data = new Intent();
		data.putStringArrayListExtra("keywords", keywords);
		data.putExtra("endsOn", endsOn);
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
		builder.setMessage("Do you want to exit and cancel the edits you have made or do you want to save the edits?").
			setPositiveButton("Save Edits.", dialogClickListener).
		    setNegativeButton("Cancel Edits", dialogClickListener).show();    			
 
      
    }

    private void callSuperOnBackPressed()
    {
    	super.onBackPressed();
    }
}
