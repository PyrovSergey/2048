package com.example.pyrov.a2048;

// класс плитки
public class Tile {

    // вес плитки (ее значение)
    int value;

    // конструтор с параметром веса
    public Tile(int value) {
        this.value = value;
    }

    // конструктор без параметра
    public Tile() {
        value = 0;
    }

    // метод возвращающий true в случае, если значение поля value равно 0, иначе — false
    boolean isEmpty() {
        if (value == 0) {
            return true;
        }
        return false;
    }

    // метод возвращающий цвет шрифта в случае, если "вес" плитки меньше 16 #776E65, иначе — #F9F6F2
    int getFontColor() {
        if (value < 16) {
            return R.color.fontColorDark;
        }
        return R.color.fontColorWhite;
    }

    // метод getTileColor, возвращающий цвет плитки в зависимости от ее веса в соответствии с нижеприведенными значениями
    int getTileColor() {
        if (value == 0) {
            return R.color.colorTileValue_0;
        }
        if (value == 2) {
            return R.color.colorTileValue_2;
        }
        if (value == 4) {
            return R.color.colorTileValue_4;
        }
        if (value == 8) {
            return R.color.colorTileValue_8;
        }
        if (value == 16) {
            return R.color.colorTileValue_16;
        }
        if (value == 32) {
            return R.color.colorTileValue_32;
        }
        if (value == 64) {
            return R.color.colorTileValue_64;
        }
        if (value == 128) {
            return R.color.colorTileValue_128;
        }
        if (value == 256) {
            return R.color.colorTileValue_256;
        }
        if (value == 512) {
            return R.color.colorTileValue_512;
        }
        if (value == 1024) {
            return R.color.colorTileValue_1024;
        }
        if (value == 2048) {
            return R.color.colorTileValue_2048;
        }
        return R.color.colorTileValueOther;
    }
}
