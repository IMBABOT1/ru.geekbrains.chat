package ru.geekbrains.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log  {

    private FileWriter fw;
    private File file;

    public Log(File file){
        this.file = file;
    }


    public void read(String read){
        try {
            fw = new FileWriter(file, true);
            fw.write(  read + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
