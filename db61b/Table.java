package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Ryan Riddle & P. N. Hilfinger
 */
class Table implements Iterable<Row> {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain dupliace names. */
    Table(String[] columnTitles) {
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _title = new Row(columnTitles);
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _title.size();
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _title.get(k);
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < _title.size(); i++) {
            if (title.equals(_title.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    public int size() {
        return _rows.size();
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    public boolean add(Row row) {
        if (this.columns() != row.size())   {
            return false;
        }
        if (_rows.contains(row)) {
            return false;
        }
        _rows.add(row);
        return true;
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String newrow = input.readLine();
            while (newrow != null) {
                String[] row = newrow.split(",");
                table._rows.add(new Row(row));
                newrow = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            if (this.columns() == 0) {
                throw error("table needs at least one column");
            }
            sep += this.getTitle(0).trim();
            for (int i = 1; i < this.columns(); i++) {
                sep += ",";
                sep += this.getTitle(i).trim();
            }
            output.append(sep);
            for (Row row : this) {
                output.append("\n");
                sep = row.get(0).trim();
                for (int i = 1; i < row.size(); i++) {
                    sep += ",";
                    sep += row.get(i).trim();
                }
                output.append(sep);
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output. */
    void print() {
        for (Row row : this) {
            System.out.print(" ");
            for (int i = 0; i < row.size(); i++) {
                System.out.print(" " + row.get(i));
            }
            System.out.println();
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        int index;
        for (Row row : this) {
            if (Condition.test(conditions, row)) {
                String[] temp = new String[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    index = this.findColumn(columnNames.get(i));
                    if (index == -1) {
                        throw error("Column %s doesn't exist",
                            columnNames.get(i));
                    }
                    temp[i] = row.get(index);
                }
                result.add(new Row(temp));
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        ArrayList<Column> common1 = new ArrayList<Column>();
        ArrayList<Column> common2 = new ArrayList<Column>();
        int index;
        for (int i = 0; i < this.columns(); i++) {
            String name = this.getTitle(i);
            if (table2.findColumn(name) != -1) {
                common1.add(new Column(name, this));
                common2.add(new Column(name, table2));
            }
        }
        for (Row row1 : this) {
            Iterator row2 = table2.iterator();
            while (row2.hasNext()) {
                Row rowtwo = (Row) row2.next();
                if (equijoin(common1, common2, row1, rowtwo)) {
                    if (Condition.test(conditions, row1, rowtwo)) {
                        String[] temp = new String[columnNames.size()];
                        for (int i = 0; i < columnNames.size(); i++) {
                            index = this.findColumn(columnNames.get(i));
                            if (index == -1) {
                                index = table2.findColumn(columnNames.get(i));
                                if (index == -1) {
                                    throw error("Column %s doesn't exist",
                                        columnNames.get(i));
                                }
                                temp[i] = rowtwo.get(index);
                            } else {
                                temp[i] = row1.get(index);
                            }
                        }
                        result.add(new Row(temp));
                    }
                }
            }
        }
        return result;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 come, respectively,
     *  from those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {
        if (common1.size() != common2.size()) {
            throw error("Columns are not the same size");
        }
        for (Column column1 : common1) {
            for (Column column2 : common2) {
                if ((column1.getName()).equals(column2.getName())) {
                    if (!(column1.getFrom(row1).equals(
                        column2.getFrom(row2)))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** My rows. */
    private HashSet<Row> _rows = new HashSet<>();

    /** The column names. */
    private Row _title;
}

