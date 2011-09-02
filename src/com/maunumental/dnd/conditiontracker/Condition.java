package com.maunumental.dnd.conditiontracker;

import java.io.Serializable;
import java.util.ArrayList;

//represents a condition on a character...like dazed and immobilized, save ends
public abstract class Condition implements Serializable
{
	
	private static final long serialVersionUID = 728393296974407610L;

	//stores condition keywords like dazed, immobilized, slowed etc.
	private ArrayList<String> keywords;
	
	//constants for ending conditions...with short and long versions
	public transient final static String 	SAVE_ENDS = "Save Ends", 
								END_OF_YOUR_NEXT_TURN = "End of Your Next Turn",
								BEGINNING_OF_YOUR_NEXT_TURN = "Beginning of Your Next Turn",
								END_OF_ATTACKERS_NEXT_TURN = "End of Attacker's Next Turn",
								BEGINNING_OF_ATTACKERS_NEXT_TURN = 
										"Beginning of Attacker's Next Turn",
								END_OF_ENCOUNTER = "End of the Encounter",
								END_OF_ALLYS_NEXT_TURN = "End of Ally's Next Turn",
								BEGINNING_OF_ALLYS_NEXT_TURN = "Beginning of Ally's Next Turn",
								NEXT_ROLL = "Next Roll",
								NEXT_ATTACK = "Next Attack",
								SUCCEED_ON_CHECK="Succeed on Check";
	
	public transient final static String 	SAVE_ENDS_SH = "Save Ends", 
								END_OF_YOUR_NEXT_TURN_SH = "E of YNT",
								BEGINNING_OF_YOUR_NEXT_TURN_SH = "B of YNT", 
								END_OF_ATTACKERS_NEXT_TURN_SH = "E of ANT",
								BEGINNING_OF_ATTACKERS_NEXT_TURN_SH = "B of ANT", 
								END_OF_ENCOUNTER_SH = "E of Enc",
								END_OF_ALLYS_NEXT_TURN_SH = "E of Al's NT",
								BEGINNING_OF_ALLYS_NEXT_TURN_SH = "B of Al's NT",
								NEXT_ROLL_SH = "Next Roll",
								NEXT_ATTACK_SH = "Next Attack",
								SUCCEED_ON_CHECK_SH = "Check";
	
	//like "save ends"...this should be one of the above values
	private String howConditionEnds;
	private String type;
	
	public Condition(ArrayList<String> conditions, String ends, String type)
	{
		keywords = new ArrayList<String>();
		for(String x: conditions)
			keywords.add(x);
		if (endingIsOk(ends))
			howConditionEnds = ends;
		else
			howConditionEnds = SAVE_ENDS;
		this.type = type;
	}
	
	public ArrayList<String> getKeywords()
	{
		return keywords;
	}
	
	public String getKeywordsToString()
	{
		String fred = "";
		//stop one short so there is no comma at the end
		for(int k = 0; k < keywords.size() - 1; k++)
			fred += keywords.get(k) +",";
		if(keywords.size() > 0)
			fred += keywords.get(keywords.size() - 1);
		return fred;
	}
	
	public String getEnding()
	{
		return howConditionEnds;
	}
	
	public String getType()
	{
		return type;
	}
	
	//this method is called with references to the key and value glossary arrays
	//it builds a long description using the text in the value array
	public String getLongDescription(String[] keys, String[] values)
	{
		//for each keyword, find the position in keys that corresponds to the keyword
		//and store that in positions...then build out of values using those positions
		//no condition is stored in position 0 in the glossary...some conditions are also 
		//just not in the glossary. For those conditions, store a neg. position in the 
		//array; the value of that neg. position is the opposite of its position in the
		//keywords arraylist
		int[] positions = new int[keywords.size()];
		
		int posForPositions = 0;
		
		for(int p = 0; p < keywords.size(); p++)
		{
			//get the current keyword from the list of keywords
			String keyword = keywords.get(p);
			//look for it in the list of keys...if you find it, record the
			//position into the positions array, if you didn't...see below
			boolean foundIt = false;
			for(int i = 0; i < keys.length; i++)
			{
				String currentKey = keys[i];
				if(keyword.indexOf(currentKey) == 0)
				{
					positions[posForPositions] = i;
					posForPositions++;
					foundIt = true;
					break;
				}
			}
			
			//either you found it in the glossary's list of keys or you didn't
			//if you didn't find it, then record the position from the list of keywords
			//instead...it means that the keyword is really just a description like +2 to attack
			//which has no entry in the glossary. 
			//Record the position as a negative so that later I can figure out where to 
			//get the value from...the list of values in the glossary or the keyword list (field)
			if(!foundIt)
			{
				positions[posForPositions] = -p;
				posForPositions++;
			}
		}
		
		String longDescription = "";
		
		//now just concatenate the keys and values into one long description (separate lines for
		//the keywords). The check for not 0...the array of positions gets defaulted to 0's
		//and there are conditions that have no glossary entry. Those conditions will not get found
		//by the nested loop above and so the value in the positions array will stay 0. If 
		//there is still a 0 entry in the array, it means that there is no entry in the 
		//glossary as well.
		for(int x: positions)
			if(x > 0)
				longDescription += keys[x] + ": " + values[x] + "\n\n";
			else
				longDescription += keywords.get(-x) + "\n\n";
		
		longDescription += "This condition lasts until:\n" + howConditionEnds;
		
		return longDescription;			
	}
	
	
	private static boolean endingIsOk(String ending)
	{
		return (ending.equals(SAVE_ENDS) || ending.equals(END_OF_YOUR_NEXT_TURN) || 
				ending.equals(BEGINNING_OF_YOUR_NEXT_TURN) || 
				ending.equals(END_OF_ATTACKERS_NEXT_TURN) ||
				ending.equals(BEGINNING_OF_ATTACKERS_NEXT_TURN) || 
				ending.equals(END_OF_ENCOUNTER) ||
				ending.equals(END_OF_ALLYS_NEXT_TURN) || 
				ending.equals(BEGINNING_OF_ALLYS_NEXT_TURN) ||
				ending.equals(NEXT_ROLL) || ending.equals(SUCCEED_ON_CHECK) ||
				ending.equals(NEXT_ATTACK)
				);
	}
	
	
	public boolean equals(Object obj)
	{
		if ( !(obj instanceof Condition) || obj == null )
			return false;
		Condition other = (Condition)obj;
		
		if (this.getType().equals(other.getType()) &&
				this.getEnding().equals(other.getEnding()) &&
				this.getKeywordsToString().equals(other.getKeywordsToString())
				)
			return true;
		else
			return false;
	}
	
	
	//the format here is realllly important...it must match the makeCondition() format.
	//keyword,keyword,...,keyword:endingDescription:typeGoodOrBad
	public String toString()
	{
		String fred = "";
		//stop one short so there is no comma at the end
		for(int k = 0; k < keywords.size() - 1; k++)
			fred += keywords.get(k) + ",";
		if(keywords.size() > 0)
			fred += keywords.get(keywords.size() - 1);
		
		fred += ":";
		fred += howConditionEnds;
		fred += ":";
		fred += type;
		return fred;
	}
	

	
	//This static method is used to build a Condition object from a String. The parameter
	//must be in the format that toString() specifies...keywords separated by commas which
	//are followed by a colon. After the first colon is the description of how a condition ends.
	//After the second colon is a type modifier...good or bad
	public static Condition makeCondition(String x)
	{
		if(x == null)
			return null;
		String[] parts = x.split(":");
		if(parts.length != 3)
			return null;
		
		String keyList = parts[0];
		String endCond = parts[1];
		String type = parts[2];
		
		String[] keys = keyList.split(",");
		ArrayList<String> list = new ArrayList<String>();
		for(String blah: keys)
			list.add(blah);
		if (type.equals("good"))
			return new ConditionGood(list, endCond);
		else
			return new ConditionBad(list,endCond);
	}

	
	public static String getShortEnding(String longVersion)
	{
		if(!endingIsOk(longVersion))
			return "error getting shorthand";
		else if(longVersion.equals(SAVE_ENDS))
				return SAVE_ENDS_SH;
		else if(longVersion.equals(END_OF_YOUR_NEXT_TURN))
			return END_OF_YOUR_NEXT_TURN_SH;
		else if(longVersion.equals(BEGINNING_OF_YOUR_NEXT_TURN))
			return BEGINNING_OF_YOUR_NEXT_TURN_SH;
		else if(longVersion.equals(END_OF_ATTACKERS_NEXT_TURN))
			return END_OF_ATTACKERS_NEXT_TURN_SH;
		else if(longVersion.equals(BEGINNING_OF_ATTACKERS_NEXT_TURN))
			return BEGINNING_OF_ATTACKERS_NEXT_TURN_SH;
		else if(longVersion.equals(END_OF_ENCOUNTER))
			return END_OF_ENCOUNTER_SH;
		else if(longVersion.equals(END_OF_ALLYS_NEXT_TURN))
			return END_OF_ALLYS_NEXT_TURN_SH;
		else if(longVersion.equals(BEGINNING_OF_ALLYS_NEXT_TURN))
			return BEGINNING_OF_ALLYS_NEXT_TURN_SH;
		else if(longVersion.equals(NEXT_ROLL))
			return NEXT_ROLL_SH;
		else if(longVersion.equals(NEXT_ATTACK))
			return NEXT_ATTACK_SH;
		else if(longVersion.equals(SUCCEED_ON_CHECK))
			return SUCCEED_ON_CHECK_SH;
		else
			return "error...shorthand list incomplete";
	}
	
	
	
	
	public static String getLongEnding(String shortVersion)
	{
		if(shortVersion.equals(SAVE_ENDS_SH))
			return SAVE_ENDS;
		else if(shortVersion.equals(END_OF_YOUR_NEXT_TURN_SH))
			return END_OF_YOUR_NEXT_TURN;
		else if(shortVersion.equals(BEGINNING_OF_YOUR_NEXT_TURN_SH))
			return BEGINNING_OF_YOUR_NEXT_TURN;
		else if(shortVersion.equals(END_OF_ATTACKERS_NEXT_TURN_SH))
			return END_OF_ATTACKERS_NEXT_TURN;
		else if(shortVersion.equals(BEGINNING_OF_ATTACKERS_NEXT_TURN_SH))
			return BEGINNING_OF_ATTACKERS_NEXT_TURN;
		else if(shortVersion.equals(END_OF_ENCOUNTER_SH))
			return END_OF_ENCOUNTER;
		else if(shortVersion.equals(END_OF_ALLYS_NEXT_TURN_SH))
			return END_OF_ALLYS_NEXT_TURN;
		else if(shortVersion.equals(BEGINNING_OF_ALLYS_NEXT_TURN_SH))
			return BEGINNING_OF_ALLYS_NEXT_TURN;
		else if(shortVersion.equals(NEXT_ROLL_SH))
			return NEXT_ROLL;
		else if(shortVersion.equals(NEXT_ATTACK_SH))
			return NEXT_ATTACK;
		else if(shortVersion.equals(SUCCEED_ON_CHECK_SH))
			return SUCCEED_ON_CHECK;
		else
			return "error getting long version...incomplete list";
	}
}
