package com.maunumental.dnd.conditiontracker;

import java.util.ArrayList;

public class DiceExpressionRoller 
{
	private String expression;
	private boolean isCrit, isFail;
	private int sum;
	private boolean errorInExpression;
	private ArrayList<MultiplesOfSingleDie> posVariableTerms, negVariableTerms;
	private ArrayList<Integer> staticMods;	//as +3 or as -5...so pos and neg statics in one
	
	public DiceExpressionRoller(String exp)
	{
		expression = exp;
		isCrit = false;
		isFail = false;
		sum = 0;
		errorInExpression = false;
		roll();
	}
	
	
	//currently not subtracting...yikes?
	private void roll()
	{
		//this should correctly set the sum, isCrit, and isFail fields, hopefully not the error field
		
		
		
		String[] stringParts = expression.split("[+-]");
		posVariableTerms = new ArrayList<MultiplesOfSingleDie>();
		negVariableTerms = new ArrayList<MultiplesOfSingleDie>();
		staticMods = new ArrayList<Integer>();
		
		//now that it is split up by + and -, go through and find the terms that
		//represent static modifiers and the terms that represent 2d8 expressions
		//and load them into the appropriate ArrayLists
		for(String x: stringParts)
		{
			if(x.length() == 0)
				continue;
			//if it is a variable term...if it has a d in it
			if(x.indexOf("d") != -1)
			{
				try
				{
					MultiplesOfSingleDie mosd = new MultiplesOfSingleDie(x); 
					//x is of form NdM, so check the original expression for the location of -NdM...
					//if the indexOf -NdM is not -1, then -NdM is in the expression so x is subtracted
					//in the original expression...note this fails in expressions with +2d6 and -2d6
					//(expressions that have the same exact NdM expressions added and subtracted)
					boolean isNegativeTerm = (expression.indexOf("-"+x) != -1);
					if(isNegativeTerm)
						negVariableTerms.add(mosd);
					else
						posVariableTerms.add(mosd);
				}
				catch(Exception e)
				{
					errorInExpression = true;
				}
			}
			//else it is a static mod
			else
			{
				try
				{
					int temp = Integer.parseInt(x);
					int pos = expression.indexOf("-"+temp);
					//if there is a -x in the original expression...make sure that we should
					//in fact subtract by looking at the character right after the -x occurs
					//but also check the location of that next character (in case we hit end of 
					//expression, don't want an error in substring call). We have a neg. term if:
					//1) next term is actually just then end of the original expression: 2d6-5
					//2) next term starts in expression starts with a + or -
					//In all other cases, we are looking at a false positive like 2d6-1d8+1.
					//If we just look for a neg in front of the static mod 1 (we look for -1),
					//we will find it...but the next term isn't a new expression or the end of the
					//expression so it is a false neg term.
					int posOfNextTerm = pos + x.length() + 1;
					if(posOfNextTerm >= expression.length())
						temp = -temp;
					else
					{
						String nextChar = expression.substring(posOfNextTerm, posOfNextTerm+1);
						if(nextChar.equals("+") || nextChar.equals("-"))
							temp = -temp;
					}
					
					//by the time we add temp, it will have the correct sign
					staticMods.add(temp);
				}
				catch(NumberFormatException nfe)
				{
					errorInExpression = true;
					break;
				}
			}
		}
		
		//add up the sums from the added dice expressions
		for(MultiplesOfSingleDie mosd: posVariableTerms)
		{
			sum += mosd.getSum();
			if(mosd.isCrit())
				isCrit = true;
			else if(mosd.isFail())
				isFail = true;
		}
		
		//subtract the sums from the subtracted dice expressions...no crit or fail on subtracted rolls
		for(MultiplesOfSingleDie mosd: negVariableTerms)
		{
			sum -= mosd.getSum();
		}
		
		//add up the sums of the static modifiers...the value of k is either pos or neg in the list
		for(int k: staticMods)
			sum += k;
		
		
	}
	
	public boolean isCrit()
	{
		return isCrit;
	}
	
	public boolean isFail()
	{
		return isFail;
	}
	
	public int getResult()
	{
		return sum;
	}
	
	@SuppressWarnings("unused")
	public int getMaximizedResult()
	{
		int maxSum = 0;
		for(int k: staticMods)
			maxSum += k;
		for(MultiplesOfSingleDie x: posVariableTerms)
			maxSum += x.getMaximizedSum();
		for(MultiplesOfSingleDie x: negVariableTerms)
			maxSum -= 1;	//subtract 1 for the subtracted terms...this provides the greatest result??
		return maxSum;
	}
	
	public boolean errorInExpression()
	{
		return errorInExpression;
	}
	
	public String toString()
	{
		String temp = "Actual Rolls\n" + rollResultJustDiceToString() + "\nTotal: " + sum;
		
		return temp;
	}
	
	public String rollResultJustDiceToString()
	{
		String temp = "";
		for(MultiplesOfSingleDie mosd: posVariableTerms)
			temp += mosd.toString() + "\n";
		for(MultiplesOfSingleDie mosd: negVariableTerms)
			temp += "- " + mosd.toString() + "\n";
		return temp;
	}
}
