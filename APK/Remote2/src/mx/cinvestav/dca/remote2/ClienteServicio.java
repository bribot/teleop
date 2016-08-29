package mx.cinvestav.dca.remote2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;

import java.util.Scanner;

import android.graphics.Color;
import android.util.Log;



public class ClienteServicio implements Runnable, Subject{
	public static final int CONNECTED=1;
	public static final int NOT_CONNECTED=0;
	public static final int ERROR=2;
	public static final String ADIOS="BYE";
	public int iStatus=NOT_CONNECTED;
	private String txtStatus;
	private String out_MSG="Leer";
	private String in_MSG="QE";
	public boolean bStatus=false;
	private String IP=null;
	private int Port=0;
	Socket socket;
	String finall="",text;
	PrintWriter out = null;
	BufferedReader in = null;
	//Interface de listeners patron del observador
	private ArrayList<Observer> observers = new ArrayList<Observer>();
	
	public void registerObserver(Observer observer) {
        observers.add(observer);

	}
	public void removeObserver(Observer observer){
		observers.remove(observer);
	}
	public void notifyObservers(){
		
		for(Observer ob : observers){
			ob.update(this.in_MSG);
		}
		
	}
	
	public ClienteServicio(String IP2, int PORT){
		IP=IP2;
		Port=PORT;	
	}
	
	//esperamos a que el objeto este sincronizado para poder actuar con el en estado consistente
	public synchronized String getStatus(){
		return txtStatus;
		
	}
	
	
	 public synchronized void sendMsg(String msg){
	 out_MSG=msg;
	 }
	 public synchronized String receiveMsg(){
		 return in_MSG;
	 }
	public void setIP(String IP2){
		IP=IP2;
		
	}
	
	public void setPort(int PORT){
		Port=PORT;
		
	}
	
	private void connect(){
		try {
			socket = new Socket(IP, Port);
			Log.e("Connect", "Socket Creado" );
			//si nos conectamos
			
			
		} catch (Exception e) {
			//Si hubo algun error
			txtStatus="ERROR";
			Log.e("Error connect()", "" + e);
			iStatus=ERROR;
			
		}
	
	}
	
	private void disconnect() {
		try {
			//Prepramos mensaje de desconexion
			//avisamos al server que cierre el canal
			boolean val_acc = send_Msg(ADIOS);
				if (!val_acc) {//hubo un error
				iStatus=ERROR;
				txtStatus="Error_Fin";
				//Change_leds(false);
				Log.e("Disconnect() -> ", "!ERROR!");

			} else {//ok nos desconectamos
				txtStatus="Disconnected";
				Log.e("Disconnect() -> ", "!ok!");
				//cerramos socket	
				socket.close();
				bStatus=false;
				iStatus=NOT_CONNECTED;
			}
		} catch (IOException e) {
			//Hubo algun error
			e.printStackTrace();
		}

	}
	

	private boolean send_Msg(String msg) {
		try {
			//Accedemos a flujo de salida
			OutputStream oos;
			oos = socket.getOutputStream();
			if (socket.isConnected())// si la conexion continua
			{
				oos.write(msg.getBytes());
				//envio ok
				return true;

			} else {//en caso de que no halla conexion al enviar el msg
				txtStatus="Error al enviar, soccket offline";//error
				return false;
			}

		} catch (IOException e) {// hubo algun error
			Log.e("Snd_Msg() ERROR -> ", "" + e);
			txtStatus="Error al enviar";
			return false;
		}
		
	}
	
	private String read_Msg() {
		if (socket.isConnected())// si la conexion continua
			{
				
				try{//Accedemos al flujo de entrada y lo convertimos a una cadena con un scanner  
					InputStream in = socket.getInputStream();
					String result = new Scanner(in,"UTF-8").nextLine();
					
					return result;  
				} catch (IOException e) {  
				     
				    return "Error";
				} 
				

			} else {//en caso de que no halla conexion al enviar el msg
				txtStatus="ErrorRead";//error
				return "NADA";
			}

				
		
	}
	
	public void run(){
		
		try {//creamos socket con los valores anteriores
			Log.e("Connect", "Creando Socket" );
			socket = new Socket(IP, Port);
			Log.e("Connect", "Socket Creado" );
			//si nos conectamos
			
			Log.e("Run","Conectado");
			while(socket.isConnected()){
				Log.e("Run","While");
				if (socket.isConnected()){
					Log.e("Run","Conectado");
					Log.e("Run","Sync out_MSG"+out_MSG);
									if(!out_MSG.equals("Leer"))
									this.send_Msg(out_MSG);
									if(out_MSG.equals("ADIOS"))
									{try {
										socket.close();
										txtStatus ="Desconectado";
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										txtStatus ="Error Desconectado";
									}
									break;
									}	
									Thread.sleep(500);//Esperamos 500 ms 
									notifyObservers();
					
			}else{
				
				txtStatus ="Desconectado";
				
			}
		} try {
			socket.close();
			txtStatus ="Desconectado";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			txtStatus ="Error Desconectado";
		}
		
		} catch (Exception e) {
			//Si hubo algun error
			txtStatus="ERROR";
			Log.e("Error connect()", "" + e);
			iStatus=ERROR;
			
		}
		
		
}
	
}
