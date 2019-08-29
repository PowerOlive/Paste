package soptqs.paste.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by S0ptq on 2018/2/10.
 */

public class SU {

    private static SU su;
    private Process process;
    private BufferedWriter mOutputWriter;
    private BufferedReader mInputReader;
    private boolean closed;
    private boolean denied;
    private boolean firstTry;

    private SU() {
        try {
            firstTry = true;
            process = Runtime.getRuntime().exec("su");
            mOutputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            mInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException ignored) {
            denied = true;
            closed = true;
        }
    }

    public static SU getSU() {
        if (su == null || su.closed || su.denied) {
            if (su != null && !su.closed) {
                su.close();
            }
            su = new SU();
        }
        return su;
    }

    public static boolean isRooted() {
        SU.getSU().runCommand("");
        return !su.denied;
    }

    public synchronized String runCommand(final String command) {
        synchronized (this) {
            try {
                StringBuilder mStringBuilder = new StringBuilder();
                String callback = "/shellCallback/";
                mOutputWriter.write(command + "\necho " + callback + "\n");
                mOutputWriter.flush();

                String line;
                while ((line = mInputReader.readLine()) != null) {
                    if (line.equals(callback)) {
                        break;
                    }
                    mStringBuilder.append(line).append("\n");
                }
                firstTry = false;
                return mStringBuilder.toString().trim();
            } catch (IOException e) {
                closed = true;
                e.printStackTrace();
                if (firstTry) denied = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                denied = true;
            } catch (Exception e) {
                e.printStackTrace();
                denied = true;
            }
            return null;
        }
    }

    private void close() {
        try {
            mOutputWriter.write("exit\n");
            mOutputWriter.flush();
            process.waitFor();
            mOutputWriter.close();
            mInputReader.close();
            process.destroy();
            closed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
