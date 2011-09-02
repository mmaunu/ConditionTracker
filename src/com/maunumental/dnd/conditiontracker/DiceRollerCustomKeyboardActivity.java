package com.maunumental.dnd.conditiontracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;



public class DiceRollerCustomKeyboardActivity extends Activity 
{
	
	private DiceRollerSingleKeyView key1, key2, key3, key4, key5, key6, key7, key8, key9,
			key0, keyD, keyPlus, keyMinus, keyCrit, keyReroll, keyDelete, keyClear;
	
	private Button doneButton;
	
	private EditText expEditView,nameEditView;
	
	private String expression, name;
	private int whichArea;
	
	private int numResumes = 0;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dice_roller_custom_keyboard_activity);
		
		Intent intent = getIntent();
		String exp = intent.getStringExtra("expression");
		if(exp != null)
			expression = exp;
		else
			expression = "";
		
		name = intent.getStringExtra("name");
		if(name == null)
			name = "";
		whichArea = intent.getIntExtra("whichArea", 1);
		
		expEditView = ((EditText)findViewById(R.id.diceRollerKeyboardExpressionDisplay));
		expEditView.setFocusable(true);
		expEditView.setText(expression);
		nameEditView = ((EditText)findViewById(R.id.diceRollerKeyboardNameEdit));
		nameEditView.setText(name);
		

		doneButton = (Button)findViewById(R.id.diceRollerDoneButton);
		
		
		//load the keys and set them up
		key1 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey1);
		key1.setDisplayValue("1");
		key2 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey2);
		key2.setDisplayValue("2");
		key3 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey3);
		key3.setDisplayValue("3");
		key4 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey4);
		key4.setDisplayValue("4");
		key5 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey5);
		key5.setDisplayValue("5");
		key6 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey6);
		key6.setDisplayValue("6");
		key7 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey7);
		key7.setDisplayValue("7");
		key8 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey8);
		key8.setDisplayValue("8");
		key9 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey9);
		key9.setDisplayValue("9");
		key0 = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKey0);
		key0.setDisplayValue("0");
		keyD = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKeyD);
		keyD.setDisplayValue("d");
		keyPlus = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKeyPlus);
		keyPlus.setDisplayValue("+");
		keyMinus = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKeyMinus);
		keyMinus.setDisplayValue("-");
		keyReroll = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKeyReroll);
		keyReroll.setDisplayValue("Reroll(r)");
		keyCrit = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKeyCrit);
		keyCrit.setDisplayValue("Crit(c)");
		keyDelete = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKeyDelete);
		keyDelete.setDisplayValue("Delete");
		keyClear = (DiceRollerSingleKeyView)findViewById(R.id.diceRollerKeyClear);
		keyClear.setDisplayValue("Clear");
		
		setupClickers();
		
		saveDataToPrefs();
	}
	
	public void onResume()
	{
		super.onResume();
		//ignore the initial onResume, but otherwise load the data
	//	if(numResumes > 0)
		{
			loadDataFromPrefs();	
		}
		numResumes++;
	}
	
	private void hideSoftKeyboardShowCustom()
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nameEditView.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(expEditView.getWindowToken(), 0);
		((TableLayout)findViewById(R.id.diceRollerCustomKeyboardTableLayout)).
				setVisibility(View.VISIBLE);
		((ScrollView)findViewById(R.id.diceRollerCustomKeyboardScrollView)).
				fullScroll(ScrollView.FOCUS_UP);
	}
	
	private void hideCustomKeyboard()
	{
		((TableLayout)findViewById(R.id.diceRollerCustomKeyboardTableLayout)).
				setVisibility(View.INVISIBLE);
		((ScrollView)findViewById(R.id.diceRollerCustomKeyboardScrollView)).
				fullScroll(ScrollView.FOCUS_UP);
	}
	
	private void setupClickers()
	{
		expEditView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		hideSoftKeyboardShowCustom();
        	}        		
        });
		
		expEditView.setOnFocusChangeListener( new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && v.equals(expEditView))
				{
					hideSoftKeyboardShowCustom();
				}
			}
		});
	
		nameEditView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		hideCustomKeyboard();
        	}        		
        });
		
		nameEditView.setOnFocusChangeListener( new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && v.equals(nameEditView))
				{
					hideCustomKeyboard();
				}
			}
		});
		
		
		//all of the custom views will have onClickListeners and modify the field expression
		key1.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "1";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key2.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "2";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key3.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "3";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key4.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "4";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key5.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "5";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key6.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "6";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key7.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "7";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key8.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "8";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key9.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "9";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		key0.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "0";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		keyD.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "d";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		keyPlus.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "+";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		keyMinus.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "-";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		keyReroll.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "r";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		keyCrit.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression += "c";
        		expEditView.setText(expression);
        		expEditView.setSelection(expression.length());
        	}        		
        });
		
		keyDelete.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		if(expression.length() > 0)
        		{
	        		expression = expression.substring(0,expression.length() - 1);
	        		expEditView.setText(expression);
	        		expEditView.setSelection(expression.length());
        		}
        	}        		
        });
		
		keyClear.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expression = "";
        		expEditView.setText(expression);
        	}        		
        });
		
		doneButton.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		//end this activity, send intent back with expression in it
        		sendDataBack();
        	}        		
        });
		
		
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
	
    //saved in the base file since these are just temporary values used only when this
    //activity is up...then they are overwritten the next time this activity comes up...not 
    //character based data, not permanent data
	 private void saveDataToPrefs()
	 {
    	SharedPreferences settings = getSharedPreferences(
    				FileIOHelper.DATA_FILE, 0);
    	
    	SharedPreferences.Editor editor = settings.edit();
        
    	name = nameEditView.getText().toString();
    	editor.putString("name", name);
        editor.putString("expression", expression);
        editor.putInt("whichArea", whichArea);
	        
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
    	
        expression = settings.getString("expression", "");
        name = settings.getString("name", "");
        whichArea = settings.getInt("whichArea", 1);
        nameEditView.setText(name);
        expEditView.setText(expression);
        
	 }
	 
	 
	 private void sendDataBack()
	 {
		 Intent data = new Intent();				
		 data.putExtra("expression", expression);
		 data.putExtra("whichArea", whichArea);
		 name = nameEditView.getText().toString();
		 if(name == null)
			 name = "";
		 data.putExtra("name", name);
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
