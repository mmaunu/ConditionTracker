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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class AddGoodConditionActivity extends Activity {
	
	private String[] conditionEndsList = {Condition.END_OF_YOUR_NEXT_TURN,
						Condition.BEGINNING_OF_YOUR_NEXT_TURN, Condition.END_OF_ALLYS_NEXT_TURN,
						Condition.BEGINNING_OF_ALLYS_NEXT_TURN, Condition.NEXT_ROLL, 
						Condition.NEXT_ATTACK, Condition.END_OF_ENCOUNTER
				};
	
	private String[] bonusValues, bonusToWhat, resistTypes;
	
	private Spinner spinnerBonusValues, spinnerBonusToWhat, spinnerResistTypes;
	private int spinnerBVSelectedCounter = 0;
	private int spinnerBTWSelectedCounter = 0;
	private int spinnerRTSelectedCounter = 0;
	
	private Button finished;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_good_condition_activity);
		
		//populate spinner list
		Spinner s1 = (Spinner) findViewById(R.id.add_good_condition_how_condition_ends);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, conditionEndsList);
        s1.setAdapter(adapter);
    
        
        spinnerBonusValues = (Spinner) findViewById(R.id.add_good_condition_spinner_bonus_value);
        bonusValues = getResources().getStringArray(R.array.goodConditionBonusValue);
        ArrayAdapter<String> adaptOrDie = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, bonusValues);
        spinnerBonusValues.setAdapter(adaptOrDie);

        
        spinnerBonusToWhat = (Spinner) findViewById(R.id.add_good_condition_spinner_bonus_to_what);
        bonusToWhat = getResources().getStringArray(R.array.goodConditionBonusToWhat);
        ArrayAdapter<String> adaptable = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, bonusToWhat);
        spinnerBonusToWhat.setAdapter(adaptable);
        
        spinnerResistTypes = (Spinner) findViewById(R.id.add_good_condition_spinner_resist_damage_types);
        resistTypes = getResources().getStringArray(R.array.goodConditionResistDamageTypes);
        ArrayAdapter<String> adapted = new ArrayAdapter<String>(
                this, R.layout.spinner_row_layout, R.id.spinnerEntryTextView, resistTypes);
        spinnerResistTypes.setAdapter(adapted);
        
        
        finished = (Button)findViewById(R.id.add_good_condition_activity_button);

        
        
        
        setClickers();
        
        finished.requestFocus();
	}
	
	
	private void setClickers()
	{
		
		finished.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				sendDataBack();
			}
		});
		
		
		spinnerBonusToWhat.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, 
		    		int position, long id) 
		    {
		    	if(spinnerBTWSelectedCounter < 1)
		    		spinnerBTWSelectedCounter++;
		    	else
		    	{
			    	CheckBox bonusBox = (CheckBox)findViewById(
			    					R.id.add_good_condition_bonus_checkbox);
			    	bonusBox.setChecked(true);
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // do nothing!!
		    }

		});
		
		spinnerBonusValues.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, 
		    		int position, long id) 
		    {
		    	if(spinnerBVSelectedCounter < 1)
		    		spinnerBVSelectedCounter++;
		    	else
		    	{
			    	CheckBox bonusBox = (CheckBox)findViewById(R.id.add_good_condition_bonus_checkbox);
			    	bonusBox.setChecked(true);
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // do nothing!!
		    }

		});
		
		spinnerResistTypes.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, 
		    		int position, long id) 
		    {
		    	if(spinnerRTSelectedCounter < 1)
		    		spinnerRTSelectedCounter++;
		    	else
		    	{
			    	CheckBox resistBox = (CheckBox)findViewById(R.id.add_good_condition_resist_checkbox);
			    	resistBox.setChecked(true);
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
		
		
		if( ((CheckBox)findViewById(R.id.add_good_condition_resist_checkbox)).isChecked()||
			((EditText)findViewById(R.id.add_good_condition_resist_amount_edit)).
					getText().toString().length() > 0)
		{
			String temp = ((EditText)findViewById(
						R.id.add_good_condition_resist_amount_edit)).getText().toString();
			temp = temp.trim();
			int pos = ((Spinner)findViewById(
						R.id.add_good_condition_spinner_resist_damage_types)).
						getSelectedItemPosition();
			temp += " " + resistTypes[pos];
			keywords.add(getString(R.string.resist)+ " " + temp);
		}
		if( ((CheckBox)findViewById(R.id.add_good_condition_custom_checkbox)).isChecked()||
				((EditText)findViewById(R.id.add_good_condition_custom_edit)).
						getText().toString().length() > 0)
		{
			String temp = ((EditText)findViewById(
					R.id.add_good_condition_custom_edit)).getText().toString();
			temp = temp.trim();
			keywords.add(temp);
		}
		
		if( ((CheckBox)findViewById(R.id.add_good_condition_bonus_checkbox)).isChecked() )
		{
			int posBV = ((Spinner)findViewById(
						R.id.add_good_condition_spinner_bonus_value)).
						getSelectedItemPosition();
			int posBTW = ((Spinner)findViewById(
						R.id.add_good_condition_spinner_bonus_to_what)).
						getSelectedItemPosition();
			String temp = bonusValues[posBV] + " to " + bonusToWhat[posBTW];
			
			if(((CheckBox)findViewById(R.id.add_good_condition_bonus_mod_by_checkbox)).isChecked()
				|| 	((EditText)findViewById(R.id.add_good_condition_bonus_mod_by_edit)).
				getText().toString().length() > 0)
			{
				temp += " " +((EditText)findViewById(
						R.id.add_good_condition_bonus_mod_by_edit)).getText().toString();
			}
			
			if(((CheckBox)findViewById(R.id.add_good_condition_bonus_within_checkbox)).isChecked()
					|| 	((EditText)findViewById(R.id.add_good_condition_bonus_within_edit)).
					getText().toString().length() > 0)
				{
					temp += " when within " +((EditText)findViewById(
							R.id.add_good_condition_bonus_within_edit)).getText().toString();
				}
			
			keywords.add(temp);
		}
		
		
		int pos = ((Spinner)findViewById(R.id.add_good_condition_how_condition_ends)).
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
