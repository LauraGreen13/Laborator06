package ro.pub.cs.systems.eim.lab06.ftpserverwelcomemessage.views;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ro.pub.cs.systems.eim.lab06.ftpserverwelcomemessage.R;
import ro.pub.cs.systems.eim.lab06.ftpserverwelcomemessage.general.Constants;
import ro.pub.cs.systems.eim.lab06.ftpserverwelcomemessage.general.Utilities;

import java.io.BufferedReader;
import java.net.Socket;

public class FTPServerWelcomeMessageActivity extends AppCompatActivity {

    private EditText FTPServerAddressEditText;
    private Button displayWelcomeMessageButton;
    private TextView welcomeMessageTextView;

    private String output;
    private class FTPServerCommunicationAsyncTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                // TODO: exercise 4
                // open socket with FTPServerAddress (taken from params[0]) and port (Constants.FTP_PORT = 21)
                // get the BufferedReader attached to the socket (call to the Utilities.getReader() method)
                // should the line start with Constants.FTP_MULTILINE_START_CODE, the welcome message is processed
                // read lines from server while
                // - the value is different from Constants.FTP_MULTILINE_END_CODE1
                // - the value does not start with Constants.FTP_MULTILINE_START_CODE2
                // append the line to the welcomeMessageTextView text view content (on the UI thread!!!) - publishProgress(...)
                // close the socket

                Socket socket = new Socket(params[0], Constants.FTP_PORT);

                BufferedReader bufferedReader = Utilities.getReader(socket);

                StringBuilder stringBuilder = new StringBuilder();

                String line = bufferedReader.readLine();

                if (line.startsWith(Constants.FTP_MULTILINE_START_CODE)) {
                    while (line != null) {
                        if (!line.equals(Constants.FTP_MULTILINE_END_CODE1) && !line.startsWith(Constants.FTP_MULTILINE_END_CODE2)) {
                            line = bufferedReader.readLine();
                            Log.d(Constants.TAG, "Line read: " + line);
                            publishProgress(line);
                        } else {
                            break;
                        }

                    }
                }

                output = new String(stringBuilder);


                socket.close();
            } catch (Exception exception) {
                Log.d(Constants.TAG, exception.getMessage());
                if (Constants.DEBUG) {
                    exception.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            welcomeMessageTextView.setText("");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            // TODO: exercise 4
            // append the progress[0] to the FTPServerAddressEditText edit text

            welcomeMessageTextView.append(progress[0] + "\n");
        }

        @Override
        protected void onPostExecute(Void result) { }
    }

    private ButtonClickListener buttonClickListener = new ButtonClickListener();
    private class ButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            FTPServerCommunicationAsyncTask ftpServerCommunicationAsyncTask = new FTPServerCommunicationAsyncTask();
            ftpServerCommunicationAsyncTask.execute(FTPServerAddressEditText.getText().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpserver_welcome_message);

        Log.d(Constants.TAG, "setContentView()");

        FTPServerAddressEditText = (EditText)findViewById(R.id.ftp_server_address_edit_text);

        displayWelcomeMessageButton = (Button)findViewById(R.id.display_welcome_message_button);
        displayWelcomeMessageButton.setOnClickListener(buttonClickListener);

        welcomeMessageTextView = (TextView)findViewById(R.id.welcome_message_text_view);
    }
}
