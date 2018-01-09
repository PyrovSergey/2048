package com.example.pyrov.a2048;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    // текущий счет (очки)
    int score;
    // максимальный вес плитки (Tile.value)
    int maxTile;
    // константа, определяющая ширину игрового поля
    private static final int FIELD_WIDTH = 4;
    // двумерный массив, состоящий из объектов класса Tile
    private Tile[][] gameTiles;
    // булево - сохранить игру?
    private boolean isSaveNeeded = true;
    // стэк для хранения предыдущего состояния поля
    Stack<Tile[][]> previousStates = new Stack<Tile[][]>();
    // стэк для хранения предыдущего состояния очков
    Stack<Integer> previousScores = new Stack<>();
    // булево игра выиграна?
    boolean isGameWon = false;
    // булево игра проиграна?
    boolean isGameLost = false;

    // вьюшка текущего счета
    TextView textViewScore;
    // макет игрового поля
    RelativeLayout playField;
    // кнопка назад
    ImageButton imageButtonBack;
    // кнопка рестарта
    ImageButton imageButtonRefresh;
    // кнопка лучшего хода
    ImageButton imageButtonHack;
    // вьюшки игрового поля
    TextView textViewOneOne;
    TextView textViewOneTwo;
    TextView textViewOneThree;
    TextView textViewOneFour;
    TextView textViewTwoOne;
    TextView textViewTwoTwo;
    TextView textViewTwoThree;
    TextView textViewTwoFour;
    TextView textViewThreeOne;
    TextView textViewThreeTwo;
    TextView textViewThreeThree;
    TextView textViewThreeFour;
    TextView textViewFourOne;
    TextView textViewFourTwo;
    TextView textViewFourThree;
    TextView textViewFourFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // инициализация макета
        textViewScore = (TextView) findViewById(R.id.score);
        playField = (RelativeLayout) findViewById(R.id.playing_field);
        imageButtonBack = (ImageButton) findViewById(R.id.button_back);
        imageButtonRefresh = (ImageButton) findViewById(R.id.button_refresh);
        imageButtonHack = (ImageButton) findViewById(R.id.button_hack);
        textViewOneOne = (TextView) findViewById(R.id.one_one);
        textViewOneTwo = (TextView) findViewById(R.id.one_two);
        textViewOneThree = (TextView) findViewById(R.id.one_three);
        textViewOneFour = (TextView) findViewById(R.id.one_four);
        textViewTwoOne = (TextView) findViewById(R.id.two_one);
        textViewTwoTwo = (TextView) findViewById(R.id.two_two);
        textViewTwoThree = (TextView) findViewById(R.id.two_three);
        textViewTwoFour = (TextView) findViewById(R.id.two_four);
        textViewThreeOne = (TextView) findViewById(R.id.three_one);
        textViewThreeTwo = (TextView) findViewById(R.id.three_two);
        textViewThreeThree = (TextView) findViewById(R.id.three_three);
        textViewThreeFour = (TextView) findViewById(R.id.three_four);
        textViewFourOne = (TextView) findViewById(R.id.four_one);
        textViewFourTwo = (TextView) findViewById(R.id.four_two);
        textViewFourThree = (TextView) findViewById(R.id.four_three);
        textViewFourFour = (TextView) findViewById(R.id.four_four);
        // вешаем на кнопки слушателя
        imageButtonBack.setOnClickListener(this);
        imageButtonRefresh.setOnClickListener(this);
        imageButtonHack.setOnClickListener(this);
        // инициализируем массив
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        final Context context = this;

        resetGameTiles();

        playField.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                if (!canMove()) {
                    isGameLost = true;
                }
                if (!isGameLost && !isGameWon) {
                    left();
                }
            }

            @Override
            public void onSwipeTop() {
                if (!canMove()) {
                    isGameLost = true;
                }
                if (!isGameLost && !isGameWon) {
                    up();
                }
            }

            @Override
            public void onSwipeBottom() {
                if (!canMove()) {
                    isGameLost = true;
                }
                if (!isGameLost && !isGameWon) {
                    down();
                }
            }

            @Override
            public void onSwipeRight() {
                if (!canMove()) {
                    isGameLost = true;
                }
                if (!isGameLost && !isGameWon) {
                    right();
                }
            }

        });
    }

    // метод обновления игрового поля
    public void repaint() {
        textViewScore.setText(String.valueOf(score));
        // обновляем все значения TextView по массиву
        textViewOneOne.setText(String.valueOf(gameTiles[0][0].value));
        textViewOneTwo.setText(String.valueOf(gameTiles[0][1].value));
        textViewOneThree.setText(String.valueOf(gameTiles[0][2].value));
        textViewOneFour.setText(String.valueOf(gameTiles[0][3].value));
        textViewTwoOne.setText(String.valueOf(gameTiles[1][0].value));
        textViewTwoTwo.setText(String.valueOf(gameTiles[1][1].value));
        textViewTwoThree.setText(String.valueOf(gameTiles[1][2].value));
        textViewTwoFour.setText(String.valueOf(gameTiles[1][3].value));
        textViewThreeOne.setText(String.valueOf(gameTiles[2][0].value));
        textViewThreeTwo.setText(String.valueOf(gameTiles[2][1].value));
        textViewThreeThree.setText(String.valueOf(gameTiles[2][2].value));
        textViewThreeFour.setText(String.valueOf(gameTiles[2][3].value));
        textViewFourOne.setText(String.valueOf(gameTiles[3][0].value));
        textViewFourTwo.setText(String.valueOf(gameTiles[3][1].value));
        textViewFourThree.setText(String.valueOf(gameTiles[3][2].value));
        textViewFourFour.setText(String.valueOf(gameTiles[3][3].value));
        // обновляем все цвета текта TextView по массиву
        textViewOneOne.setTextColor(gameTiles[0][0].getFontColor());
        textViewOneTwo.setTextColor(gameTiles[0][1].getFontColor());
        textViewOneThree.setTextColor(gameTiles[0][2].getFontColor());
        textViewOneFour.setTextColor(gameTiles[0][3].getFontColor());
        textViewTwoOne.setTextColor(gameTiles[1][0].getFontColor());
        textViewTwoTwo.setTextColor(gameTiles[1][1].getFontColor());
        textViewTwoThree.setTextColor(gameTiles[1][2].getFontColor());
        textViewTwoFour.setTextColor(gameTiles[1][3].getFontColor());
        textViewThreeOne.setTextColor(gameTiles[2][0].getFontColor());
        textViewThreeTwo.setTextColor(gameTiles[2][1].getFontColor());
        textViewThreeThree.setTextColor(gameTiles[2][2].getFontColor());
        textViewThreeFour.setTextColor(gameTiles[2][3].getFontColor());
        textViewFourOne.setTextColor(gameTiles[3][0].getFontColor());
        textViewFourTwo.setTextColor(gameTiles[3][1].getFontColor());
        textViewFourThree.setTextColor(gameTiles[3][2].getFontColor());
        textViewFourFour.setTextColor(gameTiles[3][3].getFontColor());
        // обновляем все цвета фона TextView по массиву
        textViewOneOne.setBackgroundColor(gameTiles[0][0].getTileColor());
        textViewOneTwo.setBackgroundColor(gameTiles[0][1].getTileColor());
        textViewOneThree.setBackgroundColor(gameTiles[0][2].getTileColor());
        textViewOneFour.setBackgroundColor(gameTiles[0][3].getTileColor());
        textViewTwoOne.setBackgroundColor(gameTiles[1][0].getTileColor());
        textViewTwoTwo.setBackgroundColor(gameTiles[1][1].getTileColor());
        textViewTwoThree.setBackgroundColor(gameTiles[1][2].getTileColor());
        textViewTwoFour.setBackgroundColor(gameTiles[1][3].getTileColor());
        textViewThreeOne.setBackgroundColor(gameTiles[2][0].getTileColor());
        textViewThreeTwo.setBackgroundColor(gameTiles[2][1].getTileColor());
        textViewThreeThree.setBackgroundColor(gameTiles[2][2].getTileColor());
        textViewThreeFour.setBackgroundColor(gameTiles[2][3].getTileColor());
        textViewFourOne.setBackgroundColor(gameTiles[3][0].getTileColor());
        textViewFourTwo.setBackgroundColor(gameTiles[3][1].getTileColor());
        textViewFourThree.setBackgroundColor(gameTiles[3][2].getTileColor());
        textViewFourFour.setBackgroundColor(gameTiles[3][3].getTileColor());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                rollback();
                break;
            case R.id.button_refresh:
                score = 0;
                isGameWon = false;
                isGameLost = false;
                resetGameTiles();
                break;
            case R.id.button_hack:
                randomMove();
                break;
        }
    }

    // метод сохраняет состояние игрового поля и очков
    private void saveState(Tile[][] tiles) {
        Tile[][] tempGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempGameTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }
        int tempScore = score;
        previousStates.push(tempGameTiles);
        previousScores.push(tempScore);
        isSaveNeeded = false;
    }

    // метод восстанавливает предыдущее состояние игрового поля и очков
    public void rollback() {
        if (!previousStates.empty() && !previousScores.empty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
        repaint();
    }

    // геттер для поля gameTiles
    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    // метод возвращает true, если возможен ход
    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) {
            return true;
        }
        return false;
    }

    // метод движения влево
    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean hasChanges = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i]))
                hasChanges = true;
        }
        if (hasChanges) {
            addTile();
        }
        isSaveNeeded = true;
        repaint();
    }

    // метод движения вправо
    public void right() {
        saveState(gameTiles);
        rotation();
        rotation();
        left();
        rotation();
        rotation();
        repaint();
    }

    // метод движения вверх
    public void up() {
        saveState(gameTiles);
        rotation();
        rotation();
        rotation();
        left();
        rotation();
        repaint();
    }

    // метод движения вниз
    public void down() {
        saveState(gameTiles);
        rotation();
        left();
        rotation();
        rotation();
        rotation();
        repaint();
    }

    // метод "поворачивает" массив gameTiles на 90 градусов по часовой стрелке
    public void rotation() {
        Tile[][] temp = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        temp[0][3] = gameTiles[0][0];
        temp[1][3] = gameTiles[0][1];
        temp[2][3] = gameTiles[0][2];
        temp[3][3] = gameTiles[0][3];
        temp[0][2] = gameTiles[1][0];
        temp[1][2] = gameTiles[1][1];
        temp[2][2] = gameTiles[1][2];
        temp[3][2] = gameTiles[1][3];
        temp[0][1] = gameTiles[2][0];
        temp[1][1] = gameTiles[2][1];
        temp[2][1] = gameTiles[2][2];
        temp[3][1] = gameTiles[2][3];
        temp[0][0] = gameTiles[3][0];
        temp[1][0] = gameTiles[3][1];
        temp[2][0] = gameTiles[3][2];
        temp[3][0] = gameTiles[3][3];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = temp[i][j];
            }
        }
    }

    // метод рандомного хода в игре
    public void randomMove() {
        int move = ((int) (Math.random() * 100)) % 4;
        if (move == 0) {
            left();
        }
        if (move == 1) {
            right();
        }
        if (move == 2) {
            up();
        }
        if (move == 3) {
            down();
        }
        repaint();
    }

    // метод "сжатия плиток"
    private boolean compressTiles(Tile[] tiles) {
        boolean isCompressed = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            for (int j = 0; j < tiles.length - 1; j++) {
                if (tiles[j].isEmpty() && !tiles[j + 1].isEmpty()) {
                    tiles[j] = tiles[j + 1];
                    tiles[j + 1] = new Tile();
                    isCompressed = true;
                }
            }
        }
        return isCompressed;
    }

    // метод "слияния плиток" - изменяет поля score и maxTile
    private boolean mergeTiles(Tile[] tiles) {  //Слияние плиток одного номинала
        boolean isMerges = false;
        for (int j = 0; j < tiles.length - 1; j++) {
            if (tiles[j].value == tiles[j + 1].value && !tiles[j].isEmpty() && !tiles[j + 1].isEmpty()) {
                tiles[j].value = tiles[j].value * 2;
                isMerges = true;
                score += tiles[j].value;
                maxTile = maxTile > tiles[j].value ? maxTile : tiles[j].value;
                tiles[j + 1] = new Tile();
                compressTiles(tiles);
            }
        }
        return isMerges;
    }

    // метод для заполнение массива gameTiles объектами "плитка"
    void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
        repaint();
    }

    // метод должен возвращать список пустых плиток в массиве gameTiles
    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    emptyTiles.add(gameTiles[i][j]);
                }
            }
        }
        return emptyTiles;
    }

    // метод должен изменять значение случайной пустой плитки в массиве gameTiles на 2 или 4 с вероятностью 0.9 и 0.1 соответственно
    private void addTile() {
        if (getEmptyTiles().size() != 0) {
            int randomIndex = (int) (getEmptyTiles().size() * Math.random());
            getEmptyTiles().get(randomIndex).value = Math.random() < 0.9 ? 2 : 4;
        }
    }
}
