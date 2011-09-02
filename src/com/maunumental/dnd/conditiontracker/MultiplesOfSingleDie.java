package com.maunumental.dnd.conditiontracker;

import java.util.Random;


//this class is intended to store an expression like 4d6 or 4d6r1 or 1d20c19 with NO STATIC MODS
//it rolls the dice and reports the results in a variety of formats
//There is no way to keep rolling one of these objects...it rolls on construction
//and then you can query the object for its data. To "reroll" it, reconstruct a new one
//with the same expression.
public class MultiplesOfSingleDie 
{
	private Random random;
	@SuppressWarnings("unused")
	private String originalExpression;
	private int numberOfDice;
	private int dieType;
	private int[] numbers;
	private boolean rerollValues = false;
	private int rerollUpTo = 0;
	private boolean isCrit = false;
	private boolean isFail = false;			//a "fail" is a 1 on a d20...
	private int critsOnOrAbove = 20;
	
	
	public MultiplesOfSingleDie(String exp)
	{
		if (exp == null)
			throw new IllegalArgumentException("null parameter to constructor");
		else if (exp.indexOf("d") == -1)
			throw new IllegalArgumentException("parameter to constructor must contain a d for die type");
		originalExpression = exp;
		
		random = new Random();
		
		//get the number of dice (up to the d in 3d8), set it to 1 if it is just d8
		String tempNumb = exp.substring(0, exp.indexOf("d"));
		if(tempNumb.length() == 0)
			tempNumb = "1";
		
		exp = exp.substring(exp.indexOf("d") + 1);
		String tempType = null, tempReroll = null, tempCrit = null;
		if(exp.indexOf("r") != -1)
		{
			tempType = exp.substring(0,exp.indexOf("r"));
			tempReroll = exp.substring(exp.indexOf("r")+1);
		}
		else if (exp.indexOf("c") != -1)
		{
			tempType = exp.substring(0,exp.indexOf("c"));
			tempCrit = exp.substring(exp.indexOf("c")+1);			
		}
		else
			tempType = exp;
		
		try
		{
			numberOfDice = Integer.parseInt(tempNumb);
			dieType = Integer.parseInt(tempType);
			if(tempReroll != null)
			{
				rerollUpTo = Integer.parseInt(tempReroll);
				rerollValues = true;
			}
			if(tempCrit != null)
			{
				critsOnOrAbove = Integer.parseInt(tempCrit);
				
			}
		}
		catch(NumberFormatException nfe)
		{
			throw new IllegalArgumentException("malformed parameter String to constructor");
		}
			
		if(dieType == 20 && rerollValues)
			throw new IllegalArgumentException("you can't reroll d20s");
		if(dieType != 20 && critsOnOrAbove < 20)
			throw new IllegalArgumentException("you can only specify crits on d20s");
		
		numbers = new int[numberOfDice];
		
		roll();
	}
	
	private void roll()
	{
		//load up the array of rolled values, checking for rerolls
		for(int i = 0; i < numbers.length; i++)
		{
			int candidate = random.nextInt(dieType) + 1;
			if(candidate > rerollUpTo)
				numbers[i] = candidate;
			else
				i--;
		}
		
		if(dieType == 20)
		{
			for(int x: numbers)
			{
				if(x >= critsOnOrAbove)
					isCrit = true;
				else if (x == 1)
					isFail = true;
			}
			
		}
	}
	
	public int getSum()
	{
		int sum = 0;
		for(int x: numbers)
			sum += x;
		return sum;
	}
	
	public int getMaximizedSum()
	{
			return dieType*numberOfDice; 
	}
	
	public boolean isCrit()
	{
		return isCrit;
	}
	
	public boolean isFail()
	{
		return isFail;
	}
	
	//returns true if rerolls were possible, not if they were actually rerolled
	public boolean isRerollable()
	{
		return rerollValues;
	}
	
	public int[] getRolls()
	{
		return numbers;
	}
	
	//returns format of "d4: 2, 4, 1" for a 3d4 expression (example of format only)...no c's no r's
	public String toString()
	{
		String temp = "d" + dieType + ": ";
		for(int i = 0; i < numbers.length - 1; i++)
			temp += numbers[i] + ", ";
		if(numbers.length > 0)
			temp += numbers[numbers.length - 1];
		 if(rerollValues)
			 temp += " with rerolls";
		return temp;
	}
	
	
}
