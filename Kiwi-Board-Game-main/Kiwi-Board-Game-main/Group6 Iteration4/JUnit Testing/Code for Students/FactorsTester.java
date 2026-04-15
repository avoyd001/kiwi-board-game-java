import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FactorsTester {

	// Tests for perfect()
	@Test
	void testPerfect1() {
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.perfect(0));
	}

	@Test
	void testPerfect2() {
		assertFalse(FactorsUtility.perfect(1));
	}

	@Test
	void testPerfect3() {
		assertTrue(FactorsUtility.perfect(6));
	}

	@Test
	void testPerfect4() {
		boolean expected = false;
		assertEquals(expected, FactorsUtility.perfect(7));
	}

	
	// Tests for factor()
	@Test
	void testFactorValid() {
		assertTrue(FactorsUtility.factor(10, 2)); // 2 is a factor of 10
		assertFalse(FactorsUtility.factor(10, 3)); // 3 is not a factor of 10
	}

	@Test
	void testFactorInvalid() {
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(-10, 2)); // negative number
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(10, 0));  // b < 1
	}

	// Tests for getFactors()
	@Test
	void testGetFactorsNormal() {
		ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(1, 2, 3));
		assertEquals(expected, FactorsUtility.getFactors(6));
	}

	@Test
	void testGetFactorsOne() {
		ArrayList<Integer> expected = new ArrayList<>();
		assertEquals(expected, FactorsUtility.getFactors(1));
	}

	@Test
	void testGetFactorsZero() {
		ArrayList<Integer> expected = new ArrayList<>();
		assertEquals(expected, FactorsUtility.getFactors(0));
	}

	@Test
	void testGetFactorsException() {
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.getFactors(-5));
	}
}
