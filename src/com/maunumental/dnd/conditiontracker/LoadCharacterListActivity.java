package com.maunumental.dnd.conditiontracker;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class LoadCharacterListActivity extends ListActivity 
{
	private ListView listView;
	private ArrayList<String> characterNames;
	private String charToDelete;
	
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.load_character_list_activity);
		
		setupListAdapter();
		
		listView = getListView();
		
		setupClickers();
		
	}
	
	
	
	private void setupClickers()
	{
		listView.setOnItemLongClickListener(new OnItemLongClickListener() 
        {
        	@SuppressWarnings("rawtypes")
			public boolean onItemLongClick(AdapterView parent, View v, int position, long id) 
        	{
        		
        		charToDelete = (String)listView.getItemAtPosition(position);        		
        		
        		DialogInterface.OnClickListener dialogClickListener = 
        			new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        switch (which){
        		        case DialogInterface.BUTTON_POSITIVE:
        		        	
        		        	FileIOHelper.deleteCharacter(charToDelete);
        		        
        		        	setupListAdapter();	
        		        	
        		            break;
        		 
        		        case DialogInterface.BUTTON_NEUTRAL:
        		        	Toast.makeText(LoadCharacterListActivity.this,"not deleting...as you wish", 
        		        			Toast.LENGTH_SHORT).show();
        		            break;
        		        }
        		    }
        		};
        		
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(LoadCharacterListActivity.this);
        		builder.setMessage("Are you sure that you would you like to delete the character " + 
        				charToDelete + "?").
        			setPositiveButton("Delete!", dialogClickListener).        		    
        		    setNegativeButton("Cancel!", dialogClickListener).show();
        	
        		return true;
        	}
        });
	}


	private void setupListAdapter()
	{	
		characterNames = FileIOHelper.getAllCharacterNames();
		Collections.sort(characterNames);
		//create an ArrayAdapter and set it as the list adapter
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, characterNames));
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//get the name that was clicked
		String name = this.getListAdapter().getItem(position).toString();
		Intent data = new Intent();
		data.putExtra("name", name);
		setResult(Activity.RESULT_OK, data);
		finish();
		
		//Create an intent and send back the name of the character to load
		
	}
	
	
	
	
}
