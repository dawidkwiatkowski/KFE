package com.app.kfe.rysowanie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import com.app.kfe.R;
import com.app.kfe.controler.GameManager;
import com.app.kfe.dialogs.EndGameDialog;
import com.app.kfe.wifi.DeviceDetailFragment;
import com.app.kfe.wifi.DeviceDetailFragment.TextServerAsyncTask;
import com.app.kfe.wifi.DeviceListFragment;
import com.app.kfe.wifi.DeviceListFragment.DeviceActionListener;
import com.app.kfe.wifi.WiFiDirectActivity;
import com.app.kfe.wifi.WiFiDirectBroadcastReceiver;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class Tablica extends Activity implements OnSeekBarChangeListener, OnClickListener, ChannelListener, DeviceActionListener, GameManager.GameMessagesListener, EndGameDialog.EndGameDialogActionsHandler {

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
    private AlertDialog.Builder saveDialog;
    private AlertDialog.Builder newImageDialog;
    private int brushColor;
    public static Tablica tablica = null;
    public static Channel channel2;
    public static BroadcastReceiver receiver2 = null;
    private RelativeLayout answerPanel;
    private Button confirmAnwer;
    private EditText answer;

    public static Activity activity;

    public static boolean isGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tablica);
        activity = this;
        tablica = this;

        answerPanel = (RelativeLayout) findViewById(R.id.answerRelativeLayout);
        confirmAnwer = (Button) findViewById(R.id.confirmAnswer);
        answer = (EditText) findViewById(R.id.answer);
        WiFiDirectActivity.co_to = "cos";
        channel2 = WiFiDirectActivity.manager.initialize(this, getMainLooper(), null);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("isGame")) {
            isGame = getIntent().getExtras().getBoolean("isGame");

            if (DeviceDetailFragment.info.groupFormed && DeviceDetailFragment.info.isGroupOwner) {
                new TextServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text)).execute();
                DeviceDetailFragment.gamer = "Gracz2";
                DeviceDetailFragment.sendGamerNameService();
            } else {
                new DeviceDetailFragment.ForClientServerAsyncTask(Tablica.tablica, DeviceDetailFragment.mContentView.findViewById(R.id.status_text)).execute();

            }
            answerPanel.setVisibility(View.VISIBLE);
            confirmAnwer.setOnClickListener(this);

        } else {
            answerPanel.setVisibility(View.GONE);
        }

        SlidingDrawer toolsPanel = (SlidingDrawer) findViewById(R.id.toolsPanel);
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
        saveDialog.setMessage("Czy zapisa� obrazek do galerii?");
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
        newImageDialog.setMessage("Czy czy wyczyścić tablicę?");
        newImageDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newImage();
                dialog.cancel();
            }
        });
        newImageDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tablica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.tmp_show_end_game_dialog:
                DialogFragment endGameDialog = new EndGameDialog();
                endGameDialog.show(getFragmentManager(), "end_game");
                result = false;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

        drawPaint.setStrokeWidth((float) progress);
        canvasPaint.setStrokeWidth((float) progress);

        paintView.setDrawPaint(drawPaint);
        paintView.setCanvasPaint(canvasPaint);

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
                //tutaj obs�uga przycisku wy�lij odpowied�

                //na koniec po wys�aniu odpowiedzi trzeba wyczy�ci� pole
                answer.setText("");
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
            Toast unsavedToast = Toast.makeText(getApplicationContext(), "Wystąpił problem podczas zapisu", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }

        paintView.destroyDrawingCache();
    }

    public void setColor() {
        drawPaint.setColor(brushColor);
        canvasPaint.setColor(brushColor);
    }

    public void setBrushTool() {
        setColor();
        paintView.setMCurrentShape(PaintView.SMOOTHLINE);
    }

    public void setLineTool() {
        setColor();
        paintView.setMCurrentShape(PaintView.LINE);
    }

    public void setRectangleTool() {
        setColor();
        paintView.setMCurrentShape(PaintView.RECTANGLE);
    }

    public void setCircleTool() {
        setColor();
        paintView.setMCurrentShape(PaintView.CIRCLE);
    }

    public void setSquareTool() {
        setColor();
        paintView.setMCurrentShape(PaintView.SQUARE);
    }

    public void setTriangleTool() {
        setColor();
        paintView.setMCurrentShape(PaintView.TRIANGLE);
        paintView.resetTriangle();
    }

    public void setEraserTool() {
        drawPaint.setColor(Color.WHITE);
        canvasPaint.setColor(Color.WHITE);
        paintView.setMCurrentShape(PaintView.SMOOTHLINE);
    }

    public void newImage() {
        paintView.newImage();
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver2 = WiFiDirectBroadcastReceiver.getInstance(WiFiDirectActivity.manager, WiFiDirectActivity.channel, this);
        registerReceiver(receiver2, WiFiDirectActivity.intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver2);
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
        }


    }

    @Override
    public void onGameStartMessageReceived(JSONObject gameObject) {
    }

    @Override
    public void onCanvasMessageReceived(Bitmap image) {

        paintView.drawImage(image);
    }

    @Override
    public void onGameRerunAck(DialogFragment dialog) {
        newImage();
    }

    @Override
    public void onGameRerunNack(DialogFragment dialog) {

    }
}
