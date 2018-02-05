package org.ziemer.simbledb;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BinaryHashIndexedDb implements IndexedDb {

    private final String MAP_NAME = "mapfile";
    private RandomAccessFile db;
    private Map<String, Long> map;

    private File mapFile;
    private File dbFile;

    public BinaryHashIndexedDb(String dbName) {
        this.dbFile = new File(dbName);
        this.mapFile = new File(MAP_NAME);
    }

    public BinaryHashIndexedDb(String dbName, String mapName) {
        this.dbFile = new File(dbName);
        this.mapFile = new File(mapName);
    }

    @Override
    public void run() throws IOException, ClassNotFoundException {
        this.dbFile.createNewFile();
        this.db = new RandomAccessFile(this.dbFile, "rw");
        map = load();
    }

    @Override
    public void write(String key, String value) throws IOException {
        String data = value;
        byte[] byteArray = data.getBytes(StandardCharsets.UTF_8);

        // Save the place we are going to save our data, in our HashMap
        // This is not smart, as we will still have multiple entries in our actual db file
        map.put(key, db.getFilePointer());

        // Convert the value to binary
        StringBuilder byteData = new StringBuilder();
        for (byte b : byteArray) {
            byteData.append(Integer.toBinaryString(b).replaceFirst("0*", "").concat(" "));
        }
        // Write the actual data and a line seperator specific to the system it gets saved on
        // THIS IS NOT A GOOD IDEA as you can't move the DB from one system to another
        db.writeBytes(byteData.toString());
        db.writeBytes(System.getProperty("line.separator"));
    }

    @Override
    public void writeAndPersist(String key, String value) throws IOException {
        write(key, value);
        saveHashMap();
    }

    @Override
    public String read(String key) throws IOException {
        // Set pointer to where the data is stored
        db.seek(map.get(key));
        String dataLine = db.readLine();

        // Replace key with nothing
        String[] lines = dataLine.split("\\s+");

        // Convert the binary string to a human readable value
        StringBuilder data = new StringBuilder();
        for (String line : lines) {
            data.append((char) Integer.parseInt(line, 2));
        }

        return data.toString();
    }

    @Override
    public void delete(String key) throws IOException {
        map.remove(key);
    }

    @Override
    public void shutdown() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(this.mapFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeObject(this.map);

        objectOutputStream.close();
        fileOutputStream.close();
    }

    /**
     * Actually just calls the shutdown method as there's no difference as of now
     * @throws IOException
     */
    private void saveHashMap() throws IOException {
        shutdown();
    }

    /**
     * Loads the HashMap containing the pointers the the data in the DB file
     * @return
     * @throws IOException
     */
    private Map<String, Long> load() throws IOException {
        if (mapFile.exists()) {
            HashMap<String, Long> hashmap = new HashMap<>();

            for (Long i = 0L; i < db.length(); i = db.getFilePointer()) {
                Long pointer = db.getFilePointer();
                // Save the key and the pointer to the hashmap
                hashmap.put(db.readLine().split(",")[0], pointer);
            }
            return hashmap;
        } else {
            mapFile.createNewFile();
            return new HashMap<>();
        }
    }
}
