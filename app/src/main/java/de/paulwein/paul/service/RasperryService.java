package de.paulwein.paul.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RasperryService extends Service {
	
	private Socket mClientSocket;
	private BufferedReader mInputStream;
	private PrintWriter mOutputStream;
	private RasperryBinder mBinder = new RasperryBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public void connect(){
		try {
			mClientSocket = new Socket("192.168.130.172",9999);
			OutputStream out = mClientSocket.getOutputStream();
			mOutputStream = new PrintWriter(out);
			InputStreamReader in = new InputStreamReader(mClientSocket.getInputStream());
			mInputStream = new BufferedReader(in);
		} catch (UnknownHostException e) {
	        e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean sendMessage(String message){
		if(mClientSocket != null){
			if(mOutputStream != null){
				mOutputStream.println(message);
//				try {
//					String result = mInputStream.readLine();
//					Log.e("TAG",result);
//					if(result.equals("ok"))
//						return true;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
		}
		return false;
	}
	
	public void close(){
		if(mOutputStream != null)
			mOutputStream.close();
		if(mInputStream != null)
			try{
				mInputStream.close();
			} catch(IOException e){ e.printStackTrace();}
		if(mClientSocket != null)
			try{
				mClientSocket.close();
			} catch(IOException e){ e.printStackTrace();}
	}
	
	public class RasperryBinder extends Binder{
		public RasperryService getService(){
			return RasperryService.this;
		}
	}
	
	

}
