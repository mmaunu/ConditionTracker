package com.maunumental.dnd.conditiontracker;

import java.io.Serializable;

public class Note implements Serializable
{
	private static final long serialVersionUID = 9129599689051795739L;
	
	private String title, body;
	
	public Note(String title, String body)
	{
		this.title = title;
		this.body = body;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getBody()
	{
		return body;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof Note))
			return false;
			
		return this.title.equals(((Note)o).title);
	}
	
	
	
	//toString and makeNote go together
	public String toString()
	{
		return title + ":END_OF_TITLE:" + body;
	}
	
	
	
	public static Note makeNote( String s )
	{
		String[] parts = s.split(":END_OF_TITLE:");
		if(parts == null)
			return new Note("","");
		String t = parts[0];
		String b = null;
		if(parts.length >= 2)
			b = parts[1];
		else
			b = "";
		
		return new Note(t,b);
	}
}
