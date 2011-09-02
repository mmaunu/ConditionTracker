package com.maunumental.dnd.conditiontracker;

import java.util.ArrayList;
import java.util.Random;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class ConditionTracker extends TabActivity { 
 
    private int HEAL_ACTIVITY = 23, DAMAGE_ACTIVITY = 7, CONFIGURE_ACTIVITY = 8, 
    			ADD_BAD_CONDITION_ACTIVITY = 19, ADD_GOOD_CONDITION_ACTIVITY = 723,
    			DICE_ROLLER_KEYBOARD_ACTIVITY = 819, 
    			DICE_ROLLER_KEYBOARD_BONUS_DAMAGE_ACTIVITY = 72394,
    			TEMP_HIT_POINT_ACTIVITY = 2323,
    			NOTES_EDITOR_ACTIVITY = 723327,
    			LOAD_CHARACTER_ACTIVITY = 237, NEW_CHARACTER_ACTIVITY = 81918,
    			POWER_POINT_ACTIVITY = 2332;
    
    private static final String HISTORY_SEPARATOR = "===================";
    
  //primary data...for the character...kind of important
    private CharacterDnD4Ed duderino;
    
    //muy importante
    private Random random;
    
    
    private MediaPlayer diceSound;
    private boolean diceSoundEnabled = true;
    
    
    //views and controls on the main tab
    private HitPointDisplayView hitPointDisplayView;
    private TempHitPointControlView thpControlView;
    private SurgeNumberView surgeNumberView;
    private HitPointsPlusControlView hpPlusControlView;
    private HitPointsMinusControlView hpMinusControlView;
    private ActionPointNumberView apNumberView;
    private PowerPointNumberView powerPtNumberView;
    private AddBadConditionView addBadConditionButton;
    private AddGoodConditionView addGoodConditionButton;
    
    
    //fields for the conditions
    private ListView conditionListView;
    private ArrayList<String> conditionsStringArrayList = new ArrayList<String>();
    private ConditionsAdapter conditionsAdapter;
    private Condition deletableCondition;
   
    
    //notes tab
    private Button notesStartEditor;
    private ListView notesListView;
    private ArrayList<String> notesStringArrayList = new ArrayList<String>();
    private NotesAdapter notesAdapter;
    private Note deletableNote;
    
    
    //big chunk of fields for the diceroller
    private Button diceRollerRollButton1, diceRollerRollButton2, diceRollerRollButton3,
    				diceRollerRollButton4, diceRollerRollButton5, diceRollerRollButton6,
    				diceRollerRollButton7, diceRollerRollButton8, diceRollerRollButton9,
    				diceRollerRollButton10;
    private Button diceRollerEditButton1, diceRollerEditButton2, diceRollerEditButton3,
    				diceRollerEditButton4, diceRollerEditButton5, diceRollerEditButton6,
    				diceRollerEditButton7, diceRollerEditButton8, diceRollerEditButton9,
    				diceRollerEditButton10;
    private Button diceRollerMaxButton1, diceRollerMaxButton2, diceRollerMaxButton3,
    				diceRollerMaxButton4, diceRollerMaxButton5, diceRollerMaxButton6,
    				diceRollerMaxButton7, diceRollerMaxButton8, diceRollerMaxButton9,
    				diceRollerMaxButton10;
    private TextView diceRollerReportArea;
    private ImageView d20DiceRollerImageView;
    private String expToMax;	//this is for maximized rolls...passing data from inner classes
    private int sumOfMax;		//to preserve data between activities
    private ArrayList<String> diceHistory;
    private int diceRollCounter;
    private ImageView diceRollerHistoryImageView;
    
    
    
    
    //our gracious host
    private TabHost tabHost;

    
    private ArrayList<String> hitPointHistory;
    
 
    
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainwithtabs);
        
        
        Resources res = getResources();
        tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tabMain").setIndicator("Character",
                res.getDrawable(R.drawable.character_tab_icon)).setContent(R.id.tabMainLayout));
        
        tabHost.addTab(tabHost.newTabSpec("tabNotes").setIndicator("Notes",
                res.getDrawable(R.drawable.notes_icon)).
                setContent(R.id.tabNotesLayout));
        
        tabHost.addTab(tabHost.newTabSpec("tabDice").setIndicator("Dice Roller",
                res.getDrawable(R.drawable.d20)).setContent(R.id.tabDiceRollerLayout));
        
        
        
        //tab drawing...we'll see...drawing a background image on the tabs...still no good
        //way of drawing the little left/right dividers without moving up to api 8 (iirc)
        TabWidget tw = getTabWidget();

        for (int i = 0; i < tw.getChildCount(); i++) 
        {
	        View v = tw.getChildAt(i);
	        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_test));
        } 
        
        
        FileIOHelper.context = getApplicationContext();

        
        diceSound = MediaPlayer.create(this, R.raw.dice_attempt2);
        diceSound.setVolume(0.025f, 0.025f);
        
        
        boolean firstUse = FileIOHelper.isFirstUseOfApp();
        if(firstUse)
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        	String firstUseDirections = getResources().getString(R.string.first_app_use_help);
    		builder.setMessage(firstUseDirections).
    			setPositiveButton("Done", null).show();
        }
        
        //obvious
        random = new Random();
        
        hitPointHistory = new ArrayList<String>();
        hitPointHistory.add("The history is empty.");
        
        hitPointDisplayView = (HitPointDisplayView)findViewById(R.id.hitPointDisplayView);
        thpControlView = (TempHitPointControlView)findViewById(R.id.tempHPControlView);
        surgeNumberView = (SurgeNumberView)findViewById(R.id.surgeNumberView);
        hpMinusControlView = (HitPointsMinusControlView)findViewById(
        		R.id.hitPointsMinusControlView);
        hpPlusControlView = (HitPointsPlusControlView)findViewById(
        		R.id.hitPointsPlusControlView);
        apNumberView = (ActionPointNumberView)findViewById(R.id.actionPointNumberView);
        powerPtNumberView = (PowerPointNumberView)findViewById(R.id.powerPointNumberView);
        
        
        
		
		addBadConditionButton = (AddBadConditionView)findViewById(
				R.id.main_screen_add_bad_condition_button);
		addGoodConditionButton = (AddGoodConditionView)findViewById(
				R.id.main_screen_add_good_condition_button);
		
		
		//conditions and list stuff for conditions
		conditionListView = (ListView)findViewById(R.id.conditionListView);
		
		notesStartEditor = (Button)findViewById(R.id.notes_start_note_editor_button);
	    notesListView = (ListView)findViewById(R.id.notesListView);
	
		
        
        //dice roller config
        diceRollerEditButton1 = (Button)findViewById(R.id.diceRollerEditButton1);
        diceRollerEditButton2 = (Button)findViewById(R.id.diceRollerEditButton2);
        diceRollerEditButton3 = (Button)findViewById(R.id.diceRollerEditButton3);
        diceRollerEditButton4 = (Button)findViewById(R.id.diceRollerEditButton4);
        diceRollerEditButton5 = (Button)findViewById(R.id.diceRollerEditButton5);
        diceRollerEditButton6 = (Button)findViewById(R.id.diceRollerEditButton6);
        diceRollerEditButton7 = (Button)findViewById(R.id.diceRollerEditButton7);
        diceRollerEditButton8 = (Button)findViewById(R.id.diceRollerEditButton8);
        diceRollerEditButton9 = (Button)findViewById(R.id.diceRollerEditButton9);
        diceRollerEditButton10 = (Button)findViewById(R.id.diceRollerEditButton10);
        
        diceRollerMaxButton1 = (Button)findViewById(R.id.diceRollerMaxButton1);
        diceRollerMaxButton2 = (Button)findViewById(R.id.diceRollerMaxButton2);
        diceRollerMaxButton3 = (Button)findViewById(R.id.diceRollerMaxButton3);
        diceRollerMaxButton4 = (Button)findViewById(R.id.diceRollerMaxButton4);
        diceRollerMaxButton5 = (Button)findViewById(R.id.diceRollerMaxButton5);
        diceRollerMaxButton6 = (Button)findViewById(R.id.diceRollerMaxButton6);
        diceRollerMaxButton7 = (Button)findViewById(R.id.diceRollerMaxButton7);
        diceRollerMaxButton8 = (Button)findViewById(R.id.diceRollerMaxButton8);
        diceRollerMaxButton9 = (Button)findViewById(R.id.diceRollerMaxButton9);
        diceRollerMaxButton10 = (Button)findViewById(R.id.diceRollerMaxButton10);
        
        diceRollerRollButton1 = (Button)findViewById(R.id.diceRollerRollButton1);
        diceRollerRollButton2 = (Button)findViewById(R.id.diceRollerRollButton2);
        diceRollerRollButton3 = (Button)findViewById(R.id.diceRollerRollButton3);
        diceRollerRollButton4 = (Button)findViewById(R.id.diceRollerRollButton4);
        diceRollerRollButton5 = (Button)findViewById(R.id.diceRollerRollButton5);
        diceRollerRollButton6 = (Button)findViewById(R.id.diceRollerRollButton6);
        diceRollerRollButton7 = (Button)findViewById(R.id.diceRollerRollButton7);
        diceRollerRollButton8 = (Button)findViewById(R.id.diceRollerRollButton8);
        diceRollerRollButton9 = (Button)findViewById(R.id.diceRollerRollButton9);
        diceRollerRollButton10 = (Button)findViewById(R.id.diceRollerRollButton10);
        
        diceRollerReportArea = (TextView)findViewById(R.id.diceRollerReportArea);
        
        d20DiceRollerImageView = (ImageView)findViewById(R.id.dice_roller_d20);
        diceRollerHistoryImageView = (ImageView)findViewById(R.id.dice_roller_history_image);
        
        diceHistory = new ArrayList<String>();
        diceRollCounter = 0;
       
        
        
        //load the dude and set the views so they have a handle on the data
        loadDataFromPrefs();
        
        updateCharacterTabWithName(duderino.getName());
        
        togglePowerPointView();
        
        setupClickers();
        
    }
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	if(requestCode == HEAL_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		
    		//for the hitpoint history...not surges used, just how much healing was applied
    		int numberSurgesHealed = 0;
    		int preHealHP = duderino.getCurrentHitPoints();
    		
    		
    		//fill in the rest...perhaps...
    		//a -3 for surgeYes indicates some weird error...it shouldn't default to -3
    		int surgeYes = intent.getIntExtra("surge",-3);
    		int amount = intent.getIntExtra("amountEntered", 0);
    		if(amount > 0)
    			duderino.heal(amount);
    		else if(amount < 0)
    			duderino.takeDamage(Math.abs(amount));	//can't take negative damage
    		
    		
    		
    		//1 indicates use 1 surge while -1 for surgeYes indicates an "As If" heal
    		if(surgeYes == 1)
    		{
    			if(duderino.getCurrentSurges() > 0)
    			{
    				duderino.loseSurge();
    				duderino.heal(duderino.getSurgeValue());
    				numberSurgesHealed = 1;
    			}
    			else
    			{    				
    				Toast.makeText(ConditionTracker.this, 
    						"You are out of surges...check the 'As If' option if " +
    						"you want surgeless healing.", 
    						Toast.LENGTH_LONG).show();
    			}
    		} 
    		else if(surgeYes == 2)
    		{
    			if(duderino.getCurrentSurges() > 1)
    			{
    				duderino.loseSurge();
    				duderino.loseSurge();
    				duderino.heal(duderino.getSurgeValue());
    				duderino.heal(duderino.getSurgeValue());
    				numberSurgesHealed = 2;
    			}
    			else if(duderino.getCurrentSurges() == 1)
				{
					duderino.loseSurge();
    				duderino.heal(duderino.getSurgeValue());
    				numberSurgesHealed = 1;
					Toast.makeText(this, "You checked 2 surges, but you only have 1..." +
							"so you only healed one surge value", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(ConditionTracker.this, 
						"You are out of surges...check the 'As If' option if " +
						"you want surgeless healing.", 
						Toast.LENGTH_LONG).show();
				}
    			
    		}
    		else if (surgeYes == -1)
    		{
    			duderino.heal(duderino.getSurgeValue());
    			numberSurgesHealed = 1;
    		}
    		else if (surgeYes == -2)
    		{
    			duderino.heal(duderino.getSurgeValue());
    			duderino.heal(duderino.getSurgeValue());
    			numberSurgesHealed = 2;
    		}
    		hitPointDisplayView.invalidate();
    		surgeNumberView.invalidate();
    		
    		//update the hitPointHistory
    		String hpHistDesc = "HP Before Heal: " + preHealHP + "\n";
    		if(numberSurgesHealed > 0)
    		{
    			hpHistDesc += "Healed " + amount + " + " + numberSurgesHealed + " surge(s) " +
    						"which gives a total of " + 
    						(amount + numberSurgesHealed*duderino.getSurgeValue()) + 
    						" hit points healed.\n";
    			if(surgeYes < 0)
    				hpHistDesc += "The surge healing was \'As If\' healing; " +
    								"you did not lose any surges.\n";
    		}
    		else
    			hpHistDesc += "Healed for " + amount + "\n";
    		hpHistDesc += "HP After Heal: " + duderino.getCurrentHitPoints() + "\n";
    		hpHistDesc += HISTORY_SEPARATOR + "\n";
    		hitPointHistory.add(hpHistDesc);
    		
    		saveDataToPrefs();
    	}
    	else if(requestCode == TEMP_HIT_POINT_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		
    		//for hit point history
    		boolean tempsApplied = false;
    		int preHealTHP = duderino.getTempHitPoints();
    		
    		int amount = intent.getIntExtra("amountEntered", 0);
    		if(duderino.getTempHitPoints() < amount)
    		{
    			duderino.setTempHitPoints(amount); 
    			tempsApplied = true;
    		}
    		else
    			Toast.makeText(this, "Remember: temporary hit points DO NOT STACK",
    					Toast.LENGTH_LONG).show();
    		hitPointDisplayView.invalidate();
    		
    		String hpHistDesc = "Temp HP Before Heal: " + preHealTHP + "\n";
    		if(tempsApplied)
    			hpHistDesc += "Temp HP After Heal: " + duderino.getTempHitPoints() + 
    							"\n";
    		else
    			hpHistDesc += "Temp Hit Points not applied; they do not stack. You entered " +
    							amount + " but you already had " + preHealTHP + "\n";
    		hpHistDesc += HISTORY_SEPARATOR + "\n";
    		hitPointHistory.add(hpHistDesc);
    		
    		saveDataToPrefs();
    	}
    	else if (requestCode == DAMAGE_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		
    		//for hit point history
    		int preDamageHP = duderino.getCurrentHitPoints();
    		
    		
    		int amount = intent.getIntExtra("amountEntered", 0);
    		int loseSurge = intent.getIntExtra("loseSurge", 0);
    		duderino.takeDamage(amount);
    		    		
    		if (duderino.getCurrentSurges() < loseSurge)
    			Toast.makeText(ConditionTracker.this, 
    					"You don't have that many surges...now you have none", 
    					Toast.LENGTH_LONG).show();
    		for(int k = 0; k < loseSurge; k++)
    			duderino.loseSurge();
    		surgeNumberView.invalidate();
    		hitPointDisplayView.invalidate();
    		
    		String hpHistDesc = "HP Before Damage: " + preDamageHP + "\n";
    		if(loseSurge > 0)
    			hpHistDesc += "You lost " + amount + " hit point(s) and " + loseSurge +
    							" surge(s).\n";
    		else
    			hpHistDesc += "Damage Taken: " + amount + "\n";
    		hpHistDesc += "HP After Damage: " + duderino.getCurrentHitPoints() + "\n";
    		hpHistDesc += HISTORY_SEPARATOR + "\n";
    		hitPointHistory.add(hpHistDesc);
    		
    		saveDataToPrefs();
    	}
    	else if(requestCode == POWER_POINT_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		
    		int amount = intent.getIntExtra("amountEntered", 0);
    		String type = intent.getStringExtra("typeUseOrGain");
    		if(type.equalsIgnoreCase("use"))
    		{
    			if(duderino.getCurrentPowerPoints() - amount < 0)
    				Toast.makeText(this, "You did not have that many power points to begin " +
    						"with...now you have 0", Toast.LENGTH_LONG).show();
    			duderino.decreasePowerPoints(amount);
    				
    		}
    		else if(type.equalsIgnoreCase("gain"))
    		{
    			duderino.increasePowerPoints(amount);
    		}
    		
    		powerPtNumberView.invalidate();
    		
    		saveDataToPrefs();
    	}
    	else if (requestCode == NOTES_EDITOR_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		
    		int position = intent.getIntExtra("position", -1);
    		String title = intent.getStringExtra("title");
    		String body = intent.getStringExtra("body");
    		if(title == null)
    			return;
    		if(body == null)
    			body = "";
    		Note newNote = new Note(title, body);
    		if(position == -1)
    		{
    			duderino.addNote(newNote);
    		}
    		else
    		{
    			//if position is not -1, then we are editing an existing note, so we replace
    			//the old note with the new one
    			duderino.replaceNote(position, newNote);	
    		}
    		
    		notesStringArrayList = notesToStringArrayList(duderino.getNotes());
    		notesAdapter = new NotesAdapter(this, R.layout.notes_list_row_layout, notesStringArrayList);
            notesListView.setAdapter(notesAdapter);
    		notesListView.invalidate();
    		
    		saveDataToPrefs();
    	}
    	else if (requestCode == LOAD_CHARACTER_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		String name = intent.getStringExtra("name");
    		if(name == null)
    			return;
    		    		
    		
            //loadDataFromPrefs looks up the current character name and then loads
            //the character with that name...
    		FileIOHelper.saveCurrentCharacterName(name);        	
    		loadDataFromPrefs();
    		    		
    	}
    	else if (requestCode == NEW_CHARACTER_ACTIVITY)
    	{
    		//get data
    		if(intent == null)
    			return;
    		
    		//get a bunch of data from the activity and construct a new duderino
    		int maxHP = intent.getIntExtra("maxHP", 0);
    		int currHP = maxHP;		//after config, full health!
    		int surgeValue = intent.getIntExtra("surgeValue", 0);
    		int maxSurges = intent.getIntExtra("maxSurges", 0);
    		int currSurges = maxSurges;	//after config, full surges
    		int maxPwrPts = intent.getIntExtra("maxPowerPoints", 0);
    		int currPwrPts = maxPwrPts;	//after config, full power!
    		int currActPts = 1;			//after config, 1 action point
    		int tempHP = 0;				//after config, no temps
    		String characterName = intent.getStringExtra("name").trim();
    		String apReminder = intent.getStringExtra("actionPointReminder");
    		if(apReminder == null)
    			apReminder = "";
    		
    		
    		//construct a dude...just construct a new one with empty arraylists 
    		//because no conditions, no dice expressions, no notes
			duderino = new CharacterDnD4Ed(characterName, maxHP, currHP, tempHP, 
					maxSurges, currSurges, surgeValue, currActPts, maxPwrPts, 
					currPwrPts, apReminder, null, null, null, null);
    		
    		
    		//save data and set current character name to this name
			FileIOHelper.saveCurrentCharacterName(duderino.getName());
			FileIOHelper.saveCharacter(duderino);
    		
			
    		//load data, setup displays
			loadDataFromPrefs();

    	}
    	else if (requestCode == CONFIGURE_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		
    		//get a bunch of data from the activity and construct a new duderino
    		int maxHP = intent.getIntExtra("maxHP", 0);
    		int currHP = maxHP;		//after config, full health!
    		int surgeValue = intent.getIntExtra("surgeValue", 0);
    		int maxSurges = intent.getIntExtra("maxSurges", 0);
    		int currSurges = maxSurges;	//after config, full surges
    		int maxPwrPts = intent.getIntExtra("maxPowerPoints", 0);
    		int currPwrPts = maxPwrPts;	//after config, full power!
    		int currActPts = 1;			//after config, 1 action point
    		int tempHP = 0;				//after config, no temps
    		String characterName = intent.getStringExtra("name").trim();
    		String apReminder = intent.getStringExtra("actionPointReminder");
    		if(apReminder == null)
    			apReminder = "";
    		
    		String charNameBeforeConfig = FileIOHelper.getCurrentCharacterName();
    		
    		
    		//determine if this is truly just an update of the current character (as intended)
    		//if it isn't, then...I don't know...error message?
    		if(charNameBeforeConfig.equals(characterName))
    		{
    			//if truly just updating the character, then get the old notes, diceExpressions,
    			//and conditions...load them into a new character along with the data passed
    			//from the intent...note that if the user deletes the act pt reminder (etc.)
    			//then their character will now have no action point reminder.
    			ArrayList<Note> currNotes = duderino.getNotes();
    			ArrayList<Condition> currConds = duderino.getConditions();
    			ArrayList<String> diceExps = duderino.getDiceExpressions();
    			ArrayList<String> diceNames = duderino.getDiceExpressionNames();
    			duderino = new CharacterDnD4Ed(characterName, maxHP, currHP, tempHP, maxSurges,
    					currSurges, surgeValue, currActPts, maxPwrPts, currPwrPts, apReminder, 
    					currConds, diceExps, diceNames, currNotes);
    		}
    		else
    		{
    			Toast.makeText(this, "Error...after configuration, name mismatch", 
    					Toast.LENGTH_LONG).show();
    		}
    		
    		//save the dude
    		saveDataToPrefs();    		

    		//reload all of the data and setup the displays
    		loadDataFromPrefs();    		    	
    		
    	}
    	else if (requestCode == ADD_BAD_CONDITION_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		ArrayList<String> keywords = intent.getStringArrayListExtra("keywords");
    		if(keywords == null || keywords.size() == 0)
    			return;
    		String endsOn = intent.getStringExtra("endsOn");
    		Condition c = new ConditionBad(keywords, endsOn);
    		duderino.addCondition(c);
    		
    		conditionsStringArrayList = conditionsToStringArrayList(duderino.getConditions());    		
    		conditionsAdapter = new ConditionsAdapter(
    			    ConditionTracker.this, R.layout.condition_list_row_layout,
    			    conditionsStringArrayList);

    		conditionListView.setAdapter(conditionsAdapter);
    		conditionListView.invalidate();
    		
    		saveDataToPrefs();
    		
    	}
    	else if (requestCode == ADD_GOOD_CONDITION_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		ArrayList<String> keywords = intent.getStringArrayListExtra("keywords");
    		if(keywords == null || keywords.size() == 0)
    			return;
    		String endsOn = intent.getStringExtra("endsOn");
    		Condition c = new ConditionGood(keywords, endsOn);
    		duderino.addCondition(c);
    		
    		conditionsStringArrayList = conditionsToStringArrayList(duderino.getConditions());    		
    		conditionsAdapter = new ConditionsAdapter(
    			    ConditionTracker.this, R.layout.condition_list_row_layout,
    			    conditionsStringArrayList);

    		conditionListView.setAdapter(conditionsAdapter);
    		conditionListView.invalidate();
    		
    		saveDataToPrefs();
    		
    	}
    	else if (requestCode == DICE_ROLLER_KEYBOARD_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		String exp = intent.getStringExtra("expression");
    		if(exp == null)
    			exp = "";
    		int whichArea = intent.getIntExtra("whichArea", 1);
    		String name = intent.getStringExtra("name");
    		if(name == null)
    			name = "";
    		//the character's lists keep position 0 for area 1, etc.
    		duderino.addDiceExpression(whichArea - 1, exp);
    		duderino.addDiceExpressionName(whichArea - 1, name);
    		switch(whichArea){
    			case 1:
    				((TextView)findViewById(R.id.diceRollerTextExpression1)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName1)).setText(name);
    				break;
    			case 2:
    				((TextView)findViewById(R.id.diceRollerTextExpression2)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName2)).setText(name);
    				break;
    			case 3:
    				((TextView)findViewById(R.id.diceRollerTextExpression3)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName3)).setText(name);
    				break;
    			case 4:
    				((TextView)findViewById(R.id.diceRollerTextExpression4)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName4)).setText(name);
    				break;
    			case 5:
    				((TextView)findViewById(R.id.diceRollerTextExpression5)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName5)).setText(name);
    				break;
    			case 6:
    				((TextView)findViewById(R.id.diceRollerTextExpression6)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName6)).setText(name);
    				break;
    			case 7:
    				((TextView)findViewById(R.id.diceRollerTextExpression7)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName7)).setText(name);
    				break;
    			case 8:
    				((TextView)findViewById(R.id.diceRollerTextExpression8)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName8)).setText(name);
    				break;
    			case 9:
    				((TextView)findViewById(R.id.diceRollerTextExpression9)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName9)).setText(name);
    				break;
    			case 10:
    				((TextView)findViewById(R.id.diceRollerTextExpression10)).setText(exp);
    				((TextView)findViewById(R.id.diceRollerTextName10)).setText(name);
    				break;
    		}
    		
    		saveDataToPrefs();
    	}
    	else if (requestCode == DICE_ROLLER_KEYBOARD_BONUS_DAMAGE_ACTIVITY)
    	{
    		if(intent == null)
    			return;
    		String exp = intent.getStringExtra("expression");
    		if(exp == null)
    			diceRollerReportArea.append("\n\nError in bonus dice calculation.");
    		
    		DiceExpressionRoller der = new DiceExpressionRoller(exp);
    		if(der.errorInExpression())
    			diceRollerReportArea.append("There is an error in your bonus dice expression. ");
    		else
    		{
    			if(diceSoundEnabled)
        			diceSound.start();
    			String desc = "\nBonus Dice\n" + der.rollResultJustDiceToString() + "\n";
    			desc += "Total: " + (sumOfMax + der.getResult());
    			diceRollerReportArea.append(desc);
    			diceHistory.add(diceHistory.size()-1, desc + "\n" + HISTORY_SEPARATOR + "\n");
    			
    		}
    		
    	}
    }
    
    private void characterTakeDamage(int x)
    {
    	int preDamageHP = duderino.getCurrentHitPoints();
    	duderino.takeDamage(x);
    	String hpHistDesc = "HP Before Damage: " + preDamageHP + "\n";
    	hpHistDesc += "Damage taken: " + x + "\n";
    	hpHistDesc += "HP After Damage: " + duderino.getCurrentHitPoints() + "\n";
    	hpHistDesc += HISTORY_SEPARATOR + "\n";
    	hitPointHistory.add(hpHistDesc);
    	hitPointDisplayView.invalidate();
    	saveDataToPrefs();
    }
    
    private void characterHeal(int x)
    {
    	int preHealHP = duderino.getCurrentHitPoints();
    	duderino.heal(x);
    	String hpHistDesc = "HP Before Heal: " + preHealHP + "\n";
    	hpHistDesc += "Healed by: " + x + "\n";
    	hpHistDesc += "HP After Heal: " + duderino.getCurrentHitPoints() + "\n";
    	hpHistDesc += HISTORY_SEPARATOR + "\n";
    	hitPointHistory.add(hpHistDesc);
    	hitPointDisplayView.invalidate();
    	saveDataToPrefs();
    }
    
    
    
    private void characterLoseSurge()
    {
    	if(duderino.getCurrentSurges() > 0)
    		duderino.loseSurge();
    	else
    		Toast.makeText(this, "you have no surges to lose, my friend", 
    				Toast.LENGTH_LONG).show();
    	surgeNumberView.invalidate();
    	saveDataToPrefs();
    }
    
    private void characterGainSurge()
    {
    	duderino.gainSurge();
    	surgeNumberView.invalidate();
    	saveDataToPrefs();
    }
    
    private void characterGainPowerPoints()
    {
    	startPowerPointActivity("gain");
    }
    
    private void characterUsePowerPoints()
    {
    	startPowerPointActivity("use");
    }
    
    private void characterGainActionPoint()
    {
    	duderino.increaseActionPoints();
    	apNumberView.invalidate();
    	saveDataToPrefs();
    }
    
    private void characterLoseActionPoint()
    {
    	if(duderino.getActionPoints() > 0)
    	{
    			duderino.decreaseActionPoints();
    			
    			String dialogText = "Don't forget to use your Action Point feature or feats!";
    			String apRemind = duderino.getActionPointReminder();
    			if(apRemind != null && apRemind.length() > 0)
    				dialogText += " Your reminder:\n\n" + apRemind;
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage(dialogText).
        			setPositiveButton("Done", null).show();
    	}
    	else
    		Toast.makeText(this, "you have no action points to spend, my friend", 
    				Toast.LENGTH_LONG).show();
    	apNumberView.invalidate();
    	saveDataToPrefs();
    }
    
    private void setupClickers()
    {
    	
    	tabHost.setOnTabChangedListener(new OnTabChangeListener(){
    	    public void onTabChanged(String tabId) {
    	        if(tabId.equals("tabDice"))
    	        {
    	        	boolean firstUseOfDiceRoller = FileIOHelper.isFirstUseOfDiceRoller();
    	        	String firstUseDiceDirections = getResources().getString(
    	        				R.string.first_dice_roller_use_help);
    	        	if(firstUseOfDiceRoller)
    	        	{
    	        		AlertDialog.Builder builder = new AlertDialog.Builder(
    	        					ConditionTracker.this);
    	        		builder.setMessage(firstUseDiceDirections).
    	        			setPositiveButton("Done", null).show();
    	        	}
    	        	((ScrollView)findViewById(R.id.tabDiceExpressionBankScrollView)).
    	        			fullScroll(ScrollView.FOCUS_UP);
    	        }
    	        else if(tabId.equals("tabMain"))
    	        {
    	        	//try to move the condition list to its top??    	       
    	        	conditionListView.setSelection(0);
    	        }
    	        else if(tabId.equals("tabNotes"))
    	        {    	        	
    	        	//find the first item in the list and scroll to it???
    	        	notesListView.setSelection(0);
    	        }
    	    }
    	});

    	
    	hitPointDisplayView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {        		        			
        		String hpHist = "";
        		for(int k = hitPointHistory.size() - 1; k >= 0; k--)
        			hpHist += hitPointHistory.get(k);
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage(hpHist).setPositiveButton("Done", null).show();
        	}
        		
        });
    	
    	
    	hpMinusControlView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) { 
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		characterTakeDamage(1);        		        		       				        		
        	}
        		
        });
    	
    	hpMinusControlView.setOnLongClickListener( new View.OnLongClickListener() {			
			public boolean onLongClick(View v) {
				startDamageActivity();
				return true;			
			}
        		
    	});
    	
    	hpPlusControlView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		characterHeal(1);        		        		       				        		
        	}
        		
        });
    	
    	hpPlusControlView.setOnLongClickListener( new View.OnLongClickListener() {			
			public boolean onLongClick(View v) {				
				startHealActivity();
				return true;			
			}
        		
    	});
    	
        
        
        
        thpControlView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startTempHitPointActivity();
        		//characterAddTempHitPoints();
        	}
        });
        
 /*       thpControlView.setOnLongClickListener( new View.OnLongClickListener() {			
			public boolean onLongClick(View v) {				
				startTempHitPointActivity();
				return true;			
			}
        		
    	});
*/    	
        
        surgeNumberView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		            characterGainSurge();
        		            break;

        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	characterLoseSurge();
        		            break;
        		            
        		        case DialogInterface.BUTTON_NEUTRAL:
        		        	Toast.makeText(ConditionTracker.this, "cancelled...as you wish", 
        		        			Toast.LENGTH_SHORT).show();
        		            break;
        		        }
        		    }
        		};

        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to lose or gain a healing surge " +
        				"(note that there will be no healing either way)?").
        			setPositiveButton("Gain", dialogClickListener).
        		    setNegativeButton("Lose", dialogClickListener).
        		    setNeutralButton("Cancel", dialogClickListener).show();
        		
        	}
        });
        
        apNumberView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		            characterGainActionPoint();
        		            break;

        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	characterLoseActionPoint();
        		            break;
        		            
        		        case DialogInterface.BUTTON_NEUTRAL:
        		        	Toast.makeText(ConditionTracker.this, "cancelled...as you wish", 
        		        			Toast.LENGTH_SHORT).show();
        		            break;
        		        }
        		    }
        		};

        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to use or add an action point?").
        			setPositiveButton("Add", dialogClickListener).
        		    setNegativeButton("Use", dialogClickListener).
        		    setNeutralButton("Cancel", dialogClickListener).show();
        	}
        });
        
        
        powerPtNumberView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		            characterGainPowerPoints();
        		            break;

        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	characterUsePowerPoints();
        		            break;
        		            
        		        case DialogInterface.BUTTON_NEUTRAL:
        		        	Toast.makeText(ConditionTracker.this, "cancelled...as you wish", 
        		        			Toast.LENGTH_SHORT).show();
        		            break;
        		        }
        		    }
        		};

        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to use or gain power points?").
        			setPositiveButton("Gain", dialogClickListener).
        		    setNegativeButton("Use", dialogClickListener).
        		    setNeutralButton("Cancel", dialogClickListener).show();
        		
        	}
        });
        
        
        
        addBadConditionButton.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startAddBadActivity();
        	}
        		
        });
        
        addGoodConditionButton.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startAddGoodActivity();
        	}
        		
        });
        
        
        notesStartEditor.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startNotesEditorActivity(-1);	//negative param indicates a new note, not an edit
        	}
        		
        });
        
        
        notesListView.setOnItemClickListener(new OnItemClickListener()
        {
             @SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView av, View view, int pos, long y)
             {       		         		
            	startNotesEditorActivity(pos);    	//a pos represents an edit of existing note        	 
             }
        });
        
        
        notesListView.setOnItemLongClickListener(new OnItemLongClickListener() 
        {
        	@SuppressWarnings("rawtypes")
			public boolean onItemLongClick(AdapterView parent, View v, int position, long id) 
        	{
        		
        		String noteString = notesStringArrayList.get(position);        		
        		deletableNote = Note.makeNote(noteString);
        
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	duderino.removeNote(deletableNote);
        		        	notesStringArrayList = notesToStringArrayList(duderino.getNotes());
        	        		   		
        	          		notesAdapter = new NotesAdapter(
        	          				ConditionTracker.this, R.layout.notes_list_row_layout,
        	          			    notesStringArrayList);

        	          		notesListView.setAdapter(notesAdapter);
        	          		notesListView.invalidate();
        		        	notesListView.setSelection(0);
        		        	saveDataToPrefs();
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEUTRAL:
        		        	Toast.makeText(ConditionTracker.this, "not deleting...as you wish", 
        		        			Toast.LENGTH_SHORT).show();
        		            break;
        		        }
        		    }
        		};
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to delete the note " + 
        				deletableNote.getTitle() + "?").
        			setPositiveButton("Delete it!", dialogClickListener).        		    
        		    setNeutralButton("Cancel", dialogClickListener).show();
        		
        		return true;
        		
        	}
        	
        });
        
        conditionListView.setOnItemClickListener(new OnItemClickListener()
        {
             @SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView av, View view, int pos, long y)
             {
            	       		
         		
            	String condString = conditionsStringArrayList.get(pos);        		
          		Condition displayableCondition = Condition.makeCondition(condString);
          		
            	String longDescription = displayableCondition.getLongDescription(
            			getResources().getStringArray(R.array.conditionGlossaryKeys), 
            			getResources().getStringArray(R.array.conditionGlossaryValues));
            	longDescription += "\n\n(Use long-press to delete conditions.)";
            	AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
         		builder.setMessage(longDescription).
         				setPositiveButton("Done", null).show();
            	
             }
        });
        
        conditionListView.setOnItemLongClickListener(new OnItemLongClickListener() 
        {
        	@SuppressWarnings("rawtypes")
			public boolean onItemLongClick(AdapterView parent, View v, int position, long id) 
        	{
        		
        		String condString = conditionsStringArrayList.get(position);        		
        		deletableCondition = Condition.makeCondition(condString);
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	duderino.removeCondition(deletableCondition);
        	        		conditionsStringArrayList = conditionsToStringArrayList(
        	        				duderino.getConditions());    		
        	          		conditionsAdapter = new ConditionsAdapter(
        	          				ConditionTracker.this, R.layout.condition_list_row_layout,
        	          			    conditionsStringArrayList);

        	          		conditionListView.setAdapter(conditionsAdapter);
        	          		conditionListView.invalidate();
        		        	conditionListView.setSelection(0);
        		        	saveDataToPrefs();
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEUTRAL:
        		        	Toast.makeText(ConditionTracker.this, "not deleting...as you wish", 
        		        			Toast.LENGTH_SHORT).show();
        		            break;
        		        }
        		    }
        		};        		
        		
        		String condForDialog = condString.substring(0, condString.lastIndexOf(":"));
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to delete the condition " + 
        				condForDialog + "?").
        			setPositiveButton("Delete it!", dialogClickListener).        		    
        		    setNeutralButton("Cancel", dialogClickListener).show();
        		
        		return true;
        	}
        });
        
        
        diceRollerEditButton1.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(1);
        	}        		
        });
        
        diceRollerEditButton2.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(2);
        	}        		
        });
        
        diceRollerEditButton3.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(3);
        	}        		
        });
        
        diceRollerEditButton4.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(4);
        	}        		
        });
        
        diceRollerEditButton5.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(5);
        	}        		
        });
        
        diceRollerEditButton6.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(6);
        	}        		
        });
        
        diceRollerEditButton7.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(7);
        	}        		
        });
        
        diceRollerEditButton8.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(8);
        	}        		
        });
        
        diceRollerEditButton9.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(9);
        	}        		
        });
        
        diceRollerEditButton10.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		startDiceRollerKeyboardActivity(10);
        	}        		
        });
        
        diceRollerRollButton1.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);        		
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression1)).getText().
        						toString();
        		rollExpression(exp, 1);	
        	}        		
        });
        
        diceRollerRollButton2.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression2)).getText().
        						toString();
        		rollExpression(exp, 2);	
        	}        		
        });
        
        diceRollerRollButton3.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression3)).getText().
        						toString();
        		rollExpression(exp, 3);	
        	}        		
        });
        
        diceRollerRollButton4.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression4)).getText().
        						toString();
        		rollExpression(exp, 4);	
        	}        		
        });
        
        diceRollerRollButton5.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression5)).getText().
        						toString();
        		rollExpression(exp, 5);	
        	}        		
        });
        
        diceRollerRollButton6.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression6)).getText().
        						toString();
        		rollExpression(exp, 6);	
        	}        		
        });
        
        diceRollerRollButton7.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression7)).getText().
        						toString();
        		rollExpression(exp, 7);	
        	}        		
        });
        
        diceRollerRollButton8.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression8)).getText().
        						toString();
        		rollExpression(exp, 8);	
        	}        		
        });
        
        diceRollerRollButton9.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression9)).getText().
        						toString();
        		rollExpression(exp, 9);	
        	}        		
        });
        
        diceRollerRollButton10.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		String exp = ((TextView)findViewById(R.id.diceRollerTextExpression10)).getText().
        						toString();
        		rollExpression(exp, 10);	
        	}        		
        });
        
        diceRollerMaxButton1.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression1)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 1);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 1);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton2.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression2)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 2);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 2);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton3.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression3)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 3);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 3);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton4.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression4)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 4);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 4);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton5.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression5)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 5);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 5);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton6.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression6)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 6);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 6);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton7.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression7)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 7);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 7);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton8.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression8)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 8);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 8);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton9.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression9)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 9);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 9);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        diceRollerMaxButton10.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		expToMax = ((TextView)findViewById(R.id.diceRollerTextExpression10)).
        						getText().toString();
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	rollMaxExpressionWithBonusDice(expToMax, 10);
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEGATIVE:
        		        	rollMaxExpressionNoBonusDice(expToMax, 10);
        		            break;
        		        }
        		    }
        		};        		
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
        		builder.setMessage("Would you like to roll bonus damage?").
        			setPositiveButton("Yes", dialogClickListener).        		    
        		    setNegativeButton("No", dialogClickListener).show();
        		
        				
        	}        		
        });
        
        d20DiceRollerImageView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		v.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
        		rollD20();
        	}        		
        });
        
        diceRollerHistoryImageView.setOnClickListener( new View.OnClickListener() {
        	public void onClick(View v) {
        		showDiceHistory();
        	}        		
        });

    }
    
    //for now...
    private void startDiceRollerKeyboardActivity(int whichArea)
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, DiceRollerCustomKeyboardActivity.class);
		String currExp = "";
		String name = "";
		switch(whichArea){
			case 1:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression1)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName1)).
						getText().toString();
				break;
			case 2:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression2)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName2)).
						getText().toString();
				break;
			case 3:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression3)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName3)).
						getText().toString();
				break;
			case 4:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression4)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName4)).
						getText().toString();
				break;
			case 5:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression5)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName5)).
						getText().toString();
				break;
			case 6:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression6)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName6)).
						getText().toString();
				break;
			case 7:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression7)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName7)).
						getText().toString();
				break;
			case 8:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression8)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName8)).
						getText().toString();
				break;
			case 9:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression9)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName9)).
						getText().toString();
				break;
			case 10:
				currExp = ((TextView)findViewById(R.id.diceRollerTextExpression10)).
						getText().toString();
				name = ((TextView)findViewById(R.id.diceRollerTextName10)).
						getText().toString();
				break;
				
		}
    	i.putExtra("expression", currExp);
		i.putExtra("whichArea", whichArea);
		i.putExtra("name", name);
		startActivityForResult(i,DICE_ROLLER_KEYBOARD_ACTIVITY);
    }
    
    private void startHealActivity()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, HealActivity.class);
		startActivityForResult(i,HEAL_ACTIVITY);
    }
    
    private void startTempHitPointActivity()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, TempHitPointActivity.class);
		startActivityForResult(i,TEMP_HIT_POINT_ACTIVITY);
    }
    
    private void startPowerPointActivity(String typeUseOrGain)
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, PowerPointActivity.class);
    	i.putExtra("typeUseOrGain", typeUseOrGain);
		startActivityForResult(i,POWER_POINT_ACTIVITY);
    }
    
    private void startDamageActivity()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, DamageActivity.class);
		startActivityForResult(i,DAMAGE_ACTIVITY);
    }
    
    private void startAddBadActivity()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, AddBadConditionActivity.class);
		startActivityForResult(i,ADD_BAD_CONDITION_ACTIVITY);
    }
    
    private void startAddGoodActivity()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, AddGoodConditionActivity.class);
		startActivityForResult(i,ADD_GOOD_CONDITION_ACTIVITY);
    }
    
    //if pos>=0, then edit the note in that position...otherwise new note
    private void startNotesEditorActivity(int pos)
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, NotesEditorActivity.class);
    	if( pos >= 0 )
    	{
    		String noteString = notesStringArrayList.get(pos);        		
      		Note currNote = Note.makeNote(noteString);
      		i.putExtra("title", currNote.getTitle());
      		i.putExtra("body", currNote.getBody());
      		i.putExtra("position", pos);
    	}
    	startActivityForResult(i, NOTES_EDITOR_ACTIVITY);
    }

    
    private void rollExpression( String exp, int whichArea )
    {
    	if(diceSoundEnabled)
			diceSound.start();
    	diceRollCounter++;
    	DiceExpressionRoller der = new DiceExpressionRoller(exp);
		if(der.errorInExpression())
			diceRollerReportArea.setText("There is an error in your expression. " +
					"Crits can only modify d20s and you can't reroll d20's. " +
					"Separate different dice and static modifiers with a +. You must " +
					"specify the number of dice. See the options menu for more help.");
		else
		{
			String name = getNameForDiceRollerExpression(whichArea);			
			String desc = der.toString();
			if(name.length() != 0)
			{
				diceRollerReportArea.setText(diceRollCounter + ") " + name + "\n" + desc);
				diceHistory.add(name + " " + exp + "\n" + desc + "\n" + HISTORY_SEPARATOR + 
						"\n");
			}
			else
			{
				diceRollerReportArea.setText(diceRollCounter + ") " + exp + " " + desc);
				diceHistory.add(exp + "\n" + desc + "\n" + HISTORY_SEPARATOR + "\n");
			}
		}
		if(der.isCrit())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
     		builder.setMessage("Crit!!").
     				setPositiveButton("OK", null).show();
		}
		else if(der.isFail())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
     		builder.setMessage("1's are bad...").
     				setPositiveButton("Shucks", null).show();
		}
			
		
		//if the dice history gets too big, trim the old ones
		if(diceHistory.size() > 50)
		{
			int counter = 0;
			while(counter < 30)
			{
				diceHistory.remove(0);
				counter++;
			}
		}
    }
    
    private void rollMaxExpressionNoBonusDice(String expMax, int whichArea)
    {
    	diceRollCounter++;
    	DiceExpressionRoller der = new DiceExpressionRoller(expMax);
		if(der.errorInExpression())
			diceRollerReportArea.setText("There is an error in your expression. " +
					"Crits can only modify d20s and you can't reroll d20's. " +
					"Separate different dice and static modifiers with a +. You must " +
					"specify the number of dice. See the options menu for more help.");
		else
		{
			String name = getNameForDiceRollerExpression(whichArea);
			String desc = expMax + " maximized: " + der.getMaximizedResult();
			if(name.length() != 0)
			{
				diceRollerReportArea.setText(diceRollCounter + ") " + name + "\n" + desc);
				diceHistory.add(name + "\n" + desc + "\n" + HISTORY_SEPARATOR + "\n");
			}
			else
			{
				diceRollerReportArea.setText(diceRollCounter + ") " + desc);
				diceHistory.add(desc + "\n" + HISTORY_SEPARATOR + "\n");
			}
		}
    }
    
    private void rollMaxExpressionWithBonusDice(String expMax, int whichArea)
    {
    	diceRollCounter++;
    	//create a DiceExpressionRoller and call getMax...report that...start a roller activity
    	//with a different activity code...in the onActivityResult, create a DiceExpressionRoller
    	//and APPEND the result to the report area...check for errors here and in onActivityResult
    	DiceExpressionRoller der = new DiceExpressionRoller(expMax);
		if(der.errorInExpression())
			diceRollerReportArea.setText("There is an error in your expression. " +
					"Crits can only modify d20s and you can't reroll d20's. " +
					"Separate different dice and static modifiers with a +. You must " +
					"specify the number of dice. See the options menu for more help.");
		else
		{
			String name = getNameForDiceRollerExpression(whichArea);
			String desc = expMax + " maximized: " + der.getMaximizedResult();
			
			if(name.length() != 0)
			{
				diceRollerReportArea.setText(diceRollCounter + ") " + name + "\n" + desc);
				diceHistory.add(name + "\n" + desc);
			}
			else
			{
				diceRollerReportArea.setText(diceRollCounter + ") " + desc);
				diceHistory.add(desc);
			}
			sumOfMax = der.getMaximizedResult();
			saveDataToPrefs();
			Intent i = new Intent(this, DiceRollerCustomKeyboardActivity.class);
			startActivityForResult(i, DICE_ROLLER_KEYBOARD_BONUS_DAMAGE_ACTIVITY);
		}
    	
    	
    }

    private void rollD20()
    {
    	if(diceSoundEnabled)
			diceSound.start();
    	diceRollCounter++;
    	int roll = random.nextInt(20) + 1;
    	String desc = "Quick d20 Result: " + roll;
		diceRollerReportArea.setText(diceRollCounter + ") " + desc);
		diceHistory.add(desc + "\n" + HISTORY_SEPARATOR + "\n");
		if(roll == 20)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
     		builder.setMessage("Crit!!").
     				setPositiveButton("OK", null).show();
		}
		else if (roll == 1)			
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
     		builder.setMessage("1's are bad...").
     				setPositiveButton("Shucks", null).show();
		}
    }
    
    private String getNameForDiceRollerExpression(int whichArea)
    {
    	String name = "";
		if(whichArea > 0 && whichArea <= 10)
			name = duderino.getDiceExpressionNames().get(whichArea - 1); //list vs number...OBO
		return name;
    }
    
    private void showDiceHistory()
    {
    	String history = "";
    	if(diceHistory.size() == 0)
    		history = "No entries were found in the dice history.";
    	int end = 0;
    	if (diceHistory.size() > 20)
    		end = diceHistory.size() - 20;
    	for(int k = diceHistory.size() - 1; k >= end; k--)
    		history += diceHistory.get(k);
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
 		builder.setMessage(history).setPositiveButton("OK", null).show();
    	
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
    
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.condition_tracker_menu_options, menu);
    	return true;
    }
     
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
    	int itemId = item.getItemId();
    	if(itemId == R.id.options_menu_new_character)
    	{
    		newCharacter();
    		return true;
    	}
    	else if(itemId == R.id.options_menu_save_character)
    	{
    		saveDataToPrefs();
    		String message = "All settings for " + duderino.getName() + " have been saved.";
    		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
    		builder.setMessage(message).setPositiveButton("OK", null).show();
    		return true;
    	}
    	else if (itemId == R.id.options_menu_configure_character)
    	{
    		updateCharacterSettings();
    		return true;
    	}
    	else if (itemId == R.id.options_menu_load_character)
    	{
    		loadCharacter();
    		return true;
    	}
    	else if (itemId == R.id.options_menu_extended_rest)
    	{
    		DialogInterface.OnClickListener dialogClickListener = 
    			new DialogInterface.OnClickListener() {
    		    @Override
    		    public void onClick(DialogInterface dialog, int which) {
    		        switch (which){
    		        case DialogInterface.BUTTON_POSITIVE:
    		        	duderino.takeExtendedRest();
    		        	String hpHistDesc = duderino.getName() + " took an extended rest; " +
    		        						"hit points reset to full (" + 
    		        						duderino.getCurrentHitPoints() +")\n";
    		        	hpHistDesc += HISTORY_SEPARATOR + "\n";
    		        	hitPointHistory.add(hpHistDesc);
    		      
    		    		hitPointDisplayView.invalidate();
    		    		surgeNumberView.invalidate();
    		    		apNumberView.invalidate();
    		    		conditionListView.invalidate();
    		    		saveDataToPrefs();
    		            break;
    		 
    		        case DialogInterface.BUTTON_NEUTRAL:
    		        	Toast.makeText(ConditionTracker.this, "work, work, work...no rest taken", 
    		        			Toast.LENGTH_SHORT).show();
    		            break;
    		        }
    		    }
    		};        		    		  
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
    		builder.setMessage("Are you sure that you want to take an extended rest? " +
    				"This can't be undone.").
    			setPositiveButton("Yes, let me rest", dialogClickListener).        		    
    		    setNeutralButton("Cancel", dialogClickListener).show();
    		return true;
    	}
    	else if (itemId == R.id.options_menu_dice_roller_help)
    	{
    		String longDescription = getString(R.string.dice_roller_help);
    		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
    		builder.setMessage(longDescription).
    			setPositiveButton("Done", null).show();
    		return true;
    	}
    	
    	return super.onMenuItemSelected(featureId, item);
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
    public void onBackPressed() {
        //determine which tab is active...switch back to main or ask for confirmation b4 exiting
    	TabHost tabHost = getTabHost();
    	int currTabIndex = tabHost.getCurrentTab();
    	
    	if(currTabIndex == 1 || currTabIndex == 2)
    	{
    		tabHost.setCurrentTab(0);
    	}
    	else
    	{
    		//ask the user if he/she wants to exit
    		DialogInterface.OnClickListener dialogClickListener = 
    			new DialogInterface.OnClickListener() {
    		    @Override
    		    public void onClick(DialogInterface dialog, int which) {
    		        switch (which){
    		        case DialogInterface.BUTTON_POSITIVE:
    		            //weird...but I need to call super.onBackPressed() and this is
    		        	//how I am going to accomplish it...so take that!
    		        	callSuperOnBackPressed();
    		            break;
    		        }
    		    }
    		};

    		AlertDialog.Builder builder = new AlertDialog.Builder(ConditionTracker.this);
    		builder.setMessage("Are you sure you want to exit?").
    			setPositiveButton("Yes, exit.", dialogClickListener).
    		    setNegativeButton("No, stay", dialogClickListener).show();    			
    	}
      
    }

    private void callSuperOnBackPressed()
    {
    	super.onBackPressed();
    }
    
    private void newCharacter()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, ConfigureCharacterActivity.class);
    	i.putExtra("isNewCharacter", true);
    	startActivityForResult(i, NEW_CHARACTER_ACTIVITY);
    }
    
    private void updateCharacterSettings()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, ConfigureCharacterActivity.class);
    	
    	if(duderino != null)
    	{
    		i.putExtra("maxHitPoints", duderino.getMaxHitPoints());
    		i.putExtra("maxSurges", duderino.getMaxSurges());
    		//if name isn't default, put an extra bit of info
    		if(!(duderino.getName().equals("default")))
    			i.putExtra("name", duderino.getName());
    		if(duderino.getSurgeValue() != duderino.getMaxHitPoints()/4)
    			i.putExtra("surgeValue", duderino.getSurgeValue());
    		if(duderino.getMaxPowerPoints() != 0)
    			i.putExtra("maxPowerPoints", duderino.getMaxPowerPoints());
    		String apRemind = duderino.getActionPointReminder();
        	if(apRemind != null && apRemind.length() != 0)
        		i.putExtra("actionPointReminder", apRemind);
        	i.putExtra("isNewCharacter", false);
    	}
    	startActivityForResult(i, CONFIGURE_ACTIVITY);
    }
    
    private void loadCharacter()
    {
    	saveDataToPrefs();
    	Intent i = new Intent(this, LoadCharacterListActivity.class);
    	startActivityForResult(i, LOAD_CHARACTER_ACTIVITY);
    }
    
    private ArrayList<String> conditionsToStringArrayList( ArrayList<Condition> list)
    {
    	ArrayList<String> ret = new ArrayList<String>();
    	for(int k = 0; k < list.size(); k++)
    	{
    		ret.add(list.get(k).toString());
    	}
    	return ret;
    }
    
    private ArrayList<String> notesToStringArrayList(ArrayList<Note> list)
    {
    	ArrayList<String> ret = new ArrayList<String>();
    	for(int k = 0; k < list.size(); k++)
    	{
    		ret.add(list.get(k).toString());
    	}
    	return ret;
    }
    
    //Hacky McHackington here
    private void updateCharacterTabWithName(String charName)
    {
    	RelativeLayout tabTesting = ((RelativeLayout)getTabWidget().getChildTabViewAt(0));
		TextView nameTextView = (TextView)tabTesting.getChildAt(1);
		nameTextView.setText(charName);
		nameTextView.invalidate();
    }
    
    private void togglePowerPointView()
    {
    	//hide the power point views if character has no power points
		TextView tv = (TextView)findViewById(R.id.powerPointTextView);
		PowerPointNumberView pp = (PowerPointNumberView)findViewById(
				R.id.powerPointNumberView);
		if(duderino.getMaxPowerPoints() == 0)
		{    			
			if(tv != null && pp != null)
			{
				tv.setVisibility(View.INVISIBLE);
				pp.setVisibility(View.INVISIBLE);
			}
		} 
		else
		{
			if(tv != null && pp != null)
			{
				tv.setVisibility(View.VISIBLE);
				pp.setVisibility(View.VISIBLE);
			}
		}
    }
    
    private void loadDataFromPrefs()
    {
    	//get the last used "current character file name" out of the data file...
    	//then load everything else out of the current character file
    	String currentCharacterName = FileIOHelper.getCurrentCharacterName();
    	CharacterDnD4Ed currentCharacter=FileIOHelper.getCharacterByName(currentCharacterName);
    	if(currentCharacter != null)
    		duderino = currentCharacter;
    	else
    		duderino = new CharacterDnD4Ed(currentCharacterName, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 
    							null, null, null, null);
    	
    	
    	
    	hitPointHistory = new ArrayList<String>();
		hitPointHistory.add("The history is empty.");
		
		diceHistory = new ArrayList<String>();
        diceRollCounter = 0;
        
        //Now start setting up the other fields for the ConditionTracker...
        
        
        //redo the condition stuffs...
		conditionsStringArrayList = conditionsToStringArrayList(duderino.getConditions());    		
		conditionsAdapter = new ConditionsAdapter(
			    ConditionTracker.this, R.layout.condition_list_row_layout,
			    conditionsStringArrayList);

		conditionListView.setAdapter(conditionsAdapter);
		conditionListView.invalidate();
        
        //setup the controls and redraw them
        hitPointDisplayView.setCharacter(duderino);
		surgeNumberView.setCharacter(duderino);
		apNumberView.setCharacter(duderino);
		powerPtNumberView.setCharacter(duderino);
        hitPointDisplayView.invalidate();
        surgeNumberView.invalidate();
        apNumberView.invalidate();
        powerPtNumberView.invalidate();
        
        //hide the power point views if character has no power points
		togglePowerPointView();
        
		//change tab name
		updateCharacterTabWithName(duderino.getName());
		
        notesStringArrayList = notesToStringArrayList(duderino.getNotes());
        
        //re-set the visible notes to this character
        notesAdapter = new NotesAdapter(this, R.layout.notes_list_row_layout, 
        		notesStringArrayList);
        notesListView.setAdapter(notesAdapter);
		notesListView.invalidate();
        
        
        //dice roller expressions for character   
		ArrayList<String> diceExps = duderino.getDiceExpressions();
		((TextView)findViewById(R.id.diceRollerTextExpression1)).setText(diceExps.get(0));
		((TextView)findViewById(R.id.diceRollerTextExpression2)).setText(diceExps.get(1));
		((TextView)findViewById(R.id.diceRollerTextExpression3)).setText(diceExps.get(2));
		((TextView)findViewById(R.id.diceRollerTextExpression4)).setText(diceExps.get(3));
		((TextView)findViewById(R.id.diceRollerTextExpression5)).setText(diceExps.get(4));
		((TextView)findViewById(R.id.diceRollerTextExpression6)).setText(diceExps.get(5));
		((TextView)findViewById(R.id.diceRollerTextExpression7)).setText(diceExps.get(6));
		((TextView)findViewById(R.id.diceRollerTextExpression8)).setText(diceExps.get(7));
		((TextView)findViewById(R.id.diceRollerTextExpression9)).setText(diceExps.get(8));
		((TextView)findViewById(R.id.diceRollerTextExpression10)).setText(diceExps.get(9));
		
		//names for the dice expressions
        ArrayList<String> diceNames = duderino.getDiceExpressionNames();
        ((TextView)findViewById(R.id.diceRollerTextName1)).setText(diceNames.get(0));
        ((TextView)findViewById(R.id.diceRollerTextName2)).setText(diceNames.get(1));
        ((TextView)findViewById(R.id.diceRollerTextName3)).setText(diceNames.get(2));
        ((TextView)findViewById(R.id.diceRollerTextName4)).setText(diceNames.get(3));
        ((TextView)findViewById(R.id.diceRollerTextName5)).setText(diceNames.get(4));
        ((TextView)findViewById(R.id.diceRollerTextName6)).setText(diceNames.get(5));
        ((TextView)findViewById(R.id.diceRollerTextName7)).setText(diceNames.get(6));
        ((TextView)findViewById(R.id.diceRollerTextName8)).setText(diceNames.get(7));
        ((TextView)findViewById(R.id.diceRollerTextName9)).setText(diceNames.get(8));
        ((TextView)findViewById(R.id.diceRollerTextName10)).setText(diceNames.get(9));
        
    }
    
    private void saveDataToPrefs()
    {
    	FileIOHelper.saveCurrentCharacterName(duderino.getName());
    	FileIOHelper.saveCharacter(duderino);    	
    }

    
    
   
    
    //a private class to get a fancy list going...one hopes
    private class ConditionsAdapter extends BaseAdapter 
    {
   	 		ArrayList<String> items;

   	  public ConditionsAdapter(Context context, int textViewResourceId, ArrayList<String> items) 
   	  {   		
   	 		this.items = items;
   	  }

   	  @Override
   	  public View getView(final int position, View convertView, ViewGroup parent) 
   	  {
   		  	TextView condDescription, endsDescription;
		   	View view = convertView;
		   	ImageView image;
		   if (view == null) {
		    	LayoutInflater vi = (LayoutInflater) getSystemService(
		    			Context.LAYOUT_INFLATER_SERVICE);
		    	view = vi.inflate(R.layout.condition_list_row_layout, null);
		   }
		    image = (ImageView) view.findViewById(R.id.condition_list_row_image);
		   	condDescription = (TextView) view.findViewById(R.id.condition_list_row_cond_desc);
		   	endsDescription = (TextView) view.findViewById(R.id.condition_list_row_ends_desc);
		   	Condition cond = Condition.makeCondition(items.get(position));
		  
		   	
		   	if(cond==null)
		   		cond = new ConditionBad(new ArrayList<String>(), "");
		   	if(cond.getType().equals("good"))
		   		image.setImageResource(R.drawable.good_condition);
		   	else
		   		image.setImageResource(R.drawable.bad_condition);
		   	
		   	condDescription.setText(cond.getKeywordsToString());
		   	
		   	
		   	
		   	String ending = cond.getEnding();
		   	if(ending.equals(Condition.SAVE_ENDS))
		   		endsDescription.setTextColor(getResources().getColor(R.color.blue));
		   	else if (ending.equals(Condition.END_OF_YOUR_NEXT_TURN) || 
		   			 ending.equals(Condition.BEGINNING_OF_YOUR_NEXT_TURN))
		   		endsDescription.setTextColor(getResources().getColor(R.color.green));
		   	else if (ending.equals(Condition.END_OF_ATTACKERS_NEXT_TURN) ||
		   			 ending.equals(Condition.BEGINNING_OF_ATTACKERS_NEXT_TURN))
		   		endsDescription.setTextColor(getResources().getColor(R.color.yellow));
		   	else
		   		endsDescription.setTextColor(getResources().getColor(R.color.red));
		   	endsDescription.setText("Until: " + Condition.getShortEnding(ending));
   
   	   	   	return view;
   	  }

   	
   	  public int getCount() {
   		  	return items.size();
   	  }

   	  public Object getItem(int position) {
   		//changed from return position...which isn't the item, right?
   		  	return position;	
   	  }

   	  public long getItemId(int position) {
   		  	return position;
   	  }
   }
   
    
    
    //an adapter for the list of notes
    private class NotesAdapter extends BaseAdapter 
    {
   	 		ArrayList<String> items;

   	  public NotesAdapter(Context context, int textViewResourceId, ArrayList<String> items) 
   	  {   		
   	 		this.items = items;
   	  }

   	  @Override
   	  public View getView(final int position, View convertView, ViewGroup parent) 
   	  {
   		   TextView noteTitle, noteBody;
		   View view = convertView;

		   if (view == null) {
		    	LayoutInflater vi = (LayoutInflater) getSystemService(
		    			Context.LAYOUT_INFLATER_SERVICE);
		    	view = vi.inflate(R.layout.notes_list_row_layout, null);
		   }
		   	noteTitle = (TextView) view.findViewById(R.id.notes_list_row_layout_title);
		   	noteBody = (TextView) view.findViewById(R.id.notes_list_row_layout_body);
		   	Note note = Note.makeNote(items.get(position));
		   	
		   	
		   	if(note==null)
		   		note = new Note("empty", "");
		   	
		   	noteTitle.setText(note.getTitle());
		   	noteBody.setText("(Click to view/edit): " + note.getBody());
   
   	   	   	return view;
   	  }

   	
   	  public int getCount() {
   		  	return items.size();
   	  }

   	  public Object getItem(int position) {
   		//changed from return position...which isn't the item, right?
   		  	return position;	
   	  }

   	  public long getItemId(int position) {
   		  	return position;
   	  }
   }
   
}