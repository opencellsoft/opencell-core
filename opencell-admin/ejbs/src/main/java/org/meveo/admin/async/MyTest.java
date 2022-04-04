package org.meveo.admin.async;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTest { 
	    public static void main(String[]args) 
	    { 
	        Matcher matcher = Pattern.compile("\\d+").matcher("str54778str917str78001str");
	    
	        while(matcher.find()) 
	        {
	            System.out.println(matcher.group());
	        }
	    }

	}

