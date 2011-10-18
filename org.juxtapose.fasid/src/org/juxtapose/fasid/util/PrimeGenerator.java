package org.juxtapose.fasid.util;

public class PrimeGenerator
{
	public static void main( String... inArgs )
	{
		int primes = 2000;
		int primeIndex = 1;
		
		for( int i = 2; ; i++ )
		{
			if( isPrime( i ) )
			{
				System.out.println( "public static final int PRIME_"+primeIndex+" = "+i+";" );
				primeIndex++;
				
				if( primeIndex > primes )
				{
					break;
				}
			}
		}
	}
	
	public static boolean isPrime( int inNum )
	{
		for( int i = 2; i < inNum; i++ ){
			int n = inNum%i;
			if (n==0)
			{
				return false;
			}
		}
		return true;
	}
}
