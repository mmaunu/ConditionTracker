package com.maunumental.dnd.conditiontracker;

import java.io.Serializable;
import java.util.ArrayList;


public class CharacterDnD4Ed implements Serializable
{
	
	private static final long serialVersionUID = 5355710109187689174L;
	
	//the maxHitPoints, maxSurges and maxPowerPoints fields are immutable
	private int maxHitPoints, currentHitPoints, tempHitPoints;
	private int maxSurges, currentSurges, surgeValue;
	private int actionPoints;
	private String actionPointReminder;
	private int maxPowerPoints, currentPowerPoints;
	private ArrayList<Condition> conditions;
	private String name = "default";
	
	private ArrayList<String> diceExpressions;
	private ArrayList<String> diceExpressionNames;
	
	private ArrayList<Note> notes;
	
	
	
	public CharacterDnD4Ed(String name, int maxHP, int currHP, int tempHP, int maxSurges, int currSurges,
			int surgeValue, int actPts, int maxPwrPts, int currPwrPts, String apReminder,
			ArrayList<Condition> conds,ArrayList<String> diceExps, 
			ArrayList<String> diceExpNames, ArrayList<Note> notes)
	{
		this.name = name;
		maxHitPoints = maxHP;
		this.maxSurges = maxSurges;
		this.surgeValue = surgeValue;
		currentSurges = currSurges;
		currentHitPoints = currHP;
		tempHitPoints = tempHP;
		actionPoints = actPts;
		maxPowerPoints = maxPwrPts;
		currentPowerPoints = currPwrPts;
		conditions = conds;
		if(conditions == null)
			conditions = new ArrayList<Condition>();
		name = "default";
		diceExpressions = diceExps;
		diceExpressionNames = diceExpNames;
		configDiceExpressionsAndNames();
		actionPointReminder = apReminder;
		this.notes = notes;
		if(this.notes == null)
			this.notes = new ArrayList<Note>();
	}
	
	//config so that there are 10 expressions and 10 names...even if they are blanks
	private void configDiceExpressionsAndNames()
	{
		if(diceExpressions == null)
			diceExpressions = new ArrayList<String>();
		if(diceExpressionNames == null)
			diceExpressionNames = new ArrayList<String>();
		if(diceExpressions.size() < 10)
		{
			for(int k = diceExpressions.size(); k < 10; k++)
				diceExpressions.add("");
		}
		if(diceExpressionNames.size() < 10)
		{
			for(int k = diceExpressionNames.size(); k < 10; k++)
				diceExpressionNames.add("");
		}
	}
	
	public void takeExtendedRest()
	{
		tempHitPoints = 0;
		currentSurges = maxSurges;
		currentHitPoints = maxHitPoints;
		actionPoints = 1;
		currentPowerPoints = maxPowerPoints;
		conditions = new ArrayList<Condition>();
	}
	
	public void loadConditions(ArrayList<Condition> list)
	{
		for(Condition x: list)
			conditions.add(x);
	}
	
	public void addCondition(Condition condy){
		conditions.add(condy);
	}
	public boolean removeCondition(Condition condy)
	{
		return conditions.remove(condy);
	}
	public ArrayList<Condition> getConditions(){
		return conditions;		
	}
	public ArrayList<String> getDiceExpressions(){
		return diceExpressions;
	}
	public ArrayList<String> getDiceExpressionNames(){
		return diceExpressionNames;
	}
	
	
	
	//edit a specific dice expression...by default, all expressions are "" blanks and we
	//replace blanks with actuals...so using the set method to replace
	public void addDiceExpression(int pos, String exp){
		if(pos <= diceExpressions.size() && pos >= 0)
			diceExpressions.set(pos, exp);
	}
	public void addDiceExpressionName(int pos, String name){
		if(pos <= diceExpressionNames.size() && pos >= 0)
			diceExpressionNames.set(pos, name);
	}
	
	public ArrayList<Note> getNotes(){
		return notes;
	}
	public void addNote(Note n){
		notes.add(n);
	}
	public void replaceNote(int pos, Note n){
		if(pos < notes.size() && pos >= 0)
			notes.set(pos,n);
	}
	public void removeNote(Note n){
		notes.remove(n);
	}
	public String getName()
	{
		return name;
	}
	
	/*Names are unique...once constructed, you can't change the name!
	 * public void setName(String n)
	{
		name = n;
	}*/
	public String getActionPointReminder(){
		return actionPointReminder;
	}
	public void setActionPointReminder(String x){
		actionPointReminder = x;
	}
	public int getMaxPowerPoints(){
		return maxPowerPoints;
	}
	public int getCurrentPowerPoints(){
		return currentPowerPoints;
	}
	public void decreasePowerPoints(int x){
		if(currentPowerPoints >= x)
			currentPowerPoints -= x;
		else
			currentPowerPoints = 0;
	}
	public void increasePowerPoints(int x){
		if(currentPowerPoints + x <= maxPowerPoints)
			currentPowerPoints += x;
		else
			currentPowerPoints = maxPowerPoints;
	}
	public int getActionPoints(){
		return actionPoints;
	}
	public void decreaseActionPoints(){
		if(actionPoints > 0)
			actionPoints--;
	}
	public void increaseActionPoints(){
		actionPoints++;
	}
	public int getMaxHitPoints(){
		return maxHitPoints;
	}
	public void takeDamage(int x){
		if(x <= 0)
			return;
		if(tempHitPoints <= 0)
			currentHitPoints -= x;
		else
		{
			if(tempHitPoints >= x)
				tempHitPoints -= x;
			else
			{
				currentHitPoints = currentHitPoints - x + tempHitPoints;
				tempHitPoints = 0;
			}
		}
	}
	public void heal(int x){
		if(currentHitPoints < 0 && x > 0)
			currentHitPoints = x;
		else if (x > 0)
			currentHitPoints += x;
		
		if(currentHitPoints > maxHitPoints)
			currentHitPoints = maxHitPoints;
	}
	public int getCurrentHitPoints(){
		return currentHitPoints;
	}
	
	//the idea...thp don't stack, so only set the field if the param is greater
	public void setTempHitPoints(int x){
		if( x > tempHitPoints) 
			tempHitPoints = x;
	}
	
	public int getTempHitPoints(){
		return tempHitPoints;
	}
	public int getMaxSurges() {
		return maxSurges;
	}
	public int getCurrentSurges(){ 
		return currentSurges;
	}
	public void loseSurge() {
		if(currentSurges > 0)
			currentSurges--;
	}
	public void gainSurge() {
		if(currentSurges < maxSurges)
			currentSurges++;
	}
	public int getSurgeValue(){
		return surgeValue;
	}
	public void setSurgeValue(int x){
		if(x > 0)
			surgeValue = x;
	}
	
	public String toString()
	{
		String fred = "";
		fred += currentHitPoints + "/" + maxHitPoints + " hp and " + 
				conditions.size() + " conditions";
		return fred;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof CharacterDnD4Ed))
			return false;
		
		return this.name.equals(((CharacterDnD4Ed)o).name);
	}
}
