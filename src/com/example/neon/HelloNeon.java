package com.example.neon;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class HelloNeon extends Activity
{
	short dtw_align( short[] av, short[] bv, short large_value ) {
		short[] mat = new short[bv.length + 1];
		Arrays.fill( mat, large_value );
	    	    
	    mat[0] = 0;
	    
//	     std:: ofstream os( "/tmp/yyy.txt" );
	    short last_s = -1;
	    for( int ai = 0; ai < av.length; ai++ ) {
	        final short a = av[ai];
	        last_s = large_value;

	        // for the first row, the diagonal value for the first colum is zero (inialization above)
	        short diag = mat[0];
	        // for the remaining rows it will be infinity. note: mat[0] is never written to in the b-loop
	        mat[0] = large_value;
	        
	  
//	        value_t *upper = mat(1);
	        
	        for( int bi = 0; bi < bv.length; bi++ ) {
	            final short b = bv[bi];
	            
	            final int upper_ptr = bi+1;
	            
	            final short sd = diag;
	            final short su = mat[upper_ptr];
	            final short sl = last_s;
	            
	            short cost = (short)Math.abs( (int)(a - b) );
	            
	            //std::cout << "cost: " << cost << "\n";
	            
	            diag = su;
	            
	            final short min = (short) Math.min( sd, Math.min(su, sl) );
	            
//	            if( int(min) + cost > large_value ) {
//	                std::cout << "overflow\n";
//	                cost = large_value - min;
//	            } 
	            
	            mat[upper_ptr] = last_s = (short) (cost + min);
	            
//	             os << "x " << last_s << "\n";
	
	        }
	        
	    }
	    
	    //return mat[mat.size() - 1];
	    return last_s;
	}
	float dtw_align_float( float[] av, float[] bv, float large_value ) {
		float[] mat = new float[bv.length + 1];
		Arrays.fill( mat, large_value );
	    	    
	    mat[0] = 0;
	    
//	     std:: ofstream os( "/tmp/yyy.txt" );
	    float last_s = -1;
	    for( int ai = 0; ai < av.length; ai++ ) {
	        final float a = av[ai];
	        last_s = large_value;

	        // for the first row, the diagonal value for the first colum is zero (inialization above)
	        float diag = mat[0];
	        // for the remaining rows it will be infinity. note: mat[0] is never written to in the b-loop
	        mat[0] = large_value;
	        
	  
//	        value_t *upper = mat(1);
	        
	        for( int bi = 0; bi < bv.length; bi++ ) {
	            final float b = bv[bi];
	            
	            final int upper_ptr = bi+1;
	            
	            final float sd = diag;
	            final float su = mat[upper_ptr];
	            final float sl = last_s;
	            
	            float cost = (float)Math.abs( (int)(a - b) );
	            
	            //std::cout << "cost: " << cost << "\n";
	            
	            diag = su;
	            
	            final float min = (float) Math.min( sd, Math.min(su, sl) );
	            
//	            if( int(min) + cost > large_value ) {
//	                std::cout << "overflow\n";
//	                cost = large_value - min;
//	            } 
	            
	            mat[upper_ptr] = last_s = (float) (cost + min);
	            
//	             os << "x " << last_s << "\n";
	
	        }
	        
	    }
	    
	    //return mat[mat.size() - 1];
	    return last_s;
	}
	String dtw() {
		short[] a = new short[256];
		short[] b = new short[256];
		
		
		for( int i = 0; i < a.length; i++ ) {
			if( i % 32 < 16 ) {
				a[i] = -127;
				
			} else {
				a[i] = 127;
			}
			b[i] = (short)(Math.sin( (i / (float)(a.length)) * 32 * 3.14159) * 128);
		
		}
		long t1 = System.currentTimeMillis();
		int resc = 0;
		short res = 0;
		
		long ncup = 0;
		for( int i = 0; i < 1000; i++ ) {
			res = dtw_align(a, b, (short) 0x7fff );
			resc += res;
			ncup += a.length * b.length;
		}
		double dt = (System.currentTimeMillis() - t1) / 1000.0;
		return "java: " + res + " " + (ncup / (dt * 1e9)) + "\n";
		
	}
	String dtw_float() {
		float[] a = new float[256];
		float[] b = new float[256];
		
		
		for( int i = 0; i < a.length; i++ ) {
			if( i % 32 < 16 ) {
				a[i] = -127;
				
			} else {
				a[i] = 127;
			}
			b[i] = (float)(Math.sin( (i / (float)(a.length)) * 32 * 3.14159) * 128);
		
		}
		long t1 = System.currentTimeMillis();
		int resc = 0;
		float res = 0;
		
		long ncup = 0;
		for( int i = 0; i < 1000; i++ ) {
			res = dtw_align_float(a, b, (float) 1e8 );
			resc += res;
			ncup += a.length * b.length;
		}
		double dt = (System.currentTimeMillis() - t1) / 1000.0;
		return "java(float): " + res + " " + (ncup / (dt * 1e9)) + "\n";
		
	}
	
	void bench() {
		
        m_tv.setText( stringFromJNI() );
        m_tv.append(dtw());
        m_tv.append(dtw_float());
        
	
	}
	TextView  m_tv;
	SensorManager m_sensorManager;
	
	class MyListener implements SensorEventListener {
		float[] burst = new float[32];
		int burst_ptr = 0;
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		boolean sock_bad = false;
		Socket csock = null;
		
		PrintStream sock_os = null;
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			if( csock == null && !sock_bad) {
				try {
					csock = new Socket("192.168.178.35", 1234 );
		
					sock_os = new PrintStream(csock.getOutputStream());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					m_tv.setText(e.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					m_tv.setText(e.toString());
				}
				
				if( csock == null ) {
					sock_bad = true;
				}
				
			}
			if( sock_bad ) {
				return;
			
			}
			
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				
				float abs_accel = 0;
				for( int i = 0; i < 3; i++ ) {
					float a = Math.abs( event.values[i] );
					abs_accel += a * a;
					
				}
				abs_accel = (float) Math.sqrt(abs_accel);
				burst[burst_ptr] = abs_accel;
				sock_os.println(abs_accel);
				
				burst_ptr++;
				m_tv.setText( burst_ptr + "\n");
				if( burst_ptr == burst.length ) {
					m_tv.setText( "acc: " + abs_accel + "    " + event.values[0] + " " + event.values[1] + " " + event.values[2] + "\n"  );
					burst_ptr = 0;
				}
				
			}
		}
		
	
	}
	private MyListener m_sensListener = new MyListener();
		
	
    /** Called when the activity is firt created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        m_tv = new TextView(this);
        m_tv.setLines( 10 );
        m_tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        setContentView(m_tv);
        m_tv.append( "Hello!\n");
        
        
        m_sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
    }
	
    @Override
    public void onResume() {
    	super.onResume();
    	bench();
//    	m_sensorManager.registerListener(m_sensListener,
//                m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	
    	m_sensorManager.unregisterListener(m_sensListener);
    	super.onStop();
    }
    /* A native method that is implemented by the
     * 'helloneon' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();

    /* this is used to load the 'helloneon' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.neon/lib/libhelloneon.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("helloneon");
    }
}
