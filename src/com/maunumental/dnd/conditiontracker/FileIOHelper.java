package com.maunumental.dnd.conditiontracker;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;


//a utility class to help with reading from and writing to files
public class FileIOHelper {

	public static final String DATA_FILE = "data_file", CHARACTER_FILE = "creature_file";
	
	public static Context context;
	
	
	public static void saveCurrentCharacterName(String name)
	{
		//save the name of the current character file to the data file
    	SharedPreferences settings = context.getSharedPreferences(DATA_FILE, 
    			Context.MODE_PRIVATE);
    	SharedPreferences.Editor baseEditor = settings.edit();
    	baseEditor.putString("currentCharacterName", name);
    	baseEditor.commit();
	}
	
	public static String getCurrentCharacterName()
	{
		SharedPreferences settings = context.getSharedPreferences(DATA_FILE, 
				Context.MODE_PRIVATE);
		String name = settings.getString("currentCharacterName", "Default");
		return name;
	}
	
	public static void saveCharacter(CharacterDnD4Ed character)
	{
		try
		{
			ArrayList<CharacterDnD4Ed> currList = getAllCharacters();
			
			
			//if the list is null...create one
			if(currList == null)
				currList = new ArrayList<CharacterDnD4Ed>();
			
			
			//now that we have the list, add the creature to it...removing an old
			//version of the creature if we find one (based on name...this could be an
			//update of an existing creature, so remove old version, add this new version)
			if(currList.contains(character))
				currList.remove(character);
			currList.add(character);
			
			
			//now write the list back
			FileOutputStream fos = context.openFileOutput(CHARACTER_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(currList);
			oos.close();
		}
		catch(Exception e)
		{
			Toast.makeText(context, "error writing creature to file in FileIOHelper " + 
					character + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	}
	
	
	public static void deleteCharacter(String name)
	{
		try
		{
			ArrayList<CharacterDnD4Ed> currList = getAllCharacters();
			
			
			//if the list is null...then there is nothing to delete
			if(currList == null)
				return;
			
			
			//now that we have the list, remove the correct character (if found)
			CharacterDnD4Ed toRemove = null;
			for(CharacterDnD4Ed x: currList)
				if(x.getName().equals(name))
				{
					toRemove = x;
					break;
				}
			
			if(toRemove != null)
				currList.remove(toRemove);
			
			//now write the list back
			FileOutputStream fos = context.openFileOutput(CHARACTER_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(currList);
			oos.close();
		}
		catch(Exception e)
		{
			Toast.makeText(context, "error writing creature to file in FileIOHelper " + 
					name + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<CharacterDnD4Ed> getAllCharacters()
	{
		ArrayList<CharacterDnD4Ed> list = new ArrayList<CharacterDnD4Ed>();
		try
		{
			FileInputStream fis = context.openFileInput(CHARACTER_FILE);
			ObjectInputStream ois = new ObjectInputStream(fis);
			list = (ArrayList<CharacterDnD4Ed>)ois.readObject();
		}
		catch(FileNotFoundException e)
		{
			//do nothing...file not found is PROBABLY b/c there is no file yet (no saved chars)
		}
		catch(Exception e)
		{
			Toast.makeText(context, "Error reading character file", Toast.LENGTH_LONG).show();
		}
		if(list != null)
			return list;
		else
			return new ArrayList<CharacterDnD4Ed>();
	}
	
	
	public static CharacterDnD4Ed getCharacterByName(String name)
	{
		CharacterDnD4Ed theOne = null;
		ArrayList<CharacterDnD4Ed> list = getAllCharacters();
		
		for(CharacterDnD4Ed curr: list)
			if(curr.getName().equals(name))
			{
				theOne = curr;
				break;
			}
		
		return theOne;
	}

	public static ArrayList<String> getAllCharacterNames()
	{
		ArrayList<CharacterDnD4Ed> list = getAllCharacters();
		ArrayList<String> names = new ArrayList<String>();
		for(CharacterDnD4Ed x: list)
			names.add(x.getName());
		return names;
	}
	
	public static boolean isFirstUseOfApp()
	{
		SharedPreferences settings = context.getSharedPreferences(DATA_FILE, 
				Context.MODE_PRIVATE);
		String first = settings.getString("firstUseOfApp", null);
		if(first == null)
		{			
			SharedPreferences.Editor editor = settings.edit();
		    //write a value so that we know that app has been started at least once
			editor.putString("firstUseOfApp", "used");
		    editor.commit();
			return true;
		}
		else 
			return false;  
	}
	
	public static boolean isFirstUseOfDiceRoller()
    {
		SharedPreferences settings = context.getSharedPreferences(DATA_FILE, 
							Context.MODE_PRIVATE);
		String first = settings.getString("firstUseOfDiceRoller", null);
        if(first == null)
        {        	
        	SharedPreferences.Editor editor = settings.edit();
            editor.putString("firstUseOfDiceRoller", "used");
            editor.commit();
        	return true;
        }
        else 
        	return false; 
    }
	
}


	
	
	
	
/*
	public static void saveEncounter(Encounter enc)
	{
		try
		{
			ArrayList<Encounter> currList = getAllEncounters();
			
			//if the list is null, create one
			if(currList == null)
				currList = new ArrayList<Encounter>();
			
			
			//now that we have the list, add the encounter to it...first remove old version
			//if there is one...names must be unique for encounters.
			if(currList.contains(enc))
				currList.remove(enc);
			currList.add(enc);
			
			//now write the list back
			FileOutputStream fos = context.openFileOutput(ENCOUNTER_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(currList);
			oos.close();
		}
		catch(Exception e)
		{
			Toast.makeText(context, "error writing encounter to file in FileIOHelper " + 
					enc + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Encounter> getAllEncounters()
	{
		ArrayList<Encounter> list = new ArrayList<Encounter>();
		try
		{
			FileInputStream fis = context.openFileInput(ENCOUNTER_FILE);
			ObjectInputStream ois = new ObjectInputStream(fis);
			list = (ArrayList<Encounter>)ois.readObject();
		}
		catch(Exception e)
		{
			if(FIRST_READ_OF_ENCOUNTER_FILE == false)
			{
				Toast.makeText(context, "error reading encounter file in FileIOHelper " + 
					ENCOUNTER_FILE, Toast.LENGTH_LONG).show();
			}
			else
				FIRST_READ_OF_ENCOUNTER_FILE = false;
		}
		if(list != null)
			return list;
		else
			return new ArrayList<Encounter>();
	}
	
	public static Encounter getEncounterByName(String name)
	{
		Encounter theOne = null;
		ArrayList<Encounter> list = getAllEncounters();
		
		for(Encounter curr: list)
			if(curr.getName().equals(name))
			{
				theOne = curr;
				break;
			}
		
		return theOne;
	}
*/	

