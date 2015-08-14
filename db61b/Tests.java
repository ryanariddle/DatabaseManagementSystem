package db61b;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

    /** Tests basics functionality including:
     * 1. The row class
     * 2. The table class
     * 3. The database class
     * 4. The condition class
     * 5. The column class
     * 6. The table select methods
     * 7. The equijoin method
     * I also wrote an addittional in/out test
     * @author Ryan Riddle
     */

public class Tests {


    /** Tests the constructor, size, get,
     * and equals method in the Row class.
     * The second constructor is tested in
     * the column test.
     */

    @Test
    public void testRow() {
        Row r = new Row(new String[]{"Ryan", "is",
            "awesome", "at", "everything", "except", "java"});
        assertEquals(7, r.size());
        assertEquals("Ryan", r.get(0));
        assertEquals("everything", r.get(4));
        Row same = new Row(new String[]{"Ryan", "is",
            "awesome", "at", "everything", "except", "java"});
        Row different1 = new Row(new String[]{"why", "cant",
            "insects", "see", "spiderwebs"});
        Row different2 = new Row(new String[]{"i", "ate",
            "a", "lot", "of", "food", "today"});
        assertEquals(true, r.equals(same));
        assertEquals(false, r.equals(different1));
        assertEquals(false, r.equals(different2));
    }

    /** Tests the constructor, columns, getTitle,
     * findColumn, size, add, and print method of
     * the Table class. I test both select methods
     * and equijoin seperatly later.
     *
     * Verify that tt and r print the same thing.
     */

    @Test
    public void testTable() {
        Table t = new Table(new String[]{"Col1", "Col2", "Col3"});
        assertEquals(3, t.columns());
        assertEquals("Col1", t.getTitle(0));
        assertEquals(1, t.findColumn("Col2"));
        assertEquals(-1, t.findColumn("NotAColumn"));
        assertEquals(0, t.size());
        assertEquals(true, t.add(new Row(new String[]{"First item",
            "Second item", "Third item"})));
        assertEquals(false, t.add(new Row(new String[]{"First item",
            "Second item", "Third item"})));
        assertEquals(1, t.size());
        t.add(new Row(new String[]{"1", "2", "3"}));
        Table r = new Table(new String[]{"Col1", "Col3"});
        r.add(new Row(new String[]{"First item", "Third item"}));
        r.add(new Row(new String[]{"1", "3"}));
        ArrayList<String> temp = new
            ArrayList<String>(Arrays.asList("Col1", "Col3"));
        ArrayList<Condition> empty = new ArrayList<Condition>();
        Table tt = t.select(temp, empty);
        tt.print();
        r.print();
        Table s = new Table(new String[]{"Col3", "Col4", "Col5"});
        s.add(new Row(new String[]{"Third item", "Fourth item",
            "Fifth item"}));
        s.add(new Row(new String[]{"3", "4", "5"}));
        ArrayList<Column> rowConstructor = new ArrayList<Column>();
        rowConstructor.add(new Column("Col4", s, t));
        Row newRow1 = new Row(rowConstructor, new Row(new String[]{"Third item",
            "Fourth item", "Fifth item"}));
        assertEquals(new Row(new String[]{"Fourth item"}), newRow1);
        rowConstructor.add(new Column("Col1", s, t));
        Row newRow2 = new Row(rowConstructor, new Row(new String[]{"Third item",
            "Fourth item", "Fifth item"}), new Row(new String[]{"First item",
                "Second item", "Third item"}));
        assertEquals(new Row(new String[]{"Fourth item",
            "First item"}), newRow2);
    }

    /** Tests constructor, get, and put method of the Database Class. */

    @Test
    public void testDatabase() {
        Database d = new Database();
        assertNull(d.get("Wut"));
        Table t = new Table(new String[]{"Game", "Of", "Thrones"});
        d.put("First", t);
        assertEquals(t, d.get("First"));
        Table r = new Table(new String[]{"Why"});
        d.put("First", r);
        assertEquals(r, d.get("First"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDatabase2() {
        Database d = new Database();
        Table t = new Table(new String[]{"Game", "Of", "Thrones"});
        d.put(null, t);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDatabase3() {
        Database d = new Database();
        d.put("null", null);
    }

    /** Tests both constructors, and tests in the Condition Class. */

    @Test
    public void testCondition() {
        Table s = new Table(new String[]{"Col3", "Col4", "Col5"});
        Row r1 = new Row(new String[]{"3", "4", "5"});
        s.add(r1);
        Table t = new Table(new String[]{"Col1", "Col2", "Col3"});
        Row rr1 = new Row(new String[]{"1", "2", "3"});
        t.add(rr1);
        Column col1 = new Column("Col4", s, t);
        String relation = ">";
        Column col2 = new Column("Col1", s, t);
        Condition condition1 = new Condition(col1, relation, col2);
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(condition1);
        assertEquals(true, Condition.test(conditions, r1, rr1));
        Condition condition2 = new Condition(col1, "=", "4");
        assertEquals(true, Condition.test(conditions, r1, rr1));
    }

    @Test(expected = Throwable.class)
    public void testCondition2() {
        Table s = new Table(new String[]{"Col3", "Col4", "Col5"});
        Row r1 = new Row(new String[]{"3", "4", "5"});
        s.add(r1);
        Table t = new Table(new String[]{"Col1", "Col2", "Col3"});
        Row rr1 = new Row(new String[]{"1", "2", "3"});
        t.add(rr1);
        Column col1 = new Column("Col4", s, t);
        String relation = "8";
        Column col2 = new Column("Col1", s, t);
        Condition condition1 = new Condition(col1, relation, col2);
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        conditions.add(condition1);
        assertEquals(true, Condition.test(conditions, r1, rr1));
    }


    /** Tests the constructor, getName, and
     * getFrom methods of the Column Class.
     */

    @Test
    public void testColumn() {
        Table t = new Table(new String[]{"Col1", "Col2", "Col3"});
        t.add(new Row(new String[]{"First item", "Second item", "Third item"}));
        t.add(new Row(new String[]{"1", "2", "3"}));
        Table s = new Table(new String[]{"Col3", "Col4", "Col5"});
        s.add(new Row(new String[]{"Third item", "Fourth item", "Fifth item"}));
        s.add(new Row(new String[]{"3", "4", "5"}));
        ArrayList<Column> rowConstructor = new ArrayList<Column>();
        Column col4 = new Column("Col4", s, t);
        rowConstructor.add(col4);
        assertEquals("Col4", col4.getName());
        assertEquals("Fourth item", col4.getFrom(new Row(new String[]{
            "Third item", "Fourth item", "Fifth item"})));
        Row newRow1 = new Row(rowConstructor, new Row(new String[]{"Third item",
            "Fourth item", "Fifth item"}));
        assertEquals(new Row(new String[]{"Fourth item"}), newRow1);
        rowConstructor.add(new Column("Col1", s, t));
        Row newRow2 = new Row(rowConstructor, new Row(new String[]{"Third item",
            "Fourth item", "Fifth item"}), new Row(new String[]{"First item",
                "Second item", "Third item"}));
        assertEquals(new Row(new String[]{"Fourth item",
            "First item"}), newRow2);
    }

    /** Tests the equijoin method from the Table class. */

    @Test
    public void testEquijoin() {
        Table t = new Table(new String[]{"Col1", "Col2", "Col3"});
        Row tr = new Row(new String[]{"1", "2", "3"});
        t.add(tr);
        Table u = new Table(new String[]{"Col2", "Col3", "Col4", "Col5"});
        Row ur = new Row(new String[]{"2", "3", "4", "5"});
        u.add(ur);
        ArrayList<Column> tc = new ArrayList<Column>();
        ArrayList<Column> uc = new ArrayList<Column>();
        tc.add(new Column("Col2", t));
        tc.add(new Column("Col3", t));
        uc.add(new Column("Col2", u));
        uc.add(new Column("Col3", u));
        Table v = new Table(new String[]{"Col1", "Col2", "Col3"});
        Row vr = new Row(new String[]{"1", "2", "3"});
        v.add(vr);
        Table w = new Table(new String[]{"Col4", "Col5"});
        Row wr = new Row(new String[]{"B", "A"});
        w.add(wr);
        ArrayList<Column> vc = new ArrayList<Column>();
        ArrayList<Column> wc = new ArrayList<Column>();
        vc.add(new Column("Col1", v));
        vc.add(new Column("Col3", v));
        wc.add(new Column("Col4", w));
        wc.add(new Column("Col5", w));
    }
    /** assertEquals(true, Table.equijoin(tc, uc, tr, ur)); and
     * assertEquals(true, Table.equijoin(vc, wc, vr, wr));
     * hold when the access of the equijoin method in the
     * Table class is changed to public.
     */


    /** Tests the two select methods of the Table class. */

    @Test
    public void testTableSelect() {
        Table r = new Table(new String[]{"Col1", "Col2", "Col3"});
        Row r1 = new Row(new String[]{"1", "99", "A"});
        Row r2 = new Row(new String[]{"2", "98", "B"});
        r.add(r1);
        r.add(r2);
        Column c1 = new Column("Col1", r);
        Column c2 = new Column("Col3", r);
        ArrayList<String> names = new ArrayList<String>();
        names.add("Col1");
        names.add("Col3");
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        Condition cond1 = new Condition(c1, "=", "2");
        conditions.add(cond1);
        Table t = r.select(names, conditions);
        assertEquals(false, t.add(new Row(new String[]{"2", "B"})));
        assertEquals(1, t.size());

        Table s = new Table(new String[]{"Col1", "Col2", "Col3", "Col4"});
        Row s1 = new Row(new String[]{"A", "aa", "1", "99"});
        Row s2 = new Row(new String[]{"B", "bb", "99", "98"});
        Row s3 = new Row(new String[]{"C", "cc", "2", "98"});
        s.add(s1);
        s.add(s2);
        s.add(s3);
        Table u = new Table(new String[]{"Col4", "Col5"});
        Row u1 = new Row(new String[]{"99", "Yes"});
        Row u2 = new Row(new String[]{"98", "No"});
        Row u3 = new Row(new String[]{"46", "Yes"});
        u.add(u1);
        u.add(u2);
        u.add(u3);
        ArrayList<String> names2 = new ArrayList<String>();
        names2.add("Col5");
        t = u.select(s, names2, new ArrayList<Condition>());
        assertEquals(2, t.size());
        assertEquals(false, t.add(new Row(new String[]{"No"})));
        assertEquals(false, t.add(new Row(new String[]{"Yes"})));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Tests.class));
    }
}
