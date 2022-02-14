Hello-Java
==========

//My first repository on GitHub

//I am Currently developing android application using Java with Eclipse IDE but i can't find solutions of printing image via bluetooth extech printer. Printing text has no problem but in IMAGE will print a lot of characters..Here's my sample code of printing barcode..Please help me to fix this problem, any help was deeply appreciated.Thank you..

package com.example.printbluetoothimage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Set;
import java.util.UUID;

import com.google.zxing.BarcodeFormat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	   // will show the statuses
    TextView myLabel;
 
    // will enable user to enter any text to be printed
    EditText myTextbox;
 
    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
 
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
 
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    
    byte[] start = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1B,  
            0x40, 0x1B, 0x33, 0x00 };  
    byte[] end = { 0x1d, 0x4c, 0x1f, 0x00 };  
    volatile boolean stopWorker;
 
    
    private int mWidth;//  
    private int mHeight;//  
    private String mStatus;//
    private BitSet dots;//
    private ByteArrayOutputStream mService;//
    
    
    private byte[] printbyte = {(byte)0x1B,(byte)0x2A,(byte)0x6F,(byte)0x63}; 
    
    ImageView image;
    
    public byte[] Packet1={
            (byte)0X8A,(byte)0XC6,(byte)0X04,(byte)0X94,(byte)0XF4,(byte)0X0B,(byte)0X5E,(byte)0X30,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X04,(byte)0X24,(byte)0X01,(byte)0X0C,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X01,(byte)0X08,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X04,(byte)0X24,(byte)0X05,(byte)0X0C,(byte)0X00,(byte)0X60,(byte)0X00,(byte)0X18,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X30,(byte)0X1E,(byte)0X10,(byte)0X60,(byte)0X00,(byte)0X18,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X70,(byte)0X3F,(byte)0X18,(byte)0XF0,(byte)0X00,(byte)0X3E,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X70,(byte)0X3C,(byte)0X39,(byte)0XF1,(byte)0X80,(byte)0X3E,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0XF8,(byte)0X7C,(byte)0X9F,(byte)0XF1,(byte)0X80,(byte)0X7F,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0XF9,(byte)0X9E,(byte)0X1C,(byte)0XFF,(byte)0XC2,(byte)0X7E,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0XF9,(byte)0X9E,(byte)0X1C,(byte)0XE7,(byte)0XE2,(byte)0X7E,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0XFB,(byte)0X1E,(byte)0X1C,(byte)0XFF,(byte)0XE7,(byte)0XBE,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X7B,(byte)0X16,(byte)0X1C,(byte)0XFF,(byte)0XDF,(byte)0X3E,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X71,(byte)0X12,(byte)0X1C,(byte)0XE7,(byte)0XF7,(byte)0X34,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X51,(byte)0X12,(byte)0X1C,(byte)0XF7,(byte)0XF7,(byte)0X24,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X49,(byte)0X12,(byte)0X1C,(byte)0XFF,(byte)0XF3,(byte)0X24,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X49,(byte)0X12,(byte)0X3F,(byte)0XFD,(byte)0XF3,(byte)0X24,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0X49,(byte)0X96,(byte)0X3F,(byte)0XFC,(byte)0XF3,(byte)0X24,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X05,(byte)0X49,(byte)0X80,(byte)0X00,(byte)0X08,(byte)0X10,(byte)0X5E,(byte)0X28,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X30,(byte)0X25,(byte)
            0X01,(byte)0X5E,(byte)0X03,(byte)0X24,(byte)0X06,(byte)0XE0,(byte)0X74,(byte)0XA9,(byte)0X33,(byte)0X23,(byte)0X26,(byte)0X5E,(byte)0X27,(byte)0X25,(byte)0X04
            }; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        try {
 
            // we are goin to have three buttons for specific functions
            Button openButton = (Button) findViewById(R.id.open);
            Button sendButton = (Button) findViewById(R.id.send);
            Button closeButton = (Button) findViewById(R.id.close);
            image = (ImageView) findViewById(R.id.imageView1);
            
            myLabel = (TextView) findViewById(R.id.label);
            myTextbox = (EditText) findViewById(R.id.entry);
            
            // open bluetooth connection
            openButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        findBT();
                        openBT();
                    } catch (IOException ex) {
                    }
                }
            });
 
            // send data typed by the user to be printed
            sendButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        sendData();
                    } catch (IOException ex) {
                    }
                }
            });
 
            // close bluetooth connection
            closeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        closeBT();
                    } catch (IOException ex) {
                    }
                }
            });
 
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // This will find a bluetooth printer device
    void findBT() {
 
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
 
            if (mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }
 
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
 
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
 
                    // MP300 is the name of the bluetooth printer device
                    if (device.getName().equals("3750THS")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            myLabel.setText("Bluetooth Device Found");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        	//UUID uuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
 
            beginListenForData();
 
            myLabel.setText("Bluetooth Opened");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
 
            // This is the ASCII code for a newline character
            final byte delimiter = 10;
 
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
 
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {
 
                        try {
 
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
 
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
 
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
 
                    }
                }
            });
 
            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData() throws IOException {     
    	try {
	    	mmOutputStream.write(("Ronel Sutabinto").getBytes());
	    	
	    	mmOutputStream.write(Packet1[0]);
	    	mmOutputStream.write(Packet1[1]);
	    	print_image("/sdcard/th.png");
	    	mmOutputStream.write(Packet1[2]);
    	} catch (Exception e) {
            Log.e("Main", "Exe ", e);
        }
        
    }
 
    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    //==========Start Print Image==========================
    
    private void print_image(String file) {
    	try{
    	 File fl = new File(file);
    	    if (fl.exists()) {
    	        Bitmap bmp = BitmapFactory.decodeFile(file);
    	        convertBitmap(bmp);
    	        
    	        mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_24);

    	        int offset = 0;
    	        while (offset < bmp.getHeight()) {
    	        	mmOutputStream.write(PrinterCommands.SELECT_BIT_IMAGE_MODE);
    	            for (int x = 0; x < bmp.getWidth(); ++x) {

    	                for (int k = 0; k < 3; ++k) {

    	                    byte slice = 0;
    	                    for (int b = 0; b < 8; ++b) {
    	                        int y = (((offset / 8) + k) * 8) + b;
    	                        int i = (y * bmp.getWidth()) + x;
    	                        boolean v = false;
    	                        if (i < dots.length()) {
    	                            v = dots.get(i);
    	                        }
    	                        slice |= (byte) ((v ? 1 : 0) << (7 - b));
    	                    }
    	                    mService.write(slice);
    	                }
    	            }
    	            offset += 24;
    	            mmOutputStream.write(PrinterCommands.FEED_LINE);
    	            mmOutputStream.write(PrinterCommands.FEED_LINE);          
    	            mmOutputStream.write(PrinterCommands.FEED_LINE);
    	            mmOutputStream.write(PrinterCommands.FEED_LINE);
    	            mmOutputStream.write(PrinterCommands.FEED_LINE);
    	            mmOutputStream.write(PrinterCommands.FEED_LINE);
    	        }
    	        mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_30);


    	    } else {
    	        Toast.makeText(this, "file doesn't exists", Toast.LENGTH_SHORT)
    	                .show();
    	    }
	    } catch (NullPointerException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }
    
    public String convertBitmap(Bitmap inputBitmap) {

        mWidth = inputBitmap.getWidth();
        mHeight = inputBitmap.getHeight();

        convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
        mStatus = "ok";
        return mStatus;

    }

    private void convertArgbToGrayscale(Bitmap bmpOriginal, int width,
            int height) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        dots = new BitSet();
        try {

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    // get one pixel color
                    pixel = bmpOriginal.getPixel(y, x);

                    // retrieve color of all channels
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);
                   
                    // take conversion up to one single value by calculating
                    // pixel intensity.
                    R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                    // set bit into bitset, by calculating the pixel's luma
                    if (R < 55) {                       
                        dots.set(k);//this is the bitset that i'm printing
                    }
                    k++;

                }


            }


        } catch (Exception e) {
            // TODO: handle exception
            Log.e("ronel", e.toString());
        }
    }
    
    //Part 2 hexa convertion
    //================================
    //================================
    
    public static byte[] hexToBuffer(String hexString)
    	    throws NumberFormatException {
    	    int length = hexString.length();
    	    byte[] buffer = new byte[(length + 1) / 2];
    	    boolean evenByte = true;
    	    byte nextByte = 0;
    	    int bufferOffset = 0;

    	    if ((length % 2) == 1) {
    	        evenByte = false;
    	    }

    	    for (int i = 0; i < length; i++) {
    	        char c = hexString.charAt(i);
    	        int nibble; // A "nibble" is 4 bits: a decimal 0..15

    	        if ((c >= '0') && (c <= '9')) {
    	            nibble = c - '0';
    	        } else if ((c >= 'A') && (c <= 'F')) {
    	            nibble = c - 'A' + 0x0A;
    	        } else if ((c >= 'a') && (c <= 'f')) {
    	            nibble = c - 'a' + 0x0A;
    	        } else {
    	            throw new NumberFormatException("Invalid hex digit '" + c +
    	                "'.");
    	        }

    	        if (evenByte) {
    	            nextByte = (byte) (nibble << 4);
    	        } else {
    	            nextByte += (byte) nibble;
    	            buffer[bufferOffset++] = nextByte;
    	        }

    	        evenByte = !evenByte;
    	    }

    	    return buffer;
    	}
    //==========End Print Image============================
}
