package org.o7planning.Ecorridor_DriverDNA.io;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogJSONWriter {

    public static void addLineFile (String line) throws IOException {
        if (line != null) {
            long mils = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_HH_mm_ss");

            File fileFileWriter = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + sdf.format(new Date(mils)).toString() + ".json"));
            try {
                FileWriter fileWriter = new FileWriter(fileFileWriter, true);
                fileWriter.write(line);
                fileWriter.close();
                Log.e("wwww", "Entered onStart...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}