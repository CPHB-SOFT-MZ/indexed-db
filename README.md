# indexed-db
This is a simple indexed database saving strings of data.
If you want to save Objects you have to serialize and deserialize them yourself.
The library stores a pointer to the value in the DB in a hashmap. 
The only thing it loads when starting up is the hashmap, which is why this DB has a worst case speed of O(1)

## Prerequisites
- You need to have at least Java 8 installed
- You need to have Apache Maven installed as well

## Getting started
1. Clone this project
2. From the commandline `cd` into the project folder and run `mvn clean install`
    2. If you're having trouble with installing the project you can run:<br/>
    `docker run -it --rm --name indexed-db -v "$(PWD)":/indexed-db -v "$HOME/.m2":/root/.m2 -w /indexed-db maven:3.5-jdk-8 mvn clean install`<br/>
    from the project folder. This command is expecting your .m2 directory to be directly in your HOME directory
    
3. Create a new Maven project, either from a template or just a basic empty one
4. In your pom.xml file, copy and paste in this snippet in your `dependencies` tag:

```xml
<dependency>
    <groupId>org.ziemer.db</groupId>
    <artifactId>indexed-db</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```
5. You might have to `cd` into your own projects folder and run `mvn clean install`, depending on your OS and/or IDE
6. indexed-db should now be a part of your project

## Usage
The project exposes a single interface called `IndexedDb` and have a single implementation called `BinaryHashIndexedDb`.

Example of usage: 

```java
public class Example {
  private IndexedDb indexedDb;
  
  public void useDb() {
    this.indexedDb = new BinaryHashIndexedDb("path/to/my-desired-db-name");
    this.indexedDb.run();
    
    // Unsafe operation as the map with pointers doesn't get saved
    this.indexedDb.write("key", "value");
    
    // Safe write operation as it saves the map to a file as it writes
    this.indexedDb.writeAndPersist("key2", "value2");
   
    
    System.out.println(this.indexedDb.read("key")); // prints out "value"
    System.out.println(this.indexedDb.read("key2")); // prints out "value2"
    
    // This is important, as it saves the indexes to a file, which gets loaded on next startup.
    this.indexedDb.shutdown();
  }
}
```
