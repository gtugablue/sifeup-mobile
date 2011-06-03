package pt.up.fe.mobile.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class FriendsData {
	
	final String TAG="FriendsData";
	final String filename="friends.lst";
	final int bufSize=1024;
	
	ArrayList<Friend> list;
	int selectedFriend=-1;
	boolean loaded=false;
	
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public int getSelectedFriend() {
		return selectedFriend;
	}

	public void setSelectedFriend(int selectedFriend) {
		this.selectedFriend = selectedFriend;
	}

	public FriendsData() 
	{
		list=new ArrayList<Friend>();
	}
	
	public boolean loadFromFile(Context con)
	{
		byte[] buf=new byte[bufSize];
		try 
		{
			FileInputStream fis = con.openFileInput(filename);
			while(fis.read(buf)>0)
			{
				String friendStr=new String(buf);
				String[] strArr=friendStr.split(",");
				if(strArr.length!=3)
				{
					Log.e(TAG, "string parsed incorrectly from file");
				}
				Friend fr=new Friend(strArr[0], strArr[1], strArr[2]);
				list.add(fr);
			}
			Log.i(TAG, "friends loaded from file successfully");
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			Log.e(TAG, "Error opening friends file in load");
			saveToFile(con);
			return false;
		} 
		catch (IOException e) 
		{
			Log.e(TAG, "Error reading friends from file in load");	
			return false;
		}	
	}
	
	public ArrayList<Friend> getList() {
		return list;
	}

	public void setList(ArrayList<Friend> list) {
		this.list = list;
	}

	public boolean addFriend(Friend fr)
	{
		list.add(fr);
		return true;
	}
	
	public boolean saveToFile(Context con)
	{
		try 
		{
			FileOutputStream fos = con.openFileOutput(filename, Context.MODE_PRIVATE);
			for(Friend f: list)
			{
				String toWrite=f.getCode()+","+f.getName()+","+f.getCourse()+",";
				byte[] buf=new byte[bufSize];
				byte[] buf2=toWrite.getBytes();
				for(int i=0; i<bufSize; i++)
				{
					if(i<buf2.length)
						buf[i]=buf2[i];
					else
						buf[i]=0;
				}
				Log.i(TAG,"buf: "+new String(buf));
				fos.write(buf);
			}
			fos.close();
			Log.i(TAG, "Saved successfully");
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			Log.e(TAG, "Error opening friends file in save");	
		} 
		catch (IOException e) 
		{
			Log.e(TAG, "Error writing to friends file in save");	
		}
		return false;
	}
}
