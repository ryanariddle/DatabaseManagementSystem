package db61b;

import java.util.HashMap;
import java.util.Map;

/** A collection of Tables, indexed by name.
 *  @author Ryan Riddle */
class Database {
    /** An empty database. */
    public Database() {
        _database = new HashMap<String, Table>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        return _database.get(name);
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        _database.put(name, table);
    }

    /** The database where tables are stored. */
    private Map<String, Table> _database;
}
