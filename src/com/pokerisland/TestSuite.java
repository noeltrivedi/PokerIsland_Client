package com.pokerisland;


public class TestSuite
{
 
	public static void main(String[] args)
	{
		createClientServer();
		//createSeveralClients();
	}
	
	public static void outputMaxInt()
	{
		System.out.println("Integer Max Value: " + Integer.MAX_VALUE);
	}
	
	public static void createClientServer()
	{
		new Client();
	}
	
	public static void createSeveralClients()
	{
		int numClients = 4;
		for(int i = 0; i < numClients; i++)
		{
			new Client();
		}
	}

}
