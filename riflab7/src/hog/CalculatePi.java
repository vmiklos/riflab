package hog;

import java.util.Random;
import java.util.Scanner;

public class CalculatePi {
	public static boolean isInside (double xPos, double yPos)    
	{   
		boolean result;   
		double distance = Math.sqrt((xPos * xPos) + (yPos * yPos)); 
		if (distance < 1)
			result = true; 
		return(distance < 1); 
	}

	public static double computePI ( int numThrows )
	{  
		Random randomGen = new Random (System.currentTimeMillis() );        
		double xPos = (randomGen.nextDouble()) * 2 - 1.0;
		double yPos = (randomGen.nextDouble()) * 2 - 1.0;
		boolean isInside = isInside(xPos, yPos);
		int hits = 0;
		double PI = 0;         

		for (int i = 0; i <= numThrows; i++)
		{        
			if (isInside) 
			{
				hits = hits + 1;
				PI = 4 * (hits/numThrows);
			}
		}       
		return PI; 
	}
	
	public static void main (String[] args)
	{ 
		Scanner reader = new Scanner (System.in);
		System.out.println("This program calculates PI using the Monte Carlo method.");
		System.out.print("Please enter number of throws: ");
		int numThrows = reader.nextInt();
		double Difference = computePI(numThrows) - Math.PI;
		System.out.println ("Number of throws = " + numThrows + ", Computed PI = " + computePI(numThrows) + ", Difference = " + Difference );        
	}
}
