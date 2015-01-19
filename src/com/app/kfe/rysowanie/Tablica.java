package com.app.kfe.rysowanie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera.Size;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import com.app.kfe.R;
import com.app.kfe.dialogs.EndGameDialog;
import com.app.kfe.dialogs.EndGameDialog.EndGameDialogActionsHandler;
import com.app.kfe.wifi.DeviceDetailFragment;
import com.app.kfe.wifi.DeviceDetailFragment.ForClientServerAsyncTask;
import com.app.kfe.wifi.DeviceDetailFragment.TextServerAsyncTask;
import com.app.kfe.wifi.DeviceListFragment;
import com.app.kfe.wifi.DeviceListFragment.DeviceActionListener;
import com.app.kfe.wifi.WiFiDirectActivity;
import com.app.kfe.wifi.WiFiDirectBroadcastReceiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.FormatterClosedException;
import java.util.UUID;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Gracz;
import sqlite.model.Rozgrywka;


public class Tablica extends Activity implements OnSeekBarChangeListener, OnClickListener, ChannelListener, DeviceActionListener, EndGameDialogActionsHandler {

    private PaintView paintView;
    private Button yellowButton;
    private Button greenButton;
    private Button blueButton;
    private Button redButton;
    private Button whiteButton;
    private Button blackButton;
    private Paint drawPaint;
    private Paint canvasPaint;
    private ImageButton saveButton;
    private ImageButton brushTool;
    private ImageButton lineTool;
    private ImageButton rectangleTool;
    private ImageButton squareTool;
    private ImageButton circleTool;
    private ImageButton triangleTool;
    private ImageButton eraserTool;
    private ImageButton newImageTool;
    private SlidingDrawer toolsPanel;
    private AlertDialog.Builder saveDialog;
    private AlertDialog.Builder newImageDialog;
    private int brushColor;
    public static Tablica tablica = null;
    public static Channel channel2;
    public static BroadcastReceiver receiver2 = null;
    public static boolean ifGiveUp = false;
    private RelativeLayout answerPanel;
    private Button confirmAnwer;
    private Button drawerGiveUp;
    private EditText answer;
    public CountDownTimer cdown;
    
    private RelativeLayout forDrawerPanel;
    private Button respondentGiveUp;
    private TextView word;
    private TextView timer_s;
    private TextView timer_c;

    public static Game gra = new Game();
    public static Activity activity;
    Builder giveUpDialog;
    public static boolean isGame = false;
    public static AsyncTask<Void, Void, String> server_task;
    public static AsyncTask<Void, Void, String> client_task;
    public static int licznik=0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tablica);
        activity = this;
        tablica = this;
//----------------------Rozgrywka---------------------------		
        Gamer gracz_1 = new Gamer();
        Gamer gracz_2 = new Gamer();
        licznik=0;
        forDrawerPanel = (RelativeLayout) findViewById(R.id.drawerRelativeLayout);
        answerPanel = (RelativeLayout) findViewById(R.id.answerRelativeLayout);
        confirmAnwer = (Button) findViewById(R.id.confirmAnswer);
        respondentGiveUp = (Button) findViewById(R.id.respondentGiveUp);
        answer = (EditText) findViewById(R.id.answer);
        drawerGiveUp = (Button) findViewById(R.id.drawerGiveUp);
        word = (TextView) findViewById(R.id.word);
        timer_s = (TextView) findViewById(R.id.Timer);
        timer_c = (TextView) findViewById(R.id.Timer_c);
        forDrawerPanel.setVisibility(View.GONE);
        answerPanel.setVisibility(View.GONE);
        confirmAnwer.setVisibility(View.GONE);
        respondentGiveUp.setVisibility(View.GONE);
        answer.setVisibility(View.GONE);
        word.setVisibility(View.GONE);
        timer_s.setVisibility(View.GONE);
        timer_c.setVisibility(View.GONE);
        giveUpDialog  = new AlertDialog.Builder(this);
//----------------------Rozgrywka---------------------------		

        toolsPanel = (SlidingDrawer) findViewById(R.id.toolsPanel);
        final ImageButton handle = (ImageButton) findViewById(R.id.handle);

        paintView = (PaintView) findViewById(R.id.drawing);
        drawPaint = paintView.getDrawPaint();
        canvasPaint = paintView.getCanvasPaint();
        redButton = (Button) findViewById(R.id.redButton);
        yellowButton = (Button) findViewById(R.id.yellowButton);
        greenButton = (Button) findViewById(R.id.greenButton);
        blueButton = (Button) findViewById(R.id.blueButton);
        whiteButton = (Button) findViewById(R.id.whiteButton);
        blackButton = (Button) findViewById(R.id.blackButton);
        saveButton = (ImageButton) findViewById(R.id.saveButton);
        brushTool = (ImageButton) findViewById(R.id.brushTool);
        brushColor = drawPaint.getColor();
        lineTool = (ImageButton) findViewById(R.id.lineTool);
        rectangleTool = (ImageButton) findViewById(R.id.rectangleTool);
        squareTool = (ImageButton) findViewById(R.id.squareTool);
        circleTool = (ImageButton) findViewById(R.id.circleTool);
        triangleTool = (ImageButton) findViewById(R.id.triangleTool);
        eraserTool = (ImageButton) findViewById(R.id.eraserTool);
        newImageTool = (ImageButton) findViewById(R.id.newImageTool);

        redButton.setOnClickListener(this);
        yellowButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);
        whiteButton.setOnClickListener(this);
        blackButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        brushTool.setOnClickListener(this);
        lineTool.setOnClickListener(this);
        rectangleTool.setOnClickListener(this);
        squareTool.setOnClickListener(this);
        circleTool.setOnClickListener(this);
        triangleTool.setOnClickListener(this);
        eraserTool.setOnClickListener(this);
        newImageTool.setOnClickListener(this);

        //-------------------------------------------------- rozgrywka ----------------------------
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("isGame")) {
            isGame = getIntent().getExtras().getBoolean("isGame");
            PaintView.czyOdbierac=true;
            PaintView.czyPrzesylac=true;
            forDrawerPanel.setVisibility(View.VISIBLE);
            answerPanel.setVisibility(View.VISIBLE);
            confirmAnwer.setVisibility(View.VISIBLE);
            respondentGiveUp.setVisibility(View.VISIBLE);
            answer.setVisibility(View.VISIBLE);
            word.setVisibility(View.VISIBLE);
            WiFiDirectActivity.co_to = "cos";
            
       
            gra.lista_graczy.add(gracz_1);
            gra.lista_graczy.add(gracz_2);

            answerPanel.setVisibility(View.VISIBLE);
            confirmAnwer.setOnClickListener(this);
            drawerGiveUp.setOnClickListener(this);

            forDrawerPanel.setVisibility(View.VISIBLE);
            respondentGiveUp.setOnClickListener(this);

            if (DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner) {
               new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                        .execute();
            	
                DeviceDetailFragment.sendGamerNameService(true);
              //  timer_s.setVisibility(View.VISIBLE);
//                gracz_1.nazwa_gracza = DeviceDetailFragment.gamer;
//                gracz_2.is_drawing = true;
//                gracz_2.nazwa_gracza = DeviceDetailFragment.opponent;
                if (gra.listaHasel.isEmpty())
                {
                	gra.getAllHasla(this);
                }
                gra.lista_graczy.get(0).nazwa_gracza = gra.db.getAllGracze().get(0).getName();
                gra.lista_graczy.get(1).is_drawing = true;
                gra.lista_graczy.get(1).nazwa_gracza =  DeviceDetailFragment.opponent;
                paintView.setIsEnabled(false);
                toolsPanel.setVisibility(View.GONE);
               
                //ukrycie panelu z podpowiedziï¿½ dla rysujï¿½cego poniewaï¿½ zgadujï¿½cy nie rysuje
                forDrawerPanel.setVisibility(View.GONE);
                //timer_serv();

            } else {
            	
            	//client_task  = new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text));
            	//client_task.execute();
            	
                new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                        .execute();
                timer_c.setVisibility(View.VISIBLE);
//                gracz_1.nazwa_gracza = DeviceDetailFragment.gamer;
//                gracz_2.nazwa_gracza = DeviceDetailFragment.opponent;
//                gracz_1.is_drawing = true;
                if (gra.listaHasel.isEmpty())
                {
                	gra.getAllHasla(this);
                }
               
               
                gra.losuj_haslo();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                    	DeviceDetailFragment.sendWordService(false, gra.getHaslo());
                    }
                }, 1000);
                gra.lista_graczy.get(0).nazwa_gracza = gra.db.getAllGracze().get(0).getName();
                gra.lista_graczy.get(0).is_drawing = true;
                gra.lista_graczy.get(1).nazwa_gracza =  DeviceDetailFragment.opponent;

                //ustawienie hasï¿½a do podpowiadania

                word.setText(gra.getHaslo());

                //ukrycie panelu sï¿½uï¿½ï¿½cego do odpowiadania poniewaï¿½ rysujï¿½cy nie odpowiada
                answerPanel.setVisibility(View.GONE);
               new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                .execute();
                //client_task.execute();
               timer_client();
            }
        }
//			else
//			{
//				answerPanel.setVisibility(View.GONE);
//				forDrawerPanel.setVisibility(View.GONE);
//			}
        //-------------------------------------------------- rozgrywka ----------------------------

        SeekBar brashSize = (SeekBar) findViewById(R.id.brushSize);
        brashSize.setOnSeekBarChangeListener(this);

        toolsPanel.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                handle.setBackgroundResource(R.drawable.right);
                paintView.setIsEnabled(false);
            }
        });

        toolsPanel.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                handle.setBackgroundResource(R.drawable.left);
                paintView.setIsEnabled(true);
            }
        });

        saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Zapis obraznka");
        saveDialog.setMessage("Czy zapisaï¿½ obrazek do galerii?");
        saveDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                saveImage();
                dialog.cancel();
            }
        });
        saveDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        
        newImageDialog = new AlertDialog.Builder(this);
        newImageDialog.setTitle("Czyszczenie tablicy");
        newImageDialog.setMessage("Czy czy wyczyï¿½ciï¿½ tablicï¿½?");
        newImageDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                newImage();
                if(isGame)
                {
                	DeviceDetailFragment.sendClearScreenService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner);
                }
                dialog.cancel();
            }
        });
        newImageDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        
   	
	 giveUpDialog.setTitle("KFE");
	 giveUpDialog.setMessage("Czy napewno chcesz siê poddaæ?");
	 
	 giveUpDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             // TODO Auto-generated method stub
        	if(gra.lista_graczy.get(0).is_drawing)
        		cdown.cancel();
        	gra.losuj_haslo();
         	gra.nowa_runda(true);
         	DeviceDetailFragment.sendEndRoundService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner, true);
         	zmianaGraczy();
         	new EndGameDialog().show(getFragmentManager(), "end_game");
//         	final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    // Do something after 5s = 5000ms
//                	Tablica.tablica.newImage();
//                }
//            }, 1000);
         	
         	
         }
     });
     giveUpDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {

         @Override
         public void onClick(DialogInterface dialog, int which) {
             // TODO Auto-generated method stub
             dialog.cancel();
         }
     });
    }
    
    public void zmianaGraczy(){
    	
    	if(gra.lista_graczy.get(0).is_drawing){
    		paintView.setIsEnabled(true);
            toolsPanel.setVisibility(View.VISIBLE);
            //ukrycie panelu z podpowiedziï¿½ dla rysujï¿½cego poniewaï¿½ zgadujï¿½cy nie rysuje
            forDrawerPanel.setVisibility(View.VISIBLE);
            answerPanel.setVisibility(View.GONE);
            word.setText(gra.getHaslo());
    	}
    	else{
    		paintView.setIsEnabled(false);
            toolsPanel.setVisibility(View.GONE);
            //ukrycie panelu z podpowiedziï¿½ dla rysujï¿½cego poniewaï¿½ zgadujï¿½cy nie rysuje
            forDrawerPanel.setVisibility(View.GONE);
            answerPanel.setVisibility(View.VISIBLE);
    	}
    	
//    	if(gra.lista_graczy.get(1).is_drawing){
//    		paintView.setIsEnabled(true);
//            toolsPanel.setVisibility(View.VISIBLE);
//            //ukrycie panelu z podpowiedziï¿½ dla rysujï¿½cego poniewaï¿½ zgadujï¿½cy nie rysuje
//            forDrawerPanel.setVisibility(View.VISIBLE);
//            answerPanel.setVisibility(View.GONE);
//            word.setText(gra.getHaslo());
//    	}
//    	else{
//    		paintView.setIsEnabled(false);
//            toolsPanel.setVisibility(View.GONE);
//            //ukrycie panelu z podpowiedziï¿½ dla rysujï¿½cego poniewaï¿½ zgadujï¿½cy nie rysuje
//            forDrawerPanel.setVisibility(View.GONE);
//            answerPanel.setVisibility(View.VISIBLE);
//    	}
    	
    	if (DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner) {
            new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                   .execute();
    		//server_task.execute();
    	}
    	else
    	{
    		new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                      .execute();
    		//client_task.execute();
    	}
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.tablica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch(item.getItemId()) {
            case R.id.tmp_show_end_game_dialog:
                new EndGameDialog().show(getFragmentManager(), "end_game");
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        drawPaint.setStrokeWidth((float) progress);
        canvasPaint.setStrokeWidth((float) progress);

        paintView.setDrawPaint(drawPaint);
        paintView.setCanvasPaint(canvasPaint);

    }

    public static void set_haslo(String haslo) {
        gra.haslo = haslo;
        Tablica.gra.add_used_haslo();
        if (DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner) {
	        new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
	                .execute();
        	
        	
        }
        else
        {
        new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
            .execute();
        	
        }
    }

    public static byte[] convertInputStreamToByteArray(InputStream inputStream) {
        byte[] bytes = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte data[] = new byte[1024];
            int count;

            while ((count = inputStream.read(data)) != -1) {
                bos.write(data, 0, count);
            }

            bos.flush();
            bos.close();
            inputStream.close();

            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub


        switch (v.getId()) {
            case R.id.redButton:
                drawPaint.setColor(Color.RED);
                canvasPaint.setColor(Color.RED);
                brushColor = drawPaint.getColor();
                break;
            case R.id.yellowButton:
                drawPaint.setColor(Color.YELLOW);
                canvasPaint.setColor(Color.YELLOW);
                brushColor = drawPaint.getColor();
                break;
            case R.id.greenButton:
                drawPaint.setColor(Color.GREEN);
                canvasPaint.setColor(Color.GREEN);
                brushColor = drawPaint.getColor();
                break;
            case R.id.blueButton:
                drawPaint.setColor(Color.BLUE);
                canvasPaint.setColor(Color.BLUE);
                brushColor = drawPaint.getColor();
                break;
            case R.id.whiteButton:
                drawPaint.setColor(Color.WHITE);
                canvasPaint.setColor(Color.WHITE);
                brushColor = drawPaint.getColor();
                break;
            case R.id.blackButton:
                drawPaint.setColor(Color.BLACK);
                canvasPaint.setColor(Color.BLACK);
                brushColor = drawPaint.getColor();
                break;
            case R.id.saveButton:
                saveDialog.show();
                break;
            case R.id.brushTool:
                setBrushTool();
                break;
            case R.id.lineTool:
                setLineTool();
                break;
            case R.id.rectangleTool:
                setRectangleTool();
                break;
            case R.id.squareTool:
                setSquareTool();
                break;
            case R.id.circleTool:
                setCircleTool();
                break;
            case R.id.triangleTool:
                setTriangleTool();
                break;
            case R.id.eraserTool:
                setEraserTool();
                break;
            case R.id.newImageTool:
                newImageDialog.show();
                break;
            case R.id.confirmAnswer:
                String yourAnswer = answer.getText().toString();
                //tutaj obsï¿½uga przycisku wyï¿½lij odpowiedï¿½
                
                String haslo = gra.getHaslo();
                if(haslo==null)
                {
                	Toast nullAnswer = Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT);
                	nullAnswer.show();
                	DeviceDetailFragment.resendWordService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner, "resend");
                	final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                        	DeviceDetailFragment.sendWordService(false, gra.getHaslo());
                        }
                    }, 1000);
                    break;
                }
                else
                {
                	yourAnswer = yourAnswer.trim();
	                yourAnswer = yourAnswer.replace(" ", "");
	                yourAnswer = normalize(yourAnswer);
	                
	                haslo = haslo.trim();
	                haslo = haslo.replace(" ", "");
	                haslo = normalize(haslo);
	                
	                if(yourAnswer.equalsIgnoreCase(haslo)){
	                	Toast goodAnswer = Toast.makeText(getApplicationContext(), "Poprawna odpowiedŸ", Toast.LENGTH_SHORT);
	                	if(cdown!=null)
	                		{
	                			cdown.cancel();
	                		}
	                	PaintView.czyOdbierac=false;
	                    goodAnswer.show();
	                    gra.losuj_haslo();
	                    DeviceDetailFragment.sendEndRoundService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner, false);
	                    gra.nowa_runda(false);
	                	zmianaGraczy();
	                	Tablica.tablica.newImage();
	                	new EndGameDialog().show(getFragmentManager(), "end_game");
	                }
	                else{
	                	Toast badAnswer = Toast.makeText(getApplicationContext(), "B³êdna odpowiedŸ", Toast.LENGTH_SHORT);
	                	badAnswer.show();
	                }
                }
                //na koniec po wysï¿½aniu odpowiedzi trzeba wyczyï¿½ciï¿½ pole
                answer.setText("");
                break;
            case R.id.respondentGiveUp:
                //tutaj obsï¿½uga poddania siï¿½ odpowiadajï¿½cego
            	PaintView.czyOdbierac=false;
                giveUpDialog.show();
                break;
            case R.id.drawerGiveUp:            	
                //tutaj obsï¿½uga poddania siï¿½ rysujï¿½cego
            	Tablica.tablica.cdown.cancel();
            	PaintView.czyPrzesylac=false;
            	PaintView.czyOdbierac=false;
            	giveUpDialog.show();
                break;

        }
        paintView.setDrawPaint(drawPaint);

    }

    public void saveImage() {
        paintView.setDrawingCacheEnabled(true);

        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), paintView.getDrawingCache(),
                UUID.randomUUID().toString() + ".png", "drawing");

        if (imgSaved != null) {
            Toast saveToast = Toast.makeText(getApplicationContext(), "Zapisano do galerii", Toast.LENGTH_SHORT);
            saveToast.show();
        } else {
            Toast unsavedToast = Toast.makeText(getApplicationContext(), "Wystï¿½piï¿½ problem podczas zapisu", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }

        paintView.destroyDrawingCache();
    }
    
    public void show_endgame()
    {
    	 new EndGameDialog().show(getFragmentManager(), "end_game");
    }
    public void setColor() {
        drawPaint.setColor(brushColor);
        canvasPaint.setColor(brushColor);
    }

    public void setBrushTool() {
        setColor();
        paintView.setMCurrentShape(paintView.SMOOTHLINE);
    }

    public void setLineTool() {
        setColor();
        paintView.setMCurrentShape(paintView.LINE);
    }

    public void setRectangleTool() {
        setColor();
        paintView.setMCurrentShape(paintView.RECTANGLE);
    }

    public void setCircleTool() {
        setColor();
        paintView.setMCurrentShape(paintView.CIRCLE);
    }

    public void setSquareTool() {
        setColor();
        paintView.setMCurrentShape(paintView.SQUARE);
    }

    public void setTriangleTool() {
        setColor();
        paintView.setMCurrentShape(paintView.TRIANGLE);
        paintView.resetTriangle();
    }

    public void setEraserTool() {
        drawPaint.setColor(Color.WHITE);
        canvasPaint.setColor(Color.WHITE);
        paintView.setMCurrentShape(paintView.SMOOTHLINE);
    }

    public void newImage() {
        paintView.newImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isGame)
        {
	        channel2 = WiFiDirectActivity.manager.initialize(this, getMainLooper(), null);
	        receiver2 = WiFiDirectBroadcastReceiver.getInstance(WiFiDirectActivity.manager, channel2, this);
	        registerReceiver(receiver2, WiFiDirectActivity.intentFilter);
        }

    }
    
    public String normalize(String slowo)
    {
    	
    	StringBuilder normalized_slowo = new StringBuilder(slowo);
    	for(int i=0; i < slowo.length() ; i++)
    	{
	    	if(normalized_slowo.charAt(i)=='¿')
	    	{
	    		normalized_slowo.setCharAt(i, 'z');
	    	}
	    	else if(normalized_slowo.charAt(i)=='Ÿ')
	    	{
	    		normalized_slowo.setCharAt(i, 'z');
	    	}
	    	else if(normalized_slowo.charAt(i)=='ñ')
	    	{
	    		normalized_slowo.setCharAt(i, 'n');
	    	}
	    	else if(normalized_slowo.charAt(i)=='³')
	    	{
	    		normalized_slowo.setCharAt(i, 'l');
	    	}
	    	else if(normalized_slowo.charAt(i)=='ó')
	    	{
	    		normalized_slowo.setCharAt(i, 'o');
	    	}
	    	else if(normalized_slowo.charAt(i)=='œ')
	    	{
	    		normalized_slowo.setCharAt(i, 's');
	    	}
	    	else if(normalized_slowo.charAt(i)=='¹')
	    	{
	    		normalized_slowo.setCharAt(i, 'a');
	    	}
	    	else if(normalized_slowo.charAt(i)=='ê')
	    	{
	    		normalized_slowo.setCharAt(i, 'e');
	    	}
	    	else if(normalized_slowo.charAt(i)=='æ')
	    	{
	    		normalized_slowo.setCharAt(i, 'c');
	    	}
    	}
		return normalized_slowo.toString();
    	
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isGame)
        {
        unregisterReceiver(receiver2);
        }
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        WiFiDirectActivity.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelDisconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public void connect(WifiP2pConfig config) {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChannelDisconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {


        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
           new TextServerAsyncTask(this, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                   .execute();
        	//server_task.execute();
        }


    }

    @Override
    public void onGameRerunAck(DialogFragment dialog) {
        newImage();
        PaintView.czyOdbierac=true;
        PaintView.czyPrzesylac=true;
        licznik=0;
        if (DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner) {
            new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                     .execute();
            timer_c.setVisibility(View.VISIBLE);
            timer_client();
        }
        else
        {
        	new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
            .execute();
        	timer_client();
        }
        
    }

    @Override
    public void onGameRerunNack(DialogFragment dialog) {
    	DeviceDetailFragment.sendEndGameService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner);
    	zapisz_do_bazy();
    	finish();
    	
    }
    
    
    @Override
    public void onBackPressed() {
    	if(isGame)
    	{
	    	DeviceDetailFragment.sendEndGameService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner);
	    	finish();
    	}
    	else
    	{
    		finish();
    	}
    }
    
    public void timer_serv()
    {
    	new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer_s.setText("t: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
            	//new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text)).cancel(true);
            	new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text))
                .execute();
            	//PaintView.czyOdbierac=false;
            	//PaintView.czyPrzesylac=false;
            	if(gra.lista_graczy.get(0).is_drawing && licznik<1)
            	{
            		gra.losuj_haslo();
                 	gra.nowa_runda(true);
                 	DeviceDetailFragment.sendEndRoundService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner, true);
                 	zmianaGraczy();
                 	new EndGameDialog().show(getFragmentManager(), "end_game");
            	}
            }
         }.start();
    }
    public void timer_client()
    {
    	cdown = new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer_c.setText("t: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                //mTextField.setText("done!");
            	
            	//new DeviceDetailFragment.ForClientServerAsyncTask (Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text)).cancel(true);
            	new DeviceDetailFragment.ForClientServerAsyncTask (Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text)).execute();
            	//PaintView.czyOdbierac=false;
            	//PaintView.czyPrzesylac=false;
            	PaintView.czyPrzesylac = false;
            	if(gra.lista_graczy.get(0).is_drawing && licznik<1)
            	{
            		gra.losuj_haslo();
                 	gra.nowa_runda(true);
                 	DeviceDetailFragment.sendEndRoundService(DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner, true);
                 	zmianaGraczy();
                 	new EndGameDialog().show(getFragmentManager(), "end_game");
            	}
            }
         }.start();
    }
    
    public void  cancelTimer() {
    	if (cdown != null) {
    	    cdown.cancel();
    	}
    }
    
    public void zapisz_do_bazy()
    {
    	
//    	db = new DatabaseHelper(getApplicationContext());
//		int pkt=20;
//		int pkt2=30;
        Gracz player1 = new Gracz(gra.lista_graczy.get(0).nazwa_gracza);
        Gracz player2 = new Gracz(gra.lista_graczy.get(1).nazwa_gracza);
        gra.db.createGracz(player1);
        gra.db.createGracz(player2);
//		
		Rozgrywka gra1 = new Rozgrywka();
		gra.db.createRozgrywka(gra1, new long[] {gra.db.getIDGracza(gra.lista_graczy.get(0).nazwa_gracza),gra.db.getIDGracza(gra.lista_graczy.get(1).nazwa_gracza)},new int[]{gra.lista_graczy.get(0).punkty,
				gra.lista_graczy.get(1).punkty});
    }
    
    

}
