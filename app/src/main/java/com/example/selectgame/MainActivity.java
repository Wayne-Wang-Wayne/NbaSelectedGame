package com.example.selectgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {

    int correctAnswer;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    List<Button> buttons = new ArrayList<>();

    Set<Integer> checkNames = new HashSet<>();
    List<Integer> trackOrder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);


        GetPlayerName.GetInfo callBack = new GetPlayerName.GetInfo() {
            @Override
            public void withPlayerName(List<Player> playerList) {

                resetBoard(playerList);

                for (int i = 0; i < 4; i++) {
                    final int index = i;
                    buttons.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(checkNames.isEmpty() || trackOrder.isEmpty()){
                                resetBoard(playerList);
                                return;
                            }

                            if (trackOrder.get(index).equals(trackOrder.get(4))) {
                                imageView.setImageResource(R.drawable.corrrreect);
                            } else {
                                imageView.setImageResource(R.drawable.wroooong);
                            }

                            checkNames.clear();
                            trackOrder.clear();

                        }
                    });
                }


            }
        };

        GetPlayerName getPlayerName = new GetPlayerName(callBack);
        getPlayerName.execute();


    }

    public void resetBoard(List<Player> playerList) {

        Random random = new Random();
        int order = random.nextInt(playerList.size());
        int correctOrder = order;
        Player player = playerList.get(order);
        Glide.with(MainActivity.this).load(player.getImageUrl()).into(imageView);
        checkNames.add(correctOrder);

        int oneOfFour = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            if (i == oneOfFour) {
                player = playerList.get(correctOrder);
                buttons.get(i).setText(player.getFirstName() + "  " + player.getLastName());
                trackOrder.add(correctOrder);
            } else {

                order = random.nextInt(playerList.size());
                while (!checkNames.add(order)) {
                    order = random.nextInt(playerList.size());
                }

                player = playerList.get(order);
                buttons.get(i).setText(player.getFirstName() + "  " + player.getLastName());
                trackOrder.add(order);
            }
        }
        trackOrder.add(correctOrder);
    }


}

class GetPlayerName extends AsyncTask<String, Void, List<Player>> {

    interface GetInfo {
        void withPlayerName(List<Player> playerList);
    }

    GetInfo getInfo;


    GetPlayerName(GetInfo getInfo) {
        this.getInfo = getInfo;
    }

    @Override
    protected List<Player> doInBackground(String... strings) {

        Document document = null;
        List<Player> playerList = new ArrayList<>();
        try {

            document = Jsoup.connect("https://www.nba.com/players").get();
            Elements elements = document.select("table.players-list tr");

            for (Element e : elements) {
                try {
                    playerList.add(Player.createPlayerFromElement(e));
                } catch (Exception exe) {
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerList;
    }

    @Override
    protected void onPostExecute(List<Player> playerList) {

        getInfo.withPlayerName(playerList);

    }


}

class Player {


    private String firstName;
    private String lastName;
    private String imageUrl;


    public Player(String firstName, String lastName, String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;

    }

    public static Player createPlayerFromElement(Element element) {

        Elements firstLastNames = element.select("p.t6");
        Element imageUrl = element.select("img.PlayerImage_image__1smob").get(0);

        Player player = new Player(firstLastNames.get(0).text(),
                firstLastNames.get(1).text(),
                imageUrl.attr("src"));
        return player;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}



