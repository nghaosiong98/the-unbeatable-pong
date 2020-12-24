// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;

public class IO
{
    public static final String DIRECTORY = "myData/";
    
    public static boolean saveFile(final String fileName, final String data, final boolean append) {
        try {
            final FileOutputStream outS = new FileOutputStream("myData/" + fileName, append);
            final PrintWriter pw = new PrintWriter(outS);
            pw.println(data);
            pw.flush();
            outS.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static String loadFile(final String fileName) {
        final StringBuffer data = new StringBuffer();
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("myData/" + fileName)));
            for (String input = br.readLine(); input != null; input = br.readLine()) {
                if (!input.equals("")) {
                    data.append(input + "\n");
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return data.toString();
    }
}
