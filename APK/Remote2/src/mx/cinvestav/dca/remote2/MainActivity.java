package mx.cinvestav.dca.remote2;





import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.os.Build;

/*Interfaces patron del observador*/

interface Observer {
    public void update(String msg);
}

interface Subject {
    public void registerObserver(Observer observer);

    public void removeObserver(Observer observer);

    public void notifyObservers();
}


public class MainActivity extends Activity {

	private TextView txt1, txt2;
	private float xl_axis,xr_axis;
	float yl_axis,yr_axis;
	private int pointerid;
	private JoystickView joystickLeft, joystickRight;
	VideoView video;
	String IP="192.168.0.105", IPF;
	int PortF=10001;
	Activity yo;
	boolean init=false;
	
	String msj;

	public boolean dataReady = false;
	
	private Button btnConnect, btnDisconnect, btnVideo, btnStop;
	
	ClienteServicio Cliente=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		yo=this;//Referencia a la main
		//Asignamos las referencias a los objetos de la GUI
		txt1 = (TextView) findViewById(R.id.ipText);
		txt2 = (TextView) findViewById(R.id.portText);
		
		btnConnect = (Button) findViewById(R.id.connectBtn);
		btnDisconnect = (Button) findViewById(R.id.disconnectBtn);
		btnStop = (Button) findViewById(R.id.stopBtn);
		btnVideo = (Button) findViewById(R.id.videoBtn);
		
		joystickLeft = (JoystickView) findViewById(R.id.JoystickViewLeft);
		joystickRight = (JoystickView) findViewById(R.id.joystickViewRight);
		joystickLeft.setYAxisInverted(false);
		joystickRight.setYAxisInverted(false);
		video = (VideoView) findViewById(R.id.videoView1);
		//Colocamos una IP y un Puerto por defecto
		txt1.setText("192.168.0.102");
		txt2.setText("10010");
		//Activamos los listeners necesarios
		joystickLeft.setOnJostickMovedListener(new JoystickMovedListener() {
			@Override
			public void OnMoved(int pan, int tilt) {
				
				xl_axis =  (float) joystickLeft.getUserX();
				yl_axis =  (float) joystickLeft.getUserY();
				
				txt1.setText((xl_axis)+","+(yl_axis));
				Cliente.sendMsg(txt1.getText().toString());
			}

			@Override
			public void OnReleased() {
				xl_axis= (float) joystickLeft.getX();
				yl_axis= (float) joystickLeft.getY();
				
				txt1.setText((xl_axis)+","+(yl_axis));
				
			}

			@Override
			public void OnReturnedToCenter() {
				pointerid = joystickLeft.getPointerId();
				xl_axis =  (float) joystickLeft.getUserX();
				yl_axis =  (float) joystickLeft.getUserY();
				
				txt1.setText((xl_axis)+","+(yl_axis));
				Cliente.sendMsg(txt1.getText().toString());//Pedimos al hilo de comunicacion que envie el msg
			}
		});
		//El joystick derecho solo se usa con fines ilustrativos y no envia comunicacion
		joystickRight.setOnJostickMovedListener(new JoystickMovedListener() {
			@Override
			public void OnMoved(int pan, int tilt) {
				xr_axis = (int) joystickRight.getUserX();
				yr_axis = (int) joystickRight.getUserY();
				txt2.setText("XR="+(xr_axis+10)+"YR="+(yr_axis+10));
				//Cliente.sendMsg(txt2.getText().toString()); 
			}

			@Override
			public void OnReleased() {
				xr_axis=(int) joystickRight.getX();
				yr_axis=(int) joystickRight.getY();
				txt2.setText("XR="+xr_axis+"YR="+yr_axis);
				//Cliente.sendMsg(txt2.getText().toString());
			}

			@Override
			public void OnReturnedToCenter() {
				pointerid = joystickRight.getPointerId();
				xr_axis = (int) joystickRight.getUserX();
				yr_axis = (int) joystickRight.getUserY();
				txt2.setText("XR="+xr_axis+"YR="+yr_axis);
				//Cliente.sendMsg(txt2.getText().toString());
			}
		});
		
		btnVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				video.setVideoPath("http://192.168.0.105:8080/?action=stream");
				video.requestFocus();
				video.start();
				
			}
		});
		
		video.setOnCompletionListener(new OnCompletionListener () {
		    @Override
		    public void onCompletion(MediaPlayer mp) {
		        Log.e("Listener", "OnCompletion");
		        mp.reset();
		       
		        video.setVideoPath("http://192.168.0.105:8080/?action=stream");
		        video.start();
		       
		        
		    }
		});
		//Acciones de los botones
		btnConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!init){
				String IP2 = txt1.getText().toString();
				int PORT = Integer.valueOf(txt2.getText().toString());
				IPF=IP2;
				PortF=PORT;
				init=true;
				}
				Log.e("Conect","Antes");
				if(Cliente==null){
				Cliente = new ClienteServicio(IPF,PortF);
				new Thread(Cliente).start();
				
				}
			else{txt2.setTextColor(Color.RED);
				txt2.setText("Error de Conexion");}
			}
		});
		
		btnDisconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Cliente.sendMsg("ADIOS");
				Cliente=null;
			}
			
		});
				
		
		
	}
	//Crea el menu de opciones para usarse en algun futuro.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onPause(){
		
		if(Cliente!=null)
		{
		try {
			Cliente.wait();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		}
		super.onPause();
	}
	
	public void update(String msg) {
		
		this.msj=(msg);
		this.dataReady=true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
