package org.menacheri.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class which can be used to read a small file into a string.
 * 
 * @author Abraham Menacherry
 * 
 */
public class SmallFileReader
{
	public static String readSmallFile(String filePath)
			throws IOException
	{
		if(filePath == null)
		{
			return null;
		}
		FileReader reader = new FileReader(new File(filePath));
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line = bufferedReader.readLine()) != null)
		{
			buf.append(line);
		}
		bufferedReader.close();
		reader.close();
		return buf.toString();
	}
}