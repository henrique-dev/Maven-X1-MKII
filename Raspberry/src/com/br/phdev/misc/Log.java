package com.br.phdev.misc;

public class Log {

    public static void s(String msg) {
        System.out.println(Color.ANSI_GREEN + msg + Color.ANSI_RESET);
    }

    public static void e(String msg) {
        System.out.println(Color.ANSI_RED + msg + Color.ANSI_RESET);
    }

    public static void w(String msg) {
        System.out.println(Color.ANSI_YELLOW + msg + Color.ANSI_RESET);
    }

    public static void i(String msg) {
        System.out.println(Color.ANSI_CYAN + msg + Color.ANSI_RESET);
    }


    private class Color {

        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";

    }


}
