package com.example.pyrov.a2048;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {

    // текущий счет (очки)
    private int score;
    // максимальный вес плитки (Tile.value)
    private int maxTile;
    // константа, определяющая ширину игрового поля
    private static final int FIELD_WIDTH = 4;

    @BindView(R.id.score)
    TextView textViewScore;
    @BindView(R.id.info)
    LinearLayout info;
    @BindView(R.id.game_over)
    TextView textViewGameOver;

    @BindViews({R.id.one_one, R.id.one_two, R.id.one_three, R.id.one_four,
            R.id.two_one, R.id.two_two, R.id.two_three, R.id.two_four,
            R.id.three_one, R.id.three_two, R.id.three_three, R.id.three_four,
            R.id.four_one, R.id.four_two, R.id.four_three, R.id.four_four})
    List<TextView> listTilesView;

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.button_refresh)
    ImageButton imageButtonRefresh;
    @BindView(R.id.button_back)
    ImageButton imageButtonBack;
    @BindView(R.id.button_hack)
    ImageButton imageButtonHack;
    @BindView(R.id.button_best_move)
    ImageButton imageButtonBestMove;
    @BindView(R.id.playing_field)
    RelativeLayout playField;

    // двумерный массив, состоящий из объектов класса Tile
    private Tile[][] gameTiles;
    // булево - сохранить игру?
    private boolean isSaveNeeded = true;
    // стэк для хранения предыдущего состояния поля
    private Stack<Tile[][]> previousStates = new Stack<>();
    // стэк для хранения предыдущего состояния очков
    private Stack<Integer> previousScores = new Stack<>();
    // булево игра выиграна?
    private boolean isGameWon = false;
    // булево игра проиграна?
    private boolean isGameLost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

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
    private void repaint() {
        textViewScore.setText(String.valueOf(score));
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                listTilesView.get(i * 4 + j).setText(String.valueOf(gameTiles[i][j].value == 0 ? "" : gameTiles[i][j].value));
                listTilesView.get(i * 4 + j).setTextColor(getResources().getColor(gameTiles[i][j].getFontColor()));
                listTilesView.get(i * 4 + j).setBackgroundColor(getResources().getColor(gameTiles[i][j].getTileColor()));
            }
        }
    }

    @OnClick({R.id.button_refresh, R.id.button_back, R.id.button_hack, R.id.button_best_move})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_refresh:
                refreshGame();
                break;
            case R.id.button_back:
                rollback();
                break;
            case R.id.button_hack:
                if (!canMove()) {
                    return;
                }
                randomMove();
                break;
            case R.id.button_best_move:
                bestMove();
                break;
        }
    }

    private void refreshGame() {
        textViewGameOver.setVisibility(View.GONE);
        previousScores.clear();
        previousStates.clear();
        score = 0;
        isGameWon = false;
        isGameLost = false;
        resetGameTiles();
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
    private void rollback() {
        if (!canMove()) {
            return;
        }
        if (!previousStates.empty() && !previousScores.empty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
        repaint();
    }

    // геттер для поля gameTiles
    private Tile[][] getGameTiles() {
        return gameTiles;
    }

    // метод возвращает true, если возможен ход
    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) {
            return true;
        }
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length - 1; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j + 1].value) {
                    return true;
                }
            }
        }
        for (int j = 0; j < gameTiles.length; j++) {
            for (int i = 0; i < gameTiles.length - 1; i++) {
                if (gameTiles[i][j].value == gameTiles[i + 1][j].value) {
                    return true;
                }
            }
        }
        textViewGameOver.setVisibility(View.VISIBLE);
        return false;
    }

    // метод движения влево
    private void left() {
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
    private void right() {
        saveState(gameTiles);
        rotation();
        rotation();
        left();
        rotation();
        rotation();
        repaint();
    }

    // метод движения вверх
    private void up() {
        saveState(gameTiles);
        rotation();
        rotation();
        rotation();
        left();
        rotation();
        repaint();
    }

    // метод движения вниз
    private void down() {
        saveState(gameTiles);
        rotation();
        left();
        rotation();
        rotation();
        rotation();
        repaint();
    }

    // метод "поворачивает" массив gameTiles на 90 градусов по часовой стрелке
    private void rotation() {
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

        gameTiles = temp;

    }

    // метод рандомного хода в игре
    private void randomMove() {
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

    // метод для лучшего хода в игре
    private void bestMove() {
        autoMove();
        repaint();
    }

    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency;
        move.move();
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        } else {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }
        rollback();

        return moveEfficiency;
    }

    private void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));

        priorityQueue.peek().getMove().move();
    }

    private boolean hasBoardChanged() {
        int sum1 = 0;
        int sum2 = 0;
        if (!previousStates.isEmpty()) {
            Tile[][] prevGameTiles = previousStates.peek();
            for (int i = 0; i < FIELD_WIDTH; i++) {
                for (int j = 0; j < FIELD_WIDTH; j++) {
                    sum1 += gameTiles[i][j].value;
                    sum2 += prevGameTiles[i][j].value;
                }
            }
        }
        return sum1 != sum2;
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
    private void resetGameTiles() {
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
