import org.junit.*;
import org.ziemer.simbledb.IndexedDb;
import org.ziemer.simbledb.BinaryHashIndexedDb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class IndexedDbTest {

    private IndexedDb indexedDb;
    private String fileName;
    private String mapName;

    @Before
    public void setup() throws IOException, ClassNotFoundException {
        this.fileName = UUID.randomUUID().toString();
        this.mapName = UUID.randomUUID().toString();
        this.indexedDb = new BinaryHashIndexedDb(this.fileName);
        this.indexedDb.run();
    }

    @After
    public void destroy() throws IOException {
        Files.deleteIfExists(Paths.get(this.fileName));
        Files.deleteIfExists(Paths.get(this.mapName));
    }

    @Test
    public void testDb() throws IOException {
        this.indexedDb.write("1", "value 1");
        this.indexedDb.write("2", "value 2");
        this.indexedDb.write("3", "value 3");

        Assert.assertEquals("value 1", this.indexedDb.read("1"));
        Assert.assertEquals("value 2", this.indexedDb.read("2"));
        Assert.assertEquals("value 3", this.indexedDb.read("3"));

        this.indexedDb.write("2", "value24");
        Assert.assertEquals("value24", this.indexedDb.read("2"));

        this.indexedDb.write("2", "99");
        Assert.assertEquals("99", this.indexedDb.read("2"));
    }
}
