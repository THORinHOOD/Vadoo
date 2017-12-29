package catwithbowtie.chatchat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import catwithbowtie.chatchat.Fragments.Items.Msg;
import catwithbowtie.chatchat.MainActivity;
import catwithbowtie.chatchat.R;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import okhttp3.*;
import okio.BufferedSink;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class FragmentDialogList extends Fragment {

    private FragmentDialogList.OnFragmentInteractionListener mListener;
    public DialogAdapter adapter;
    private View view;
    private ArrayList<Msg> msgs;

    private LinearLayoutManager layoutManager;
    private EditText editText;
    private BootstrapCircleThumbnail btn;
    private RecyclerView rv;

    private MainActivity act;

    LinearLayoutManager llm;

 //   private  BufferedReader inFromServer;
 //   private DataOutputStream outToServer;
//    private Socket clientSocket;
    ArrayList<Msg> recievedMsgs;
    private MyTimerTask mMyTimerTask;
    private Timer timer;

    public FragmentDialogList(MainActivity act) {
        msgs = new ArrayList<Msg>();
        this.act = act;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recievedMsgs = new ArrayList<Msg>();

        view = inflater.inflate(R.layout.fragment_dialog_list, container, false);

        RecyclerView feedRV = (RecyclerView) view.findViewById(R.id.recycle_view_msgs);
        llm = new LinearLayoutManager(getContext());
        feedRV.setLayoutManager(llm);
        adapter = new DialogAdapter(msgs, getContext(), view);
        feedRV.setAdapter(adapter);

        editText = (EditText) view.findViewById(R.id.edit_msgs);
        btn = (BootstrapCircleThumbnail) view.findViewById(R.id.btn_send_msg);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = editText.getText().toString();

                Log.d("TCP", "i was here -1");
                 if (!text.equals("")) {
                    msgs.add(new Msg(text, "me"));
                    Log.d("TCP", "i was here 0");
                    sendMessage(text);
                     Log.d("TCP", "i was here 0(a)");
                    adapter.notifyDataSetChanged();
                    llm.scrollToPosition(msgs.size() - 1);
                    editText.setText("");
                }
            }
        });

        mMyTimerTask = new MyTimerTask(act, act.login, act.password);
        timer = new Timer();
        timer.schedule(mMyTimerTask, 1000, 500);
        return view;
    }

    private void sendMessage(String text) {
        Log.d("TCP", "i was here 0.5");
        MyThread t = new MyThread(text);
        t.start();
    }

    public class MyThread extends Thread {
        private String message;
        public MyThread(String message) {
            this.message = message;
            Log.d("TCP", "i was here 1");
        }

        public void run() {
            Log.d("TCP", "i was here 2");
            act.mTcpClient.sendMessage(message);
        }
    }

/*
    public void getMessages(final Activity act, final String userLogin, final String userPassword) throws Exception {

        Request request = new Request.Builder()
                .url(ip + "get_messages" + makeParam("login", userLogin) + makeParam("password", userPassword))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    String res = response.body().string();
                    if (checkOnSuccess(res)) {
                       ArrayList<Msg> curMsgs = parseRecievedMsgs(res);
                       if (curMsgs.size() > recievedMsgs.size()) {
                           for (int i = recievedMsgs.size(); i < curMsgs.size(); i++) {
                                recievedMsgs.add(curMsgs.get(i));
                                addMessage(curMsgs.get(i).text, curMsgs.get(i).from);
                           }
                       }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void sendMessage(final Activity act, final String userLogin, final String userPassword, final String recieverLogin, final String sendText) throws Exception {

        Request request = new Request.Builder()
                .url(ip + "send_message" + makeParam("login_sender", userLogin) + makeParam("password_sender", userPassword)
                        + makeParam("login_recipient", recieverLogin) + makeParam("message", sendText))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(act, "Что-то пошло не так, сообщение не отправлено", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    if (!checkOnSuccess(response.body().string())) {
                        act.runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  Toast.makeText(act, "Сообщение не отправлено", Toast.LENGTH_SHORT).show();
                                              }
                                          });
                    } else {
                        addMessage(recieverLogin + "$" + sendText, "me");
                        act.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                               editText.setText("");
                            }
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private ArrayList<Msg> parseRecievedMsgs(String response) {
        ArrayList<Msg> newArray = new ArrayList<Msg>();

        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(response);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray dialogs = (JSONArray) obj.get("messages");
        for (Object buf : dialogs) {
            JSONObject msg = (JSONObject) buf;
            String from = (String) msg.get("name");
            JSONArray msgs = (JSONArray) msg.get("dialog");
            for (Object buf2 : msgs) {
                String text = (String) buf2;
                newArray.add(new Msg(text, from));
            }
        }
        return newArray;
    }

    private boolean checkOnSuccess(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response);
        Boolean status = (Boolean) obj.get("status");
        return status;
    }

    private String makeParam(String nameParam, String value) {
        return "&" + value;
    }
*/
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentDialogList.OnFragmentInteractionListener) {
            mListener = (FragmentDialogList.OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getMessages(){
        ArrayList<String> names = new ArrayList<String>();
        names.add("me");
        names.add("Petya.A");
        names.add("Kek");
        names.add("Lol");
        names.add("Guy");

        final Random random = new Random();


        for (int i = 0; i < 10; i++) {
            int ind = random.nextInt(names.size());
            msgs.add(new Msg("Всем привет!!!", names.get(ind)));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void addMessage(String text,String from) {
        msgs.add(new Msg(text,from));
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                act.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapter.notifyDataSetChanged();
                        act.dialogList.llm.scrollToPosition(msgs.size() - 1);
                    }
                });
            }
        });
        myThread.start();
    }


    class MyTimerTask extends TimerTask {
        private Activity act;
        private String userLogin;
        private String userPassword;
        public MyTimerTask(Activity act, String userLogin, String userPassword) {
            this.act = act;
            this.userLogin = userLogin;
            this.userPassword = userPassword;
        }
        @Override
        public void run() {
            try {
               // getMessages(act, userLogin, userPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        try {
     //       clientSocket.close();
        } catch(Exception e) {

        }
        super.onStop();
    }
}
