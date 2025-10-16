package com.example.leandromarques.chatbotdoid;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.leandromarques.chatbotdoid.Adapter.ChatMessageAdapter;
import com.example.leandromarques.chatbotdoid.Pojo.ChatMessage;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;

public class BotActivity extends AppCompatActivity {

    private ListView listView;
    private FloatingActionButton btnSend;
    private EditText edTxMessage;
    private ImageView imageView;
    private ChatMessageAdapter adapter;
    public Bot bot;
    public static Chat chat;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        btnSend = (FloatingActionButton) findViewById(R.id.btn_send);
        edTxMessage = (EditText) findViewById(R.id.et_message);
        imageView = (ImageView) findViewById(R.id.iv_image);
        adapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMsg = edTxMessage.getText().toString();

                String response = chat.multisentenceRespond(edTxMessage.getText().toString());
                if (TextUtils.isEmpty(newMsg)) {
                    return;
                }
                sendMessage(newMsg);
                mimicOtherMsg(response);
                edTxMessage.setText("");
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        //checking SD card availablility
        boolean a = isSDCARDAvailable();
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari");
        boolean bool = jayDir.mkdirs();

        if (jayDir.exists()){
            try {
                for (String dir : assets.list("Hari")){
                    File subdir = new File(jayDir.getParent() + "/" + dir);
                    boolean subdir_check = subdir.mkdir();
                    for (String file : assets.list("Hari/" + dir)){
                        File fl = new File(jayDir.getParent() + "/" + "/" + file);
                        if (fl.exists()){
                            continue;
                        }
                        InputStream input = null;
                        OutputStream out = null;
                        input = assets.open("Hari/"+ dir +"/"+ file);
                        out = new FileOutputStream(jayDir.getPath()+ "/" + dir + "/" + file);
                        copyFile(input, out);
                        input.close();
                        out.flush();
                        out.close();
                        out =null;
                    }
                }
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
        System.out.print("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        bot = new Bot("Hari", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void mainFunction(String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+ request);
        System.out.println("Robot: " + response);
    }

    private void copyFile(InputStream input, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = input.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private boolean isSDCARDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }



    private void sendMessage(String message) {
        ChatMessage chatMsg = new ChatMessage(message, true, false);
        adapter.add(chatMsg);
        mimicOtherMsg("Ola !");
    }

    private void mimicOtherMsg(String message) {
        ChatMessage chatMsgMimic = new ChatMessage(message, false, false);
        adapter.add(chatMsgMimic);
    }

    private void sendMessage() {
        ChatMessage chatNewMsg = new ChatMessage(null, true, true);
        adapter.add(chatNewMsg);

        mimicOtherMsg();
    }

    private void mimicOtherMsg() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        adapter.add(chatMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Bot Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
