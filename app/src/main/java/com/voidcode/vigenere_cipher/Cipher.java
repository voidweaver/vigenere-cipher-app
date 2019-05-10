package com.voidcode.vigenere_cipher;

public class Cipher {
    private char[][] enc_chart;
    private char[][] dec_chart;
    private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private boolean skip_punc = true;
    private boolean restart_count = false;

    public static int mod(int i , int j){
        while(i < 0) i += j;
        return i % j;
    }

    Cipher() {
        init();
    }

    Cipher(boolean skip_punc, boolean restart_count, String alphabet){
        this.skip_punc = skip_punc;
        this.restart_count = restart_count;
        ALPHABET = alphabet;

        init();
    }

    private void init(){
        int length = ALPHABET.length();
        enc_chart = new char[length][length];
        dec_chart = new char[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                enc_chart[i][j] = ALPHABET.charAt((j + i) %length);
                dec_chart[i][j] = ALPHABET.charAt(mod((j - i), length));
            }
        }
    }

    public String encrypt(String lines, String key) {
        String result = "";

        key = key.toUpperCase();
        int index = 0;

        for (int i = 0; i < lines.length(); i++) {
            char c = lines.charAt(i);
            char k = key.charAt(index++ % key.length());
            boolean isCap = Character.isUpperCase(c);

            if (ALPHABET.contains(String.valueOf(c)) || ALPHABET.contains(String.valueOf(Character.toUpperCase(c)))) {
                int c_index = isCap ? ALPHABET.indexOf(c) : ALPHABET.indexOf(Character.toUpperCase(c));
                int k_index = ALPHABET.indexOf(k);

                char new_char = enc_chart[k_index][c_index];

                result += isCap ? new_char : Character.toLowerCase(new_char);
            }else {
                if(skip_punc) index--;

                result += c;
            }

            if(restart_count && c == '\n'){
                index = 0;
            }
        }
        return result;
    }

    public String decrypt(String lines, String key) {
        String result = "";

        key = key.toUpperCase();
        int index = 0;

        for (int i = 0; i < lines.length(); i++) {      //for each char
            char c = lines.charAt(i);
            char k = key.charAt(index++ % key.length());
            boolean isCap = Character.isUpperCase(c);

            if (ALPHABET.contains(String.valueOf(c)) || ALPHABET.contains(String.valueOf(Character.toUpperCase(c)))) {
                int c_index = isCap ? ALPHABET.indexOf(c) : ALPHABET.indexOf(Character.toUpperCase(c));
                int k_index = ALPHABET.indexOf(k);

                char new_char = dec_chart[k_index][c_index];

                result += isCap ? new_char : Character.toLowerCase(new_char);
            }else {
                if(skip_punc) index--;

                result += c;
            }

            if(restart_count && c == '\n'){
                index = 0;
            }
        }
        return result;
    }

}
