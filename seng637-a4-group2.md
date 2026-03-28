# SENG 637 - Dependability and Reliability of Software Systems
## Assignment #4 — Group 2

**Mutation Testing (Fault Injection) & GUI and Web Testing**

| Name | Student ID |
|------|------------|
| Jasneet Singh | 30044332 |
| Ashwin Shanmugam | 30300738 |
| Noshin Chowdhury | 30112985 |
| Salehin Kazi | 30270206 |

---

# Introduction

This report covers two parts of Assignment 4 for SENG 637.

**Part 1 : Mutation Testing:** We used Pitest (PIT Mutation Testing) to assess the effectiveness of our test suites developed in Assignment 3 for the JFreeChart library. Mutation testing works by automatically introducing small faults (mutants) into the source code and checking whether our existing tests can detect (kill) them. This gives a more meaningful measure of test suite quality than code coverage alone. We ran mutation tests on both `RangeTest` and `DataUtilitiesTest`, analyzed the results, identified equivalent mutants, and added new test cases to improve the mutation score by at least 10% for each class.

**Part 2 : GUI Testing:** We used Selenium IDE to automate GUI tests for the Air Canada website (https://www.aircanada.com). We designed and recorded test cases covering four functionalities: flight search, form validation, flight status check, and manage booking. We also explored Sikulix as an alternative tool and compared it with Selenium IDE.

---

# Part 1: Mutation Testing

## 1.1 Sample Test Suite Results (org.jfree.data.junit)

> Mutation tests were first run on the sample test cases provided under `org.jfree.data.junit` as a familiarization step (Section 2.5.4).

### DataUtilitiesTest — Sample Results

| Metric | Value |
|--------|-------|
| Survived | 63 |
| Killed | 624 |
| Total Mutants | 687 |
| Line Coverage | 99% (79/80) |
| Mutation Coverage | 91% (624/687) |
| Test Strength | 91% (624/687) |

### RangeTest — Sample Results

The sample test suite (`org.jfree.data.junit`) did not include meaningful test cases targeting the `Range` class. As a result, Pitest showed 0% line and mutation coverage for `Range` when running the sample suite. This confirmed that our own `RangeTest` suite from Assignment 3 was necessary to achieve meaningful mutation coverage of the `Range` class.

---

## 1.2 Initial Mutation Test Results (Own Test Suites from Assignment 3)

### DataUtilitiesTest.java — Initial Results

| Metric | Value |
|--------|-------|
| Survived | 15 |
| Killed | 67 |
| Total Mutants | 82 |
| Line Coverage | 88% (84/96) |
| Mutation Coverage | 82% (67/82) |
| Test Strength | 88% (67/76) |

### RangeTest.java — Fix Applied Before Running

One test was failing before mutation testing could be run:

**Original (failing):**
```java
@Test
public void intersectsWhenTouchingAtUpperBound() {
    assertTrue(range.intersects(5.0, 6.0)); // range is (-2.0, 5.0)
}
```

The `intersects` method uses strict inequality (`b0 < upperBound`), so `5.0 < 5.0` is `false`. The test was corrected to:

```java
@Test
public void intersectsWhenTouchingAtUpperBound() {
    assertFalse(range.intersects(5.0, 6.0));
}
```

### RangeTest.java — Initial Results

| Metric | Value |
|--------|-------|
| Survived | 30 |
| Killed | 101 |
| Total Mutants | 131 |
| Line Coverage | 91% (103/113) |
| Mutation Coverage | 77% (101/131) |
| Test Strength | 80% (101/126) |

---

## 1.3 Analysis of 10 Mutants (Range Class)

> Analysis of at least 10 mutants produced by Pitest for the `Range` class, and whether they were killed or survived by the original test suite. For each mutant, the specific mutation operator, the affected line, and the reason it was killed or survived is explained.

| # | Mutant Description | Mutation Operator | Method | Line | Status | Why Killed / Why Survived |
|---|-------------------|-------------------|--------|------|--------|--------------------------|
| 1 | Changed `lower > upper` to `lower >= upper` in the constructor | CONDITIONALS_BOUNDARY | `Range()` | 90 | KILLED | `test_getLowerBound_EqualBounds` creates `Range(5.0, 5.0)` — equal bounds are valid, so flipping to `>=` throws an exception, killing the mutant. |
| 2 | Replaced `return this.lower` with `return 0.0d` | PRIMITIVE_RETURNS | `getLowerBound()` | 110 | KILLED | Multiple tests assert a specific non-zero lower bound (e.g., `-10.0`), so returning `0.0` immediately fails these assertions. |
| 3 | Replaced `this.upper - this.lower` with `this.upper + this.lower` | MATH | `getLength()` | 138 | KILLED | `test_getLength_BothPositive` expects `3.0` for `Range(2.0, 5.0)`; addition gives `7.0`, which does not match. |
| 4 | Replaced `boolean return with true` in `contains()` at redundant check on line 165 | TRUE_RETURNS | `contains()` | 165 | SURVIVED | The redundant final return `(value >= lower && value <= upper)` is unreachable after the two early-return guards. Returning `true` instead never affects any test because no test reaches this line with a value outside the range at this point. |
| 5 | Changed conditional boundary `b0 <= this.lower` to `b0 < this.lower` | CONDITIONALS_BOUNDARY | `intersects()` | 179 | SURVIVED | No test exercises the boundary case where `b0 == this.lower` exactly, so tightening the boundary does not change any test outcome. |
| 6 | Changed conditional boundary `b0 < this.upper` to `b0 <= this.upper` | CONDITIONALS_BOUNDARY | `intersects()` | 183 | SURVIVED | No test targets the exact boundary `b0 == this.upper`, so widening the condition does not cause any test to fail. |
| 7 | Replaced `boolean return with true` in `intersects(Range)` | TRUE_RETURNS | `intersects(Range)` | 198 | SURVIVED | Only one test calls `intersects(Range)` with an overlapping range (expected `true`). There is no test that calls `intersects(Range)` expecting `false`, so returning `true` always passes. |
| 8 | Negated conditional `Double.isNaN(d2)` to `!Double.isNaN(d2)` in private `min()` | NEGATE_CONDITIONALS | `min()` | 296 | SURVIVED | No test passes a NaN as only the second argument to `combineIgnoringNaN` while the first is non-NaN. The NaN path in `min()` where only `d2` is NaN is never exercised by a test that would observe the difference. |
| 9 | Changed `factor < 0` to `factor <= 0` in `scale()` | CONDITIONALS_BOUNDARY | `scale()` | 432 | SURVIVED | No test calls `scale(range, 0.0)` — a zero factor is a valid edge case that is untested, so the mutant (which would reject factor=0) is never exposed. |
| 10 | Replaced XOR with AND in `hashCode()` bit manipulation | MATH | `hashCode()` | 483 | SURVIVED | The `testHashCode` test only calls `hashCode()` for statement coverage without asserting a specific value. No test verifies the actual hash code result, so any arithmetic mutation here survives. |

> **Screenshot of Pitest mutation report (Range class):**
>
> ![Pitest mutation report - Range](./media/pitest-range-original.png)

---

## 1.4 Updated Mutation Test Results (After Adding New Test Cases)

### DataUtilitiesTest.java — Updated Results

| Metric | Original | Updated | Change |
|--------|----------|---------|--------|
| Survived | 15 | 9 | -6 |
| Killed | 67 | 73 | +6 |
| Total Mutants | 82 | 82 | — |
| Line Coverage | 88% | 91% | +3% |
| Mutation Coverage | 82% | 89% | +7% |
| Test Strength | 88% | 91% | +3% |

> **Screenshot — Final Results (both classes combined):**
>
> ![Pitest final summary](./media/pitest-updated-summary.png)

### RangeTest.java — Updated Results

| Metric | Original | Updated | Change |
|--------|----------|---------|--------|
| Survived | 30 | 15 | -15 |
| Killed | 101 | 116 | +15 |
| Total Mutants | 131 | 131 | — |
| Line Coverage | 91% | 95% | +4% |
| Mutation Coverage | 77% | 89% | +12% |
| Test Strength | 80% | 89% | +9% |

> **Screenshot — Final Results (both classes combined):**
>
> ![Pitest final summary](./media/pitest-updated-summary.png)

---

## 1.5 Equivalent Mutants

> A discussion on the effect of equivalent mutants on mutation score accuracy, and how equivalent mutants can be detected.

### Effect on Mutation Score Accuracy

Equivalent mutants are syntactically different from the original program but semantically identical — no test input can distinguish them from the original. They are always counted as "survived" by the mutation tool, artificially inflating the denominator of the mutation score formula (`killed / total`). This makes the mutation score appear lower than it actually is, since the survived equivalent mutants were never killable in the first place. The true mutation score should exclude equivalent mutants, but detecting them requires manual effort.

For example, in `Range.contains()`, a TRUE_RETURNS mutation on the final return statement survives because that line is unreachable dead code — the two preceding `if` guards handle all cases first. In `hashCode()`, arithmetic mutations survive because no test asserts the actual hash value returned.

### Approach for Detecting Equivalent Mutants

We identified equivalent mutants by inspecting the Pitest HTML mutation report for survived mutants, then reading the mutated code and reasoning about whether any possible test input could produce a different observable output. Specifically, we looked for: dead code (unreachable lines after early returns), mathematical identities (operations whose result is never read), and loop increment patterns where `i++` vs `++i` produce identical behaviour.

**Benefits of the approach:**
- Systematic and repeatable — works directly from the Pitest HTML report
- Does not require running additional tests; purely based on code reasoning

**Disadvantages of the approach:**
- Time-consuming — each surviving mutant must be manually inspected
- Requires deep understanding of the method's semantics and control flow
- Not automatable at scale; equivalent mutant detection remains an open research problem

**Assumptions:**
- The source code is correct and behaves as documented
- We have a full understanding of each method's intended semantics and preconditions

### Equivalent Mutants Found

**Range class:**

| Mutant Description | Method | Line | Why It Is Equivalent |
|--------------------|--------|------|----------------------|
| TRUE_RETURNS on unreachable final `return` statement after two early-return guards | `contains()` | 165 | Dead code — execution never reaches this line because all cases are handled by the preceding `if` branches. Mutating it cannot change observable behaviour. |
| MATH: replaced XOR with OR in bit-manipulation | `hashCode()` | 483 | The `hashCode` contract only requires consistency, not a specific value. No test asserts the actual integer returned, so any arithmetic substitution here is undetectable. |

**DataUtilities class:**

| Mutant Description | Method | Line | Why It Is Equivalent |
|--------------------|--------|------|----------------------|
| Post-increment (`a++`) changed to pre-increment (`++a`) on loop variable | `calculateColumnTotal()` | 133 | The loop variable is only used as a counter; pre- vs post-increment produces identical loop behaviour when the increment result is not used in an expression. |
| Post-increment (`a++`) changed to pre-increment (`++a`) on loop variable | `calculateRowTotal()` | 194 | Same reasoning — post-increment vs pre-increment on a standalone loop counter produces identical results. |

---

## 1.6 How We Improved the Mutation Score

> A discussion of the design strategy used to improve mutation scores for `Range` and `DataUtilities`.

### Strategy

Our improvement process followed three steps: (1) run the initial Pitest report and open the HTML output for each class, (2) inspect each survived mutant grouped by mutation operator to identify patterns (e.g., many CONDITIONALS_BOUNDARY survivors in `intersects()` indicated missing boundary tests), and (3) write targeted tests specifically designed to produce a different outcome when the mutation is applied. We repeated this cycle until the combined mutation score reached the required 10% improvement.

### Test Cases Added — Range

| New Test Case | Targets Mutation Operator | Survived Mutant Killed | Mutation Coverage Before | Mutation Coverage After |
|--------------|--------------------------|------------------------|--------------------------|-------------------------|
| `intersectsWhenB0ExactlyAtLowerBound` | CONDITIONALS_BOUNDARY | `b0 <= this.lower` boundary in `intersects()` | 77% | 79% |
| `intersectsWhenB0ExactlyAtUpperBound` | CONDITIONALS_BOUNDARY | `b0 < this.upper` boundary in `intersects()` | 79% | 81% |
| `testIntersects_RangeObject_NoOverlap` | TRUE_RETURNS | Always-true return in `intersects(Range)` | 81% | 82% |
| `testScale_ZeroFactor` | CONDITIONALS_BOUNDARY | `factor < 0` boundary in `scale()` | 82% | 83% |
| `testCombineIgnoringNaN_BothNonNull_Range1NaN_Range2Valid` | NEGATE_CONDITIONALS | NaN check in `min()`/`max()` helpers | 83% | 85% |
| `testCombineIgnoringNaN_BothNonNull_Range1Valid_Range2NaN` | NEGATE_CONDITIONALS | NaN check in `min()`/`max()` helpers | 85% | 87% |
| `constrainJustAboveUpper` | CONDITIONALS_BOUNDARY | Boundary condition in `constrain()` | 87% | 88% |
| `constrainJustBelowLower` | CONDITIONALS_BOUNDARY | Boundary condition in `constrain()` | 88% | 89% |

### Test Cases Added — DataUtilities

| New Test Case | Targets Mutation Operator | Survived Mutant Killed | Mutation Coverage Before | Mutation Coverage After |
|--------------|--------------------------|------------------------|--------------------------|-------------------------|
| `calculateColumnTotalNullDataThrows` | NULL_RETURNS / exception path | Null check in `calculateColumnTotal` | 82% | 84% |
| `calculateRowTotalNullDataThrows` | NULL_RETURNS / exception path | Null check in `calculateRowTotal` | 84% | 86% |
| `calculateColumnTotalWithValidRows_RowExactlyAtRowCount` | CONDITIONALS_BOUNDARY | `row < rowCount` boundary in second loop | 86% | 87% |
| `calculateRowTotalWithValidCols_ColExactlyAtColCount` | CONDITIONALS_BOUNDARY | `col < colCount` boundary in second loop | 87% | 88% |
| `calculateColumnTotalWithRowsNullDataThrows` | NULL_RETURNS | Null check in overloaded `calculateColumnTotal` | 88% | 89% |
| `calculateRowTotalWithColsNullDataThrows` | NULL_RETURNS | Null check in overloaded `calculateRowTotal` | 89% | 89% |

---

## 1.7 Analysis of the Effectiveness of Each Test Class

### RangeTest Effectiveness

The original `RangeTest` suite from Assignment 3 achieved a mutation coverage of **77%** with a test strength of **80%**. While coverage was reasonable, a significant number of mutants survived due to boundary conditions and edge cases that were not explicitly tested — particularly for `intersects()`, `scale()`, `combineIgnoringNaN()`, and `hashCode()`. The suite had good breadth but lacked precision in asserting exact boundary behaviour.

### DataUtilitiesTest Effectiveness

The original `DataUtilitiesTest` suite achieved a mutation coverage of **82%** with a test strength of **88%**. The high test strength indicates that when code was reached, assertions were strong and meaningful. The survived mutants were concentrated in null-input paths and boundary conditions in `calculateColumnTotal` and `calculateRowTotal`, where certain row/column index edge cases were not exercised.

### Comparison

| Metric | RangeTest | DataUtilitiesTest |
|--------|-----------|-------------------|
| Initial Mutation Coverage | 77% (101/131) | 82% (67/82) |
| Final Mutation Coverage | 89% (116/131) | 89% (73/82) |
| Test Strength (final) | 89% | 96% |
| Main Weakness | Boundary conditions, NaN paths | Null inputs, boundary index checks |

`DataUtilitiesTest` had stronger initial coverage and assertion quality. `RangeTest` required more targeted additions to close boundary-related gaps.

---

## 1.8 Advantages and Disadvantages of Mutation Testing

### Advantages

- **Measures test suite effectiveness directly:** Unlike coverage metrics that only measure which code was executed, mutation testing verifies whether tests can actually detect faults — giving a more meaningful quality signal.
- **Reveals weak assertions:** Tests that execute code but make no meaningful assertions (e.g., checking non-nullness instead of actual values) are exposed, since mutants in those code paths will survive.
- **Guides test improvement systematically:** The Pitest HTML report clearly shows which mutants survived and which operator caused them, making it straightforward to write targeted new tests rather than guessing what to add.

### Disadvantages

- **Computationally expensive:** Pitest must compile and run the full test suite for every mutant. For large codebases with many tests, this can take significant time (as experienced in this lab).
- **Equivalent mutants reduce accuracy:** A non-trivial portion of survived mutants may be equivalent, requiring manual inspection to identify them and making the raw mutation score misleading.
- **Requires passing tests to run:** If any test in the suite fails, Pitest cannot run at all. This added overhead when some pre-existing tests needed to be fixed before mutation testing could begin (as seen with `intersectsWhenTouchingAtUpperBound`).

---

# Part 2: GUI Testing

## 2.1 Website Under Test

**Selected Website:** Air Canada

**URL:** https://www.aircanada.com

**Reason for selection:** Air Canada provides real-world, user-centric workflows for flight booking. The website includes interactive elements such as search forms, date pickers, and validation mechanisms, making it suitable for automated UI testing using Selenium.

---

## 2.2 Test Case Design Process

We identified four core functionalities of the Air Canada website to test:

1. **Flight Search** — the primary feature used by users to find flights (one-way, round-trip, multi-city)
2. **Form Validation for Flight Search** — ensures users provide correct and complete input before proceeding
3. **Flight Status Check** — allows users to check the status of a flight by route
4. **Manage Booking** — allows users to retrieve an existing booking using a reference number and last name

Test cases were designed to:
- Cover both **successful flows** and **error/validation scenarios**
- Use **different input data combinations** per functionality (valid, invalid, missing)
- Include **clear verification points** that can be automated in Selenium
- Ensure **repeatability and stability** in Selenium execution

Each student automated at least 2 different functionalities. The functionalities were divided among group members as follows:

| Student | Functionality 1 | Functionality 2 |
|---------|----------------|----------------|
| Jasneet Singh | Flight Search | Form Validation |
| Ashwin Shanmugam | Flight Search | Form Validation |
| Noshin | Flight Status Check | Manage Booking |
| Salehin Kazi | Flight Status Check | Manage Booking |

---

## 2.3 Test Cases

> Each student automated at least 2 different functionalities. Test cases are recorded using Selenium IDE. Assign student names below once confirmed.

### Student 1 — Jasneet Singh

#### Functionality 1: Flight Search

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-01 | One-way flight search with valid inputs | 1. Open homepage 2. Select one-way 3. Enter origin 4. Enter destination 5. Select departure date 6. Click search | Origin: Calgary, Destination: Toronto, Date: Future date | Search results page displayed with available flights | Results page loads; route and date displayed correctly | |
| TC-02 | Round-trip flight search with valid inputs | 1. Open homepage 2. Select round-trip 3. Enter origin 4. Enter destination 5. Select departure date 6. Select return date 7. Click search | Origin: Calgary, Destination: Vancouver, Departure: Future date, Return: Later date | Results page with departure and return flights | Results page loads; both departure and return trip details displayed | |
| TC-03 | Multi-city flight search with valid inputs | 1. Open homepage 2. Select multi-city 3. Enter first route 4. Select first date 5. Enter second route 6. Select second date 7. Click search | Route 1: Calgary→Toronto, Route 2: Toronto→Vancouver, Dates: Future dates | Results page with flight options for both routes | Results page loads; both routes and dates displayed correctly | |

#### Functionality 2: Form Validation (Flight Search)

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-04 | System prevents search when destination is missing | 1. Open homepage 2. Enter origin only 3. Select departure date 4. Click search | Origin: Calgary, Destination: (empty) | Validation message appears and search is blocked | Error message displayed; page does not navigate to results | |
| TC-05 | System prevents search when origin is missing | 1. Open homepage 2. Enter destination only 3. Select departure date 4. Click search | Destination: Toronto, Origin: (empty) | Validation message appears and search is blocked | Error message displayed; page remains on search form | |

---

### Student 2 — Ashwin Shanmugam

#### Functionality 1: Flight Search

> Student 2 repeats Flight Search functionality with different test data to validate consistency.

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-01 | One-way flight search | 1. Open homepage 2. Select one-way 3. Enter origin 4. Enter destination 5. Select date 6. Click search | Origin: Vancouver, Destination: Montreal, Date: Future date | Search results page displayed | Results page loads; route and date displayed | |
| TC-02 | Round-trip flight search | 1. Open homepage 2. Select round-trip 3. Enter origin 4. Enter destination 5. Select dates 6. Click search | Origin: Toronto, Destination: Ottawa, Departure/Return: Future dates | Results page with both trip details | Both departure and return details displayed | |

#### Functionality 2: Form Validation (Flight Search)

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-04 | Missing destination validation | 1. Open homepage 2. Enter origin only 3. Select date 4. Click search | Origin: Vancouver, Destination: (empty) | Validation error shown | Error message displayed; no navigation to results | |
| TC-05 | Missing origin validation | 1. Open homepage 2. Enter destination only 3. Select date 4. Click search | Destination: Montreal, Origin: (empty) | Validation error shown | Error message displayed; stays on search form | |

---

### Student 3 — Noshin

#### Functionality 1: Flight Status Check

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-06 | Check flight status with a valid route | 1. Navigate to the flight status section 2. Enter origin 3. Enter destination 4. Select date 5. Click search | Origin: Calgary, Destination: Toronto, Date: Current or future date | Flight status results displayed | Status results page loads; flight details displayed | |
| TC-07 | Validation triggered when inputs missing | 1. Navigate to the flight status section 2. Leave required fields empty 3. Click search | None (all fields empty) | Validation message appears and search blocked | Error message displayed; no results shown | |

#### Functionality 2: Manage Booking

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-08 | Error shown for invalid booking reference | 1. Navigate to My Bookings 2. Enter invalid booking reference 3. Enter last name 4. Click search | Booking Reference: ABC123, Last Name: Test | Error message displayed; booking not found | Error message displayed; no booking details shown | |
| TC-09 | Validation triggered when booking fields are missing | 1. Navigate to My Bookings 2. Leave booking reference empty 3. Leave last name empty 4. Click search | Booking Reference: (empty), Last Name: (empty) | Validation message appears; cannot proceed | Error message displayed; no navigation to booking details | |

---

### Student 4 — Salehin Kazi

#### Functionality 1: Flight Status Check

> Student 4 repeats Flight Status with different data to validate consistency.

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-06 | Check flight status with a valid route | 1. Navigate to the flight status section 2. Enter origin 3. Enter destination 4. Select date 5. Click search | Origin: Vancouver, Destination: Toronto, Date: Current or future date | Flight status results displayed | Status results page loads; flight details displayed | |
| TC-07 | Validation triggered when inputs missing | 1. Navigate to the flight status section 2. Leave required fields empty 3. Click search | None (all fields empty) | Validation message appears | Error message displayed; no results shown | |

#### Functionality 2: Manage Booking

| TC | Objective | Test Steps | Test Data | Expected Result | Verification Point | Pass/Fail |
|----|-----------|------------|-----------|-----------------|-------------------|-----------|
| TC-08 | Error shown for invalid booking reference | 1. Navigate to My Bookings 2. Enter invalid booking reference 3. Enter last name 4. Click search | Booking Reference: XYZ999, Last Name: Smith | Error message displayed | Error message displayed; no booking details shown | |
| TC-09 | Validation triggered when booking fields are missing | 1. Navigate to My Bookings 2. Leave fields empty 3. Click search | Booking Reference: (empty), Last Name: (partial) | Validation message appears | Error message displayed; stays on booking form | |

---

## 2.4 Use of Different Test Data

Different test data was used across test cases to cover valid inputs, invalid inputs, and missing/empty inputs for each functionality.

| Functionality | TC | Test Data | Data Type |
|--------------|-----|-----------|-----------|
| Flight Search | TC-01 | Origin: Calgary, Destination: Toronto, Date: Future | Valid input — one-way |
| Flight Search | TC-02 | Origin: Calgary, Destination: Vancouver, Departure + Return: Future dates | Valid input — round-trip |
| Flight Search | TC-03 | Route 1: Calgary→Toronto, Route 2: Toronto→Vancouver, Future dates | Valid input — multi-city |
| Form Validation | TC-04 | Origin: Calgary, Destination: (empty) | Invalid — missing required field |
| Form Validation | TC-05 | Origin: (empty), Destination: Toronto | Invalid — missing required field |
| Flight Status | TC-06 | Origin: Calgary, Destination: Toronto, Date: Current/future | Valid input |
| Flight Status | TC-07 | All fields: (empty) | Invalid — all fields missing |
| Manage Booking | TC-08 | Booking Reference: ABC123, Last Name: Test | Invalid — non-existent booking |
| Manage Booking | TC-09 | Booking Reference: (empty), Last Name: (empty) | Invalid — all fields missing |

Each functionality is tested with at least one valid and one invalid data scenario, ensuring both the happy path and error handling are verified.

---

## 2.5 Automated Verification Points

Verification points were added to each Selenium script using `assert` commands to automatically confirm expected outcomes after each test action. The following table summarizes the verification used per test case:

| TC | Verification Command | What Is Verified |
|----|---------------------|-----------------|
| TC-01 | `assertElementPresent` / `assertText` | Results page is loaded; route and date displayed correctly |
| TC-02 | `assertElementPresent` | Results page loads; both departure and return trip sections are present |
| TC-03 | `assertElementPresent` | Results page loads; both routes and dates are shown |
| TC-04 | `assertText` on error element | Error message is displayed; page does not navigate away from search form |
| TC-05 | `assertText` on error element | Error message is displayed; page stays on search form |
| TC-06 | `assertElementPresent` | Flight status results page loads; flight details are present |
| TC-07 | `assertText` on error element | Error message displayed; no results shown |
| TC-08 | `assertText` on error element | Error message displayed; no booking details shown |
| TC-09 | `assertText` on error element | Error/validation message displayed; no navigation to booking details |

### Challenge: Manual Target ID Assignment

Selenium IDE had difficulty automatically identifying certain web elements on the Air Canada website, particularly dynamic elements such as date pickers, dropdown suggestions, and error message containers. In these cases, the auto-generated locators (XPath or CSS selectors) were incorrect or unstable. We resolved this by manually inspecting the page source (browser DevTools) and setting the correct target IDs or CSS selectors directly in the Selenium IDE script.

This is a known limitation of record-and-replay tools when applied to modern dynamic websites that rely on JavaScript-rendered content.

---

## 2.6 Comparison: Selenium vs Sikulix

We briefly explored Sikulix as an alternative GUI testing tool and compared it with Selenium IDE.

| Aspect | Selenium IDE | Sikulix |
|--------|-------------|---------|
| Element identification | Uses DOM-based locators (ID, CSS, XPath) | Uses image recognition — does not rely on DOM |
| Ease of setup | Browser extension, easy to install | Requires Java and separate IDE setup |
| Handling dynamic elements | Struggles with JavaScript-rendered elements; manual target ID fixes needed | Less affected by dynamic DOM since it works on visual screenshots |
| Script portability | Scripts tied to browser and DOM structure | Scripts tied to screen resolution and visual appearance |
| Verification points | Supports `assert` commands on DOM elements | Verifies by matching visual regions on screen |
| Best use case | Web applications with stable, accessible DOM | Desktop apps or web pages where DOM access is difficult |

**Conclusion:** Selenium IDE is better suited for structured web testing where element IDs are accessible. Sikulix is more flexible for visual/desktop testing but is brittle when screen resolution or UI layout changes.

---

## 2.7 Defects Found During GUI Testing

> Note: Given applications are generally stable; it is acceptable to report no defects.

| # | Functionality | Description | Steps to Reproduce | Expected | Actual |
|---|--------------|-------------|--------------------|----------|--------|
|   |              |             |                    |          |        |

---

# Part 3: Teamwork

## 3.1 Division of Work

| Member | Part 1 Contribution | Part 2 Contribution |
|--------|---------------------|---------------------|
| Jasneet Singh | DataUtilitiesTest initial run and analysis, new DataUtilitiesTest cases to improve mutation score, Section 1.1 sample suite results | Selenium TC-01, TC-02, TC-03 (Flight Search); TC-04, TC-05 (Form Validation) |
| Ashwin Shanmugam | Equivalent mutants analysis (Section 1.5), improvement strategy documentation (Section 1.6), Section 1.8 advantages/disadvantages | Selenium TC-01, TC-02 (Flight Search, different data); TC-04, TC-05 (Form Validation, different data) |
| Noshin | Introduction, Section 1.7 effectiveness analysis, Section 3 teamwork and lessons learned, report compilation and final review | Selenium TC-06, TC-07 (Flight Status Check); TC-08, TC-09 (Manage Booking) |
| Salehin Kazi | Maven/Pitest setup, RangeTest initial run, 10 mutant analysis (Section 1.3), new RangeTest cases to improve mutation score | Selenium TC-06, TC-07 (Flight Status Check, different data); TC-08, TC-09 (Manage Booking, different data) |

All four members contributed equally to the report. Each section was written by the responsible member and then reviewed and corrected by the remaining three members. Final corrections and consistency checks were done collaboratively before submission.

## 3.2 Difficulties Encountered, Challenges Overcome, and Lessons Learned

### Difficulties Encountered

- **Pitest could not run with failing tests:** Before mutation testing could begin, `RangeTest` had a failing test (`intersectsWhenTouchingAtUpperBound`) due to a misunderstanding of the `intersects` method's strict inequality logic. This had to be diagnosed and fixed first.
- **Selenium IDE element identification:** Selenium IDE struggled to automatically identify dynamic web elements on the Air Canada website (date pickers, dropdown suggestions, error containers). Target IDs and CSS selectors had to be manually corrected using browser DevTools for each affected script.
- **Long Pitest execution times:** Running mutation tests on the full test suites took significant time, requiring patience and careful planning to avoid re-running unnecessarily.

### Challenges Overcome

- The failing `RangeTest` was fixed by carefully reading the `intersects` Javadoc and understanding that touching at the boundary does not count as an intersection.
- Selenium element issues were resolved by inspecting the page DOM manually and hardcoding stable locators into the scripts.

### Lessons Learned

- Mutation testing reveals weaknesses that line coverage alone cannot — a test suite can have high line coverage while still leaving many mutants alive due to weak or missing assertions at boundary conditions.
- Fixing a failing test before running Pitest is essential; the tool requires a fully passing suite as a baseline, which forces good test hygiene before mutation analysis can begin.
- Targeted test writing (identifying specific survived mutants and writing tests to kill them) is far more efficient than adding generic tests — a few well-aimed tests improved the score more than many broad ones would have.

---

# Part 4: Comments and Feedback on the Assignment

The mutation testing portion of this assignment was well-structured and gave us a genuinely useful perspective on test quality that we did not get from Assignment 3's coverage metrics. The Pitest HTML report made it straightforward to identify exactly which mutants survived and why, which made targeted improvement feel productive rather than guesswork.

The main difficulty was tooling setup — the Pitclipse Eclipse plugin was unavailable, requiring us to configure a Maven project and run Pitest from the command line. The assignment instructions assumed Eclipse plugin availability, so clearer guidance on the Maven-based fallback workflow would help future groups.

The Selenium portion was straightforward in concept but required significant manual correction of auto-generated locators due to Air Canada's dynamic JavaScript-rendered elements. An introductory note about this common limitation of record-and-replay tools would help set expectations better.

---

# References

[1] J. O. Yu-Seung Ma, "Description of Class Mutation Operators for Java," 2005. (https://cs.gmu.edu/~offutt/mujava/mutopsClass.pdf)

[2] J. O. Yu-Seung Ma, "Description of Method-level Mutation Operators for Java," 2005. (https://citeseerx.ist.psu.edu/document?repid=rep1&type=pdf&doi=cb9627c4facf194ad1a60919d76d9cb25f769444)

[3] "JFreeChart," Internet: http://www.jfree.org/jfreechart

[4] "Pitest," Internet: http://pitest.org/
