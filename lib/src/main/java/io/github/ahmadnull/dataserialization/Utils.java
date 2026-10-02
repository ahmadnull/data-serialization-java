package io.github.ahmadnull.dataserialization;

class Utils {
    static String repeatChar(char c, int repeat) {
        String result = "";
        for (int i = 0; i < repeat; i++)
            result += c;
        return result;
    }

    static String repeatSpace(int repeat) {
        return repeatChar(' ', repeat);
    }
}
