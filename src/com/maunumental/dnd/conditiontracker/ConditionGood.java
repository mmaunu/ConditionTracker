package com.maunumental.dnd.conditiontracker;

import java.util.ArrayList;

public class ConditionGood extends Condition 
{
	
	private static final long serialVersionUID = 8170690971950595336L;

	public ConditionGood(ArrayList<String> keywords, String enders)
	{
		super(keywords, enders, "good");
	}
}
