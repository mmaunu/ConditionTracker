package com.maunumental.dnd.conditiontracker;

import java.util.ArrayList;

public class ConditionBad extends Condition
{
	
	private static final long serialVersionUID = 144816741633989755L;

	public ConditionBad(ArrayList<String> keywords, String enders)
	{
		super(keywords, enders, "bad");
	}
}
