package btd.util;

public class FieldUtil {

	public static boolean isDouble(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public static boolean isInteger(String str)  
	{  
	  try  
	  {  
	    double d = Integer.parseInt(str);
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public static boolean isIntBetween(int min, int check, int max) {
		if (check<min) return false;
		else if (check>max) return false;
		else return true;
	}
}
