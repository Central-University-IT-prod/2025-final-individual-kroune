package io.github.kroune.superfinancer.usecase

import io.github.kroune.superfinancer.domain.usecases.FinanceDataValidator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class FinanceDataValidatorTest {
    @Test
    fun validateGoalTitle_singleCharacter_returnsTrue() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalTitle("A", listOf())
        assertTrue(result)
    }

    @Test
    fun validateGoalTitle_thirtyCharacters_returnsTrue() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalTitle("This is a valid titleWith30 ch", listOf())
        assertTrue(result)
    }

    @Test
    fun validateGoalTitle_exceedingThirtyCharacters_returnsFalse() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalTitle("This is a title that exceeds thirty characters", listOf())
        assertFalse(result)
    }
    @Test
    fun validateGoalTitle_empty_returnsFalse() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalTitle("", listOf())
        assertFalse(result)
    }
    @Test
    fun validateGoalTitle_duplicate_returnsFalse() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalTitle("goalName", listOf("goalName1", "goalName2", "goalName"))
        assertFalse(result)
    }

    @Test
    fun validateGoalTitle_onlyWhitespace_returnsFalse() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalTitle("   ", listOf())
        assertFalse(result)
    }

    @Test
    fun validateGoalCost_emptyInput_returnsFalseAndEmptyString() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalCost("1", "")
        assertFalse(result.second)
        assertEquals("", result.first)
    }

    @Test
    fun validateGoalCost_nonNumericInput_returnsFalseAndEmptyString() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalCost("10", "abc")
        assertFalse(result.second)
        assertEquals("10", result.first)
    }

    @Test
    fun validateGoalCost_validInput() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalCost("10", "100")
        assertTrue(result.second)
        assertEquals("100", result.first)
    }

    @Test
    fun validateGoalCost_validInput2() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalCost("10", "100 ")
        assertTrue(result.second)
        assertEquals("100", result.first)
    }

    @Test
    fun validateGoalCost_validInput3() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalCost("10", "100\n")
        assertTrue(result.second)
        assertEquals("100", result.first)
    }

    @Test
    fun validateGoalCost_validNumericInput_returnsTrueAndCleanedString() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalCost("", "1234.56")
        assertTrue(result.second)
        assertEquals("1234.56", result.first)
    }

    @Test
    fun validateGoalCost_inputExceedingMaxValue_returnsFalseAndString() {
        val validator = FinanceDataValidator()
        val result = validator.validateGoalCost("", "1000000000000000001")
        assertFalse(result.second)
        assertEquals("1000000000000000001", result.first)
    }

    @Test
    fun validateGoalCost_zeroOrNegativeInput_returnsFalseAndCleanedString() {
        val validator = FinanceDataValidator()

        val resultZero = validator.validateGoalCost("", "0")
        assertFalse(resultZero.second)
        assertEquals("0", resultZero.first)

        val resultNegative = validator.validateGoalCost("", "-10.5")
        assertFalse(resultNegative.second)
        assertEquals("-10.5", resultNegative.first)
    }
}
