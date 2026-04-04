package fr.framelab;

public class AppSession {
    private static boolean demoMode = false;

    public static boolean isDemoMode() { return demoMode; }
    public static void setDemoMode(boolean value) { demoMode = value; }
}
