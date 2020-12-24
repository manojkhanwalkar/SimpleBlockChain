package wal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DataRecordWriter {

   // String fileName ; //= "/home/manoj/data/aero/datafile";

    long position;

    public long getPosition() {
        return position;
    }

    RandomAccessFile writer;

    public DataRecordWriter(String fileName)
    {
        //this.fileName = fileName;
        try {
            writer = new RandomAccessFile(fileName,"rw");
            position = writer.length();
            writer.seek(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataRecordWriter(File file)
    {

        try {
            writer = new RandomAccessFile(file,"rw");
            position = writer.length();
            writer.seek(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public synchronized void write(String  walString)
    {
        try {
            writer.writeChars(walString);
            position = writer.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public synchronized void close()
    {
        try {
            writer.writeChar(WALRecord.EOF);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
