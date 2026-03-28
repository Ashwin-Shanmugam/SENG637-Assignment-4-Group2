package org.jfree.data;

import static org.junit.Assert.*;
import org.jfree.data.Range;
import org.junit.*;

public class RangeTest {

    private Range range;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception { }

    @Before
    public void setUp() {
        // Default range; many tests override it as needed.
        range = new Range(-2.0, 5.0);
    }

    // -----------------------------------------------------------------
    // getLowerBound() Tests
    // -----------------------------------------------------------------

    @Test
    public void test_getLowerBound_NegativeNominal() {
        range = new Range(-10.0, 10.0);
        assertEquals(-10.0, range.getLowerBound(), 0.0000001);
    }

    @Test
    public void test_getLowerBound_JustBelowZero() {
        range = new Range(-0.1, 10.0);
        assertEquals(-0.1, range.getLowerBound(), 0.0000001);
    }

    @Test
    public void test_getLowerBound_ExtremeNegative() {
        range = new Range(-Double.MAX_VALUE, 0.0);
        assertEquals(-Double.MAX_VALUE, range.getLowerBound(), 0.000000001);
    }

    @Test
    public void test_getLowerBound_ZeroBoundary() {
        range = new Range(0.0, 10.0);
        assertEquals(0.0, range.getLowerBound(), 0.0000001);
    }

    @Test
    public void test_getLowerBound_PositiveNominal() {
        range = new Range(10.0, 20.0);
        assertEquals(10.0, range.getLowerBound(), 0.000000001);
    }

    @Test
    public void test_getLowerBound_JustAboveZero() {
        range = new Range(0.1, 10.0);
        assertEquals(0.1, range.getLowerBound(), 0.000000001);
    }

    @Test
    public void test_getLowerBound_EqualBounds() {
        range = new Range(5.0, 5.0);
        assertEquals(5.0, range.getLowerBound(), 0.000000001);
    }

    // -----------------------------------------------------------------
    // getUpperBound() Tests
    // -----------------------------------------------------------------

    @Test
    public void test_getUpperBound_NegativeNominal() {
        range = new Range(-20.0, -10.0);
        assertEquals(-10.0, range.getUpperBound(), 0.000000001);
    }

    @Test
    public void test_getUpperBound_JustBelowZero() {
        range = new Range(-10.0, -0.1);
        assertEquals(-0.1, range.getUpperBound(), 0.000000001);
    }

    @Test
    public void test_getUpperBound_ZeroBoundary() {
        range = new Range(-10.0, 0.0);
        assertEquals(0.0, range.getUpperBound(), 0.000000001);
    }

    @Test
    public void test_getUpperBound_PositiveNominal() {
        range = new Range(10.0, 20.0);
        assertEquals(20.0, range.getUpperBound(), 0.000000001);
    }

    @Test
    public void test_getUpperBound_JustAboveZero() {
        range = new Range(-10.0, 0.1);
        assertEquals(0.1, range.getUpperBound(), 0.000000001);
    }

    @Test
    public void test_getUpperBound_ExtremePositive() {
        range = new Range(0.0, Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, range.getUpperBound(), 0.000000001);
    }

    @Test
    public void test_getUpperBound_EqualBounds() {
        range = new Range(5.0, 5.0);
        assertEquals(5.0, range.getUpperBound(), 0.000000001);
    }

    // ---------------------------------------------------------
    // getLength() Tests
    // ---------------------------------------------------------

    @Test
    public void test_getLength_BothPositive() {
        range = new Range(2.0, 5.0);
        assertEquals(3.0, range.getLength(), 0.000000001);
    }

    @Test
    public void test_getLength_BothNegative() {
        range = new Range(-10.0, -2.0);
        assertEquals(8.0, range.getLength(), 0.000000001);
    }

    @Test
    public void test_getLength_MixedSigns() {
        range = new Range(-5.0, 5.0);
        assertEquals(10.0, range.getLength(), 0.000000001);
    }

    @Test
    public void test_getLength_ZeroLength() {
        range = new Range(5.0, 5.0);
        assertEquals(0.0, range.getLength(), 0.000000001);
    }

    @Test
    public void test_getLength_LowerBoundZero() {
        range = new Range(0.0, 10.0);
        assertEquals(10.0, range.getLength(), 0.000000001);
    }

    @Test
    public void test_getLength_UpperBoundZero() {
        range = new Range(-10.0, 0.0);
        assertEquals(10.0, range.getLength(), 0.000000001);
    }

    @Test
    public void test_getLength_ExtremeValues() {
        range = new Range(-1_000_000.0, 1_000_000.0);
        assertEquals(2_000_000.0, range.getLength(), 0.000000001);
    }

    // ---------------------------------------------------------
    // intersects(double, double) Tests
    // ---------------------------------------------------------

    @Test
    public void intersectsWhenTouchingAtUpperBound() {
        assertFalse(range.intersects(5.0, 6.0));
    }

    @Test
    public void intersectsReturnsFalseForDisjointAbove() {
        assertFalse(range.intersects(6.0, 7.0));
    }

    @Test
    public void intersectsReturnsFalseForDisjointBelow() {
        assertFalse(range.intersects(-5.0, -3.0));
    }

    @Test
    public void intersectsWhenFullyInsideRange() {
        assertTrue(range.intersects(-1.0, 1.0));
    }

    @Test
    public void intersectsWhenRangeFullyInsideInterval() {
        assertTrue(range.intersects(-3.0, 6.0));
    }

    // ---------------------------------------------------------
    // constrain(double) Tests
    // ---------------------------------------------------------

    @Test
    public void constrainReturnsUpperWhenAbove() {
        assertEquals(5.0, range.constrain(10.0), 1e-9);
    }

    @Test
    public void constrainReturnsLowerWhenBelow() {
        assertEquals(-2.0, range.constrain(-10.0), 1e-9);
    }

    @Test
    public void constrainReturnsValueWhenInside() {
        assertEquals(1.5, range.constrain(1.5), 1e-9);
    }

    @Test
    public void constrainReturnsLowerAtExactLowerBound() {
        assertEquals(-2.0, range.constrain(-2.0), 1e-9);
    }

    @Test
    public void constrainReturnsUpperAtExactUpperBound() {
        assertEquals(5.0, range.constrain(5.0), 1e-9);
    }

    // ---------------------------------------------------------
    // contains(double) Tests
    // ---------------------------------------------------------
    @Test
    public void testContains_ValueInside() {
        assertTrue("Value inside the range should return true", range.contains(1.0));
    }

    @Test
    public void testContains_ExactLowerBound() {
        assertTrue("Exact lower bound should return true", range.contains(-2.0));
    }

    @Test
    public void testContains_ExactUpperBound() {
        assertTrue("Exact upper bound should return true", range.contains(5.0));
    }

    @Test
    public void testContains_ValueBelowLowerBound() {
        assertFalse("Value below the range should return false", range.contains(-5.0));
    }

    @Test
    public void testContains_ValueAboveUpperBound() {
        assertFalse("Value above the range should return false", range.contains(10.0));
    }

    // ---------------------------------------------------------
    // combine(Range, Range) Tests
    // ---------------------------------------------------------
    @Test
    public void testCombine_BothNull() {
        assertNull("Combining two null ranges should return null", Range.combine(null, null));
    }

    @Test
    public void testCombine_FirstNullSecondValid() {
        Range range2 = new Range(1.0, 5.0);
        assertEquals("If first is null, should return second", range2, Range.combine(null, range2));
    }

    @Test
    public void testCombine_FirstValidSecondNull() {
        Range range1 = new Range(1.0, 5.0);
        assertEquals("If second is null, should return first", range1, Range.combine(range1, null));
    }

    @Test
    public void testCombine_BothValidOverlapping() {
        Range range1 = new Range(1.0, 5.0);
        Range range2 = new Range(3.0, 10.0);
        Range expected = new Range(1.0, 10.0);
        assertEquals("Should return outer boundaries of both ranges", expected, Range.combine(range1, range2));
    }

    // ---------------------------------------------------------
    // expandToInclude(Range, double) Tests
    // ---------------------------------------------------------
    @Test
    public void testExpandToInclude_NullRange() {
        Range expected = new Range(5.0, 5.0);
        assertEquals("Expanding a null range should return a range with value as both bounds",
                     expected, Range.expandToInclude(null, 5.0));
    }

    @Test
    public void testExpandToInclude_ValueBelowLowerBound() {
        Range expected = new Range(-5.0, 5.0);
        assertEquals("Should expand lower bound to include value", expected, Range.expandToInclude(range, -5.0));
    }

    @Test
    public void testExpandToInclude_ValueAboveUpperBound() {
        Range expected = new Range(-2.0, 10.0);
        assertEquals("Should expand upper bound to include value", expected, Range.expandToInclude(range, 10.0));
    }

    @Test
    public void testExpandToInclude_ValueAlreadyInside() {
        assertEquals("Should return the same range if value is inside", range, Range.expandToInclude(range, 1.0));
    }

    // ---------------------------------------------------------
    // equals(Object) Tests
    // ---------------------------------------------------------
    @Test
    public void testEquals_NullObject() {
        assertFalse("Should return false for null object", range.equals(null));
    }

    @Test
    public void testEquals_DifferentClass() {
        assertFalse("Should return false for a non-Range object", range.equals("Not a Range"));
    }

    @Test
    public void testEquals_SameReference() {
        assertTrue("Should return true for the same reference", range.equals(range));
    }

    @Test
    public void testEquals_EquivalentObject() {
        Range equivalentRange = new Range(-2.0, 5.0);
        assertTrue("Should return true for an equivalent range object", range.equals(equivalentRange));
    }

    @Test
    public void testEquals_DifferentLowerBound() {
        Range differentLower = new Range(-1.0, 5.0);
        assertFalse("Should return false if lower bounds are different", range.equals(differentLower));
    }

    @Test
    public void testEquals_DifferentUpperBound() {
        Range differentUpper = new Range(-2.0, 6.0);
        assertFalse("Should return false if upper bounds are different", range.equals(differentUpper));
    }

    // ---------------------------------------------------------
    // shift(Range, double) Tests
    // ---------------------------------------------------------
    @Test
    public void testShift_PositiveDelta() {
        Range expected = new Range(0.0, 7.0);
        assertEquals("Should shift bounds by positive delta", expected, Range.shift(range, 2.0));
    }

    @Test
    public void testShift_NegativeDelta() {
        Range expected = new Range(-4.0, 3.0);
        assertEquals("Should shift bounds by negative delta", expected, Range.shift(range, -2.0));
    }

    @Test
    public void testShift_ZeroDelta() {
        assertEquals("Should not change bounds for zero delta", range, Range.shift(range, 0.0));
    }

    //---------------------------------------------------------
    //Exception and Validation Tests
    //---------------------------------------------------------
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_LowerGreaterThanUpper() {
        new Range(10.0, 5.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScale_NegativeFactor() {
        Range.scale(range, -1.0);
    }

    //---------------------------------------------------------
    //combineIgnoringNaN(Range, Range) Tests
    //---------------------------------------------------------
    @Test
    public void testCombineIgnoringNaN_Range1Null_Range2Valid() {
        assertEquals(range, Range.combineIgnoringNaN(null, range));
    }

    @Test
    public void testCombineIgnoringNaN_Range1Null_Range2NaN() {
        Range nanRange = new Range(Double.NaN, Double.NaN);
        assertNull(Range.combineIgnoringNaN(null, nanRange));
    }

    @Test
    public void testCombineIgnoringNaN_Range2Null_Range1Valid() {
        assertEquals(range, Range.combineIgnoringNaN(range, null));
    }

    @Test
    public void testCombineIgnoringNaN_Range2Null_Range1NaN() {
        Range nanRange = new Range(Double.NaN, Double.NaN);
        assertNull(Range.combineIgnoringNaN(nanRange, null));
    }

    @Test
    public void testCombineIgnoringNaN_BothNaN() {
        Range nanRange = new Range(Double.NaN, Double.NaN);
        assertNull(Range.combineIgnoringNaN(nanRange, nanRange));
    }

    @Test
    public void testCombineIgnoringNaN_BothValid() {
        Range r1 = new Range(1.0, 3.0);
        Range r2 = new Range(2.0, 5.0);
        assertEquals(new Range(1.0, 5.0), Range.combineIgnoringNaN(r1, r2));
    }

    //---------------------------------------------------------
    //shift(Range, double, boolean) Tests
    //---------------------------------------------------------
    @Test
    public void testShift_AllowZeroCrossing() {
        assertEquals(new Range(1.0, 8.0), Range.shift(range, 3.0, true));
    }

    @Test
    public void testShift_NoZeroCrossing_PositiveValues() {
        Range r = new Range(2.0, 5.0);
        assertEquals(new Range(1.0, 4.0), Range.shift(r, -1.0, false));
    }

    @Test
    public void testShift_NoZeroCrossing_NegativeValues() {
        Range r = new Range(-5.0, -2.0);
        assertEquals(new Range(-4.0, -1.0), Range.shift(r, 1.0, false));
    }

    @Test
    public void testShift_NoZeroCrossing_ZeroValue() {
        Range r = new Range(0.0, 0.0);
        assertEquals(new Range(1.0, 1.0), Range.shift(r, 1.0, false));
    }

    //---------------------------------------------------------
    //General Method Tests
    //---------------------------------------------------------
    @Test
    public void testGetCentralValue() {
        assertEquals(1.5, range.getCentralValue(), 1e-9);
    }

    @Test
    public void testIntersects_RangeObject() {
        assertTrue(range.intersects(new Range(-1.0, 1.0)));
    }

    @Test
    public void testScale_PositiveFactor() {
        Range expected = new Range(-4.0, 10.0);
        assertEquals(expected, Range.scale(range, 2.0));
    }

    @Test
    public void testIsNaNRange_True() {
        Range nanRange = new Range(Double.NaN, Double.NaN);
        assertTrue(nanRange.isNaNRange());
    }

    @Test
    public void testIsNaNRange_False() {
        assertFalse(range.isNaNRange());
    }

    @Test
    public void testHashCode() {
        range.hashCode();
    }

    @Test
    public void testToString() {
        assertEquals("Range[-2.0,5.0]", range.toString());
    }

    @Test
    public void testExpand_LowerGreaterThanUpperAfterMargins() {
        Range result = Range.expand(new Range(2.0, 6.0), -0.5, -0.5);
        assertEquals(new Range(4.0, 4.0), result);
    }

    // ---------------------------------------------------------
    // Additional Tests to Improve Mutation Coverage
    // ---------------------------------------------------------

    @Test
    public void testIntersects_RangeObject_NoOverlap() {
        assertFalse(range.intersects(new Range(6.0, 10.0)));
    }

    @Test
    public void intersectsWhenB0ExactlyAtLowerBound() {
        assertTrue(range.intersects(-2.0, 0.0));
    }

    @Test
    public void intersectsWhenB0ExactlyAtUpperBound() {
        assertFalse(range.intersects(5.0, 8.0));
    }

    @Test
    public void testScale_ZeroFactor() {
        assertEquals(new Range(0.0, 0.0), Range.scale(range, 0.0));
    }

    @Test
    public void testHashCode_ConsistentAndCorrect() {
        Range r1 = new Range(-2.0, 5.0);
        Range r2 = new Range(-2.0, 5.0);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertFalse(0 == r1.hashCode());
    }

    @Test
    public void testCombineIgnoringNaN_Range1Valid_Range2LowerNaN() {
        Range r1 = new Range(1.0, 3.0);
        Range r2 = new Range(Double.NaN, 5.0);
        Range result = Range.combineIgnoringNaN(r1, r2);
        assertEquals(new Range(1.0, 5.0), result);
    }

    @Test
    public void testExpand_NegativeMarginsCausingInversion() {
        Range r = Range.expand(new Range(0.0, 4.0), -0.5, -0.5);
        assertEquals(r.getLowerBound(), r.getUpperBound(), 1e-9);
    }

    @Test
    public void constrainJustAboveUpper() {
        assertEquals(5.0, range.constrain(5.0001), 1e-4);
    }

    @Test
    public void constrainJustBelowLower() {
        assertEquals(-2.0, range.constrain(-2.0001), 1e-4);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testExpand_NullRange() {
        Range.expand(null, 0.5, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShift_NullBase() {
        Range.shift(null, 1.0, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScale_NullBase() {
        Range.scale(null, 1.0);
    }

    @Test
    public void testExpand_LowerActuallyExceedsUpper() {
        // lower=2.0, upper=1.8 → triggers lower > upper branch (line 355-357)
        Range result = Range.expand(new Range(0.0, 2.0), -1.0, -0.1);
        assertEquals(1.9, result.getLowerBound(), 1e-9);
        assertEquals(1.9, result.getUpperBound(), 1e-9);
    }

    @Test
    public void intersectsWhenB1ExactlyAtLowerBound() {
        // b1 == lower: b1 > lower is false, mutant b1 >= lower would be true
        assertFalse(range.intersects(-5.0, -2.0));
    }

    @Test
    public void testCombineIgnoringNaN_BothNonNull_Range1NaN_Range2Valid() {
        // Covers max() where d1 (range1.upper) is NaN, d2 is not
        Range nanRange = new Range(Double.NaN, Double.NaN);
        Range r2 = new Range(1.0, 3.0);
        assertEquals(new Range(1.0, 3.0), Range.combineIgnoringNaN(nanRange, r2));
    }
    @Test
    public void testCombineIgnoringNaN_BothNonNull_Range1Valid_Range2NaN() {
        Range r1 = new Range(1.0, 3.0);
        Range nanRange = new Range(Double.NaN, Double.NaN);
        assertEquals(new Range(1.0, 3.0), Range.combineIgnoringNaN(r1, nanRange));
    }

    @After
    public void tearDown() throws Exception { }

    @AfterClass
    public static void tearDownAfterClass() throws Exception { }
}
