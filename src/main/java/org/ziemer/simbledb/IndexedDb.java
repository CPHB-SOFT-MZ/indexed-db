package org.ziemer.simbledb;

import java.io.IOException;

/**
 * Ideally an implementation should store two hashmaps
 */
public interface IndexedDb {
    /**
     * Made to prevent thrown exceptions from constructor.
     * Must be called before anything else
     * @throws IOException
     * @throws ClassNotFoundException
     */
    void run() throws IOException, ClassNotFoundException;

    /**
     * Writes to the DB with a simple String key and value
     * Updates the map as well, but only in memory
     * @param key
     * @param value
     * @throws IOException
     */
    void write(String key, String value) throws IOException;

    /**
     * Does the same as write, except it also persists the map file
     * @param key
     * @param value
     * @throws IOException
     */
    void writeAndPersist(String key, String value) throws IOException;

    /**
     * Read a value from the file based on the index stored in the map
     * @param key
     * @return
     * @throws IOException
     */
    String read(String key) throws IOException;

    /**
     * Delete an entry in the map only
     * @param key
     * @throws IOException
     */
    void delete(String key) throws IOException;

    /**
     * Shutdown the database gracefully and write the index map to a file.
     * @throws IOException
     */
    void shutdown() throws IOException;
}
