package org.jfree.data;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.Values2D;

/**
 * JUnit 4 tests for {@link DataUtilities}.
 * Covers calculateColumnTotal, calculateRowTotal, createNumberArray,
 * createNumberArray2D, getCumulativePercentages, equal, and clone.
 */
public class DataUtilitiesTest {

    private Mockery context = new Mockery();

    // ---------- equal(double[][], double[][]) ----------

    @Test
    public void equalBothNullReturnsTrue() {
        assertTrue(DataUtilities.equal(null, null));
    }

    @Test
    public void equalFirstNullSecondNonNullReturnsFalse() {
        assertFalse(DataUtilities.equal(null, new double[][] { { 1.0 } }));
    }

    @Test
    public void equalSecondNullFirstNonNullReturnsFalse() {
        assertFalse(DataUtilities.equal(new double[][] { { 1.0 } }, null));
    }

    @Test
    public void equalDifferentLengthsReturnsFalse() {
        double[][] a = { { 1.0 } };
        double[][] b = { { 1.0 }, { 2.0 } };
        assertFalse(DataUtilities.equal(a, b));
    }

    @Test
    public void equalSameValuesReturnsTrue() {
        double[][] a = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        double[][] b = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        assertTrue(DataUtilities.equal(a, b));
    }

    @Test
    public void equalDifferentValuesReturnsFalse() {
        double[][] a = { { 1.0, 2.0 } };
        double[][] b = { { 1.0, 3.0 } };
        assertFalse(DataUtilities.equal(a, b));
    }

    // ---------- clone(double[][]) ----------

    @Test(expected = IllegalArgumentException.class)
    public void cloneNullSourceThrowsIllegalArgumentException() {
        DataUtilities.clone(null);
    }

    @Test
    public void cloneDeepCopyReturnsIndependentCopy() {
        double[][] source = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        double[][] result = DataUtilities.clone(source);
        assertNotNull(result);
        assertNotSame(source, result);
        assertEquals(2, result.length);
        assertNotSame(source[0], result[0]);
        assertArrayEquals(source[0], result[0], 1e-9);
        assertArrayEquals(source[1], result[1], 1e-9);
        result[0][0] = 99.0;
        assertEquals(1.0, source[0][0], 1e-9);
    }

    @Test
    public void clonePreservesNullRowInSource() {
        double[][] source = { { 1.0 }, null, { 3.0 } };
        double[][] result = DataUtilities.clone(source);
        assertNotNull(result);
        assertEquals(3, result.length);
        assertNotNull(result[0]);
        assertArrayEquals(new double[] { 1.0 }, result[0], 1e-9);
        assertNull(result[1]);
        assertNotNull(result[2]);
        assertArrayEquals(new double[] { 3.0 }, result[2], 1e-9);
    }

    // ---------- calculateColumnTotal(Values2D, int) ----------

    @Test
    public void calculateColumnTotalForTwoValues() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(2));
            oneOf(values).getValue(0, 0); will(returnValue(6));
            oneOf(values).getValue(1, 0); will(returnValue(3));
        }});
        double result = DataUtilities.calculateColumnTotal(values, 0);
        assertEquals(9.0, result, 1e-9);
    }

    @Test
    public void calculateColumnTotalForSingleRow() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(1));
            oneOf(values).getValue(0, 0); will(returnValue(5));
        }});
        double result = DataUtilities.calculateColumnTotal(values, 0);
        assertEquals(5.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateColumnTotalForEmptyTableIsZero() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(0));
        }});
        double result = DataUtilities.calculateColumnTotal(values, 0);
        assertEquals(0.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateColumnTotalForNegativeValues() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(2));
            oneOf(values).getValue(0, 0); will(returnValue(-2.0));
            oneOf(values).getValue(1, 0); will(returnValue(-3.0));
        }});
        double result = DataUtilities.calculateColumnTotal(values, 0);
        assertEquals(-5.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateColumnTotalForLargeNumbers() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(2));
            oneOf(values).getValue(0, 0); will(returnValue(1e9));
            oneOf(values).getValue(1, 0); will(returnValue(2e9));
        }});
        double result = DataUtilities.calculateColumnTotal(values, 0);
        assertEquals(3e9, result, 1e-6);
        context.assertIsSatisfied();
    }

    @Test
    public void columnTotalSkipsNulls() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(2));
            oneOf(values).getValue(0, 0); will(returnValue(null));
            oneOf(values).getValue(1, 0); will(returnValue(3.0));
        }});
        double total = DataUtilities.calculateColumnTotal(values, 0);
        assertEquals(3.0, total, 1e-9);
    }

    @Test
    public void columnTotalMultipleRowsAddsAll() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(3));
            oneOf(values).getValue(0, 0); will(returnValue(1.5));
            oneOf(values).getValue(1, 0); will(returnValue(2.0));
            oneOf(values).getValue(2, 0); will(returnValue(3.5));
        }});
        double total = DataUtilities.calculateColumnTotal(values, 0);
        assertEquals(7.0, total, 1e-9);
    }

    // ---------- calculateColumnTotal(Values2D, int, int[]) ----------

    @Test
    public void columnTotalWithValidRowsFiltersRows() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(4));
            oneOf(values).getValue(1, 1); will(returnValue(2.0));
            oneOf(values).getValue(3, 1); will(returnValue(4.0));
        }});
        int[] valid = { 1, 3 };
        double total = DataUtilities.calculateColumnTotal(values, 1, valid);
        assertEquals(6.0, total, 1e-9);
    }

    @Test
    public void columnTotalWithValidRowsSkipsOutOfBounds() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(2));
            oneOf(values).getValue(1, 1); will(returnValue(5.0));
        }});
        int[] valid = { 1, 5 };
        double total = DataUtilities.calculateColumnTotal(values, 1, valid);
        assertEquals(5.0, total, 1e-9);
    }

    @Test
    public void columnTotalWithValidRowsEmptyArrayReturnsZero() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(3));
        }});
        int[] valid = {};
        double total = DataUtilities.calculateColumnTotal(values, 0, valid);
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    public void columnTotalWithValidRowsCountsDuplicates() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getRowCount(); will(returnValue(2));
            exactly(2).of(values).getValue(1, 0);
            will(onConsecutiveCalls(returnValue(4.0), returnValue(4.0)));
        }});
        int[] valid = { 1, 1 };
        double total = DataUtilities.calculateColumnTotal(values, 0, valid);
        assertEquals(8.0, total, 1e-9);
    }

    // ---------- calculateRowTotal(Values2D, int) ----------

    @Test
    public void calculateRowTotalForTwoValues() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            allowing(values).getColumnCount(); will(returnValue(2));
            allowing(values).getValue(0, 0); will(returnValue(1.0));
            allowing(values).getValue(0, 1); will(returnValue(4.0));
        }});
        double result = DataUtilities.calculateRowTotal(values, 0);
        assertEquals(5.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateRowTotalForSingleColumnSingleRow() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            allowing(values).getColumnCount(); will(returnValue(1));
            oneOf(values).getValue(0, 0); will(returnValue(9.0));
        }});
        double result = DataUtilities.calculateRowTotal(values, 0);
        assertEquals(9.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateRowTotalForEmptyTableIsZero() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(0));
        }});
        double result = DataUtilities.calculateRowTotal(values, 0);
        assertEquals(0.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateRowTotalForNegativeValues() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(2));
            oneOf(values).getValue(0, 0); will(returnValue(-2.0));
            oneOf(values).getValue(0, 1); will(returnValue(-3.0));
        }});
        double result = DataUtilities.calculateRowTotal(values, 0);
        assertEquals(-5.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateRowTotalForLargeNumbers() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(2));
            oneOf(values).getValue(0, 0); will(returnValue(1e9));
            oneOf(values).getValue(0, 1); will(returnValue(2e9));
        }});
        double result = DataUtilities.calculateRowTotal(values, 0);
        assertEquals(3e9, result, 1e-6);
        context.assertIsSatisfied();
    }

    @Test
    public void rowTotalAddsAcrossColumns() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(3));
            oneOf(values).getValue(0, 0); will(returnValue(1.0));
            oneOf(values).getValue(0, 1); will(returnValue(2.0));
            oneOf(values).getValue(0, 2); will(returnValue(3.0));
        }});
        double total = DataUtilities.calculateRowTotal(values, 0);
        assertEquals(6.0, total, 1e-9);
    }

    @Test
    public void rowTotalSkipsNulls() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(2));
            oneOf(values).getValue(0, 0); will(returnValue(null));
            oneOf(values).getValue(0, 1); will(returnValue(4.0));
        }});
        double total = DataUtilities.calculateRowTotal(values, 0);
        assertEquals(4.0, total, 1e-9);
    }

    // ---------- calculateRowTotal(Values2D, int, int[]) ----------

    @Test
    public void rowTotalWithValidColsFilters() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(4));
            oneOf(values).getValue(1, 1); will(returnValue(2.0));
            oneOf(values).getValue(1, 3); will(returnValue(5.0));
        }});
        int[] validCols = { 1, 3 };
        double total = DataUtilities.calculateRowTotal(values, 1, validCols);
        assertEquals(7.0, total, 1e-9);
    }

    @Test
    public void rowTotalWithDuplicateValidColsCountsEach() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(3));
            exactly(2).of(values).getValue(0, 1);
            will(onConsecutiveCalls(returnValue(2.0), returnValue(2.0)));
            oneOf(values).getValue(0, 2); will(returnValue(3.0));
        }});
        int[] validCols = { 1, 1, 2 };
        double total = DataUtilities.calculateRowTotal(values, 0, validCols);
        assertEquals(7.0, total, 1e-9);
    }

    @Test
    public void rowTotalWithValidColsSkipsOutOfBounds() {
        final Values2D values = context.mock(Values2D.class);
        context.checking(new Expectations() {{
            oneOf(values).getColumnCount(); will(returnValue(2));
            oneOf(values).getValue(0, 1); will(returnValue(5.0));
        }});
        int[] validCols = { 1, 4 };
        double total = DataUtilities.calculateRowTotal(values, 0, validCols);
        assertEquals(5.0, total, 1e-9);
    }

    // ---------- createNumberArray ----------

    @Test
    public void createNumberArrayCopiesValues() {
        double[] data = { 1.0, -2.5, 3.3 };
        Number[] out = DataUtilities.createNumberArray(data);
        assertNotNull(out);
        assertEquals(3, out.length);
        assertEquals(1.0, out[0].doubleValue(), 1e-9);
        assertEquals(-2.5, out[1].doubleValue(), 1e-9);
        assertEquals(3.3, out[2].doubleValue(), 1e-9);
    }

    @Test
    public void createNumberArraySingleElement() {
        Number[] out = DataUtilities.createNumberArray(new double[] { 4.5 });
        assertNotNull(out);
        assertEquals(1, out.length);
        assertEquals(4.5, out[0].doubleValue(), 1e-9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNumberArrayNullInputThrowsException() {
        DataUtilities.createNumberArray(null);
    }

    @Test
    public void createNumberArrayHandlesZeros() {
        Number[] out = DataUtilities.createNumberArray(new double[] { 0.0, -0.0 });
        assertNotNull(out);
        assertEquals(2, out.length);
        assertNotNull(out[0]);
        assertNotNull(out[1]);
    }

    @Test
    public void createNumberArrayLargeValues() {
        Number[] out = DataUtilities.createNumberArray(new double[] { 1e9, -1e9 });
        assertNotNull(out);
        assertEquals(2, out.length);
        assertEquals(1e9, out[0].doubleValue(), 1e-6);
        assertEquals(-1e9, out[1].doubleValue(), 1e-6);
    }

    // ---------- createNumberArray2D ----------

    @Test
    public void createNumberArray2DCopyValues() {
        double[][] in = { { 1.0, 2.0 }, { -1.5, 0.5 } };
        Number[][] out = DataUtilities.createNumberArray2D(in);
        assertNotNull(out);
        assertEquals(2, out.length);
        assertEquals(2, out[0].length);
        assertEquals(1.0, out[0][0].doubleValue(), 1e-9);
        assertEquals(2.0, out[0][1].doubleValue(), 1e-9);
        assertEquals(-1.5, out[1][0].doubleValue(), 1e-9);
        assertEquals(0.5, out[1][1].doubleValue(), 1e-9);
    }

    @Test
    public void createNumberArray2DSingleCell() {
        Number[][] out = DataUtilities.createNumberArray2D(new double[][] { { 7.7 } });
        assertNotNull(out);
        assertEquals(1, out.length);
        assertEquals(1, out[0].length);
        assertEquals(7.7, out[0][0].doubleValue(), 1e-9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNumberArray2DNullInputThrows() {
        DataUtilities.createNumberArray2D(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNumberArray2DNullRowThrows() {
        DataUtilities.createNumberArray2D(new double[][] { null });
    }

    @Test
    public void createNumberArray2DEmptyMatrix() {
        Number[][] out = DataUtilities.createNumberArray2D(new double[][] {});
        assertNotNull(out);
        assertEquals(0, out.length);
    }

    // ---------- getCumulativePercentages ----------

    @Test
    public void getCumulativePercentagesTypical() {
        final KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(3));
            allowing(kv).getKey(0); will(returnValue("A"));
            allowing(kv).getKey(1); will(returnValue("B"));
            allowing(kv).getKey(2); will(returnValue("C"));
            allowing(kv).getValue(0); will(returnValue(2));
            allowing(kv).getValue(1); will(returnValue(3));
            allowing(kv).getValue(2); will(returnValue(5));
        }});
        KeyedValues out = DataUtilities.getCumulativePercentages(kv);
        assertEquals(0.2, out.getValue(0).doubleValue(), 1e-9);
        assertEquals(0.5, out.getValue(1).doubleValue(), 1e-9);
        assertEquals(1.0, out.getValue(2).doubleValue(), 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void getCumulativePercentagesSingleItemIsOne() {
        final KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(1));
            allowing(kv).getKey(0); will(returnValue("X"));
            allowing(kv).getValue(0); will(returnValue(5));
        }});
        KeyedValues out = DataUtilities.getCumulativePercentages(kv);
        assertEquals(1.0, out.getValue(0).doubleValue(), 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void getCumulativePercentagesIncludesZeros() {
        final KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(3));
            allowing(kv).getKey(0); will(returnValue("A"));
            allowing(kv).getKey(1); will(returnValue("B"));
            allowing(kv).getKey(2); will(returnValue("C"));
            allowing(kv).getValue(0); will(returnValue(0));
            allowing(kv).getValue(1); will(returnValue(5));
            allowing(kv).getValue(2); will(returnValue(5));
        }});
        KeyedValues out = DataUtilities.getCumulativePercentages(kv);
        assertEquals(0.0, out.getValue(0).doubleValue(), 1e-9);
        assertEquals(0.5, out.getValue(1).doubleValue(), 1e-9);
        assertEquals(1.0, out.getValue(2).doubleValue(), 1e-9);
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCumulativePercentagesNullInputThrows() {
        DataUtilities.getCumulativePercentages(null);
    }

    @Test
    public void getCumulativePercentagesHandlesSmallTotals() {
        final KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(2));
            allowing(kv).getKey(0); will(returnValue("A"));
            allowing(kv).getKey(1); will(returnValue("B"));
            allowing(kv).getValue(0); will(returnValue(0.2));
            allowing(kv).getValue(1); will(returnValue(0.8));
        }});
        KeyedValues out = DataUtilities.getCumulativePercentages(kv);
        assertEquals(0.2, out.getValue(0).doubleValue(), 1e-9);
        assertEquals(1.0, out.getValue(1).doubleValue(), 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void cumulativePercentagesSumsToOne() {
        DefaultKeyedValues kv = new DefaultKeyedValues();
        kv.addValue("A", 2.0);
        kv.addValue("B", 2.0);
        kv.addValue("C", 6.0);
        KeyedValues out = DataUtilities.getCumulativePercentages(kv);
        assertEquals(0.2, out.getValue(0).doubleValue(), 1e-9);
        assertEquals(0.4, out.getValue(1).doubleValue(), 1e-9);
        assertEquals(1.0, out.getValue(2).doubleValue(), 1e-9);
    }

    @Test
    public void cumulativePercentagesHandlesNullsAsZero() {
        DefaultKeyedValues kv = new DefaultKeyedValues();
        kv.addValue("A", null);
        kv.addValue("B", 4.0);
        KeyedValues out = DataUtilities.getCumulativePercentages(kv);
        assertEquals(0.0, out.getValue(0).doubleValue(), 1e-9);
        assertEquals(1.0, out.getValue(1).doubleValue(), 1e-9);
    }
    // ---------- Additional Tests to Improve Mutation Coverage ----------

    @Test(expected = IllegalArgumentException.class)
    public void calculateColumnTotalNullDataThrows() {
        DataUtilities.calculateColumnTotal(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateColumnTotalWithRowsNullDataThrows() {
        DataUtilities.calculateColumnTotal(null, 0, new int[]{0});
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateRowTotalNullDataThrows() {
        DataUtilities.calculateRowTotal(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateRowTotalWithColsNullDataThrows() {
        DataUtilities.calculateRowTotal(null, 0, new int[]{0});
    }

    @Test
    public void calculateColumnTotalWithValidRows_RowExactlyAtRowCount() {
        final Values2D values = context.mock(Values2D.class, "boundaryCols");
        context.checking(new Expectations() {{
            one(values).getRowCount(); will(returnValue(2));
        }});
        double result = DataUtilities.calculateColumnTotal(values, 0, new int[]{2});
        assertEquals(0.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    @Test
    public void calculateRowTotalWithValidCols_ColExactlyAtColCount() {
        final Values2D values = context.mock(Values2D.class, "boundaryRows");
        context.checking(new Expectations() {{
            one(values).getColumnCount(); will(returnValue(2));
        }});
        double result = DataUtilities.calculateRowTotal(values, 0, new int[]{2});
        assertEquals(0.0, result, 1e-9);
        context.assertIsSatisfied();
    }

}
