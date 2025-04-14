package io.github.kroune.superfinancer.domain.usecases

class FinanceDataValidator : FinanceDataValidatorI {
    override fun validateGoalTitle(title: String, otherGoals: List<String>): Boolean {
        return title.length in FinanceDataValidatorConstants.validTitleLength
                && title.isNotBlank() && !otherGoals.contains(title)
    }

    override fun validateGoalCost(oldCost: String, newCost: String): Pair<String, Boolean> {
        var goalCost = oldCost
        val isGoalCostValid = run {
            if (newCost == "") {
                goalCost = ""
                return@run false
            }
            val input = newCost.toFloatOrNull() ?: return@run false
            goalCost = (newCost.filter { char -> char != '\n' && !char.isWhitespace() })
            // pretty sure it is enough for most people
            if (input > FinanceDataValidatorConstants.maxCost)
                return@run false
            if (input <= 0) {
                return@run false
            }
            return@run true
        }
        return goalCost to isGoalCostValid
    }
}
