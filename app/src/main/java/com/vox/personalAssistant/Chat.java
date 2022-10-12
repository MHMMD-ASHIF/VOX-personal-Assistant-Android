package com.vox.personalAssistant;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voxpersonalassistant.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Chat extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText editText;
    ImageView imageView;
    ArrayList<Chatsmodel> chatsmodalArrayList;
    ChatAdapter chatAdapter;
    private  final String USER_KEY = "user";
    private  final String BOT_KEY = "bot";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.edt_msg);
        imageView = findViewById(R.id.send_btn);
        chatsmodalArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatsmodalArrayList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatAdapter);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().isEmpty()){
                    Toast.makeText(Chat.this,"Please enter your message",Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(editText.getText().toString());
                editText.setText("");
            }
        });



    }

    private void getResponse(String message) {
        chatsmodalArrayList.add(new Chatsmodel(message,USER_KEY));
        chatAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=162139&key=ay3O1OYJtTpltNPY&uid=[uid]&msg=[msg]="+message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetroFitApi retroFitApi = retrofit.create(RetroFitApi.class);
        Call<MsgModel> call = retroFitApi.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                if(response.isSuccessful()){
                    MsgModel msgModal = response.body();
                    chatsmodalArrayList.add(new Chatsmodel(msgModal.getCnt(),BOT_KEY));
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatsmodalArrayList.size()-1);
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {
                chatsmodalArrayList.add(new Chatsmodel("no response",BOT_KEY));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }
}