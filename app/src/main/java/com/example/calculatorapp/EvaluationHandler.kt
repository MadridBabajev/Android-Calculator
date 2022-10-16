package com.example.calculatorapp

import kotlin.math.sqrt

class EvaluationHandler {
    companion object {
        fun evaluate(expression: String): String {
            /*
             =================
             Num1 will be num1 and simple actions
             num2 will be numbers after priority symbols
             When the loop reaches another symbol,
             calculate num1 and num2 with the priority action ( if provided )
             and the sum will be new num1
             ( or the final result) */
            val prioritySymbols = setOf('×', '÷', '√')
            var num1 = ""
            var num2 = ""
            var priorityAction = ' '
            var priorityActionToPerform = false
            for (chr: Char in expression) {
                if (priorityActionToPerform) {
                    if (chr.isDigit()) num2 += chr
                    else {
                        if (prioritySymbols.contains(chr)) {
                            priorityAction = chr
                            num1 = calculateTemp(num1, num2, priorityAction)
                        } else {
                            priorityActionToPerform = false
                            num1 = calculateTemp(num1, num2, priorityAction) + chr
                        }
                        num2 = ""
                    }
                }
                else if (chr.isDigit()) num1 += chr
                else {
                    if (prioritySymbols.contains(chr)) {
                        priorityAction = chr
                        priorityActionToPerform = true
                    } else {
                        num1 += chr
                        priorityActionToPerform = false
                    }
                }
            }
            // Final calculations
            return calculateTemp(num1, num2, priorityAction)
        }

        private fun calculateTemp(num1: String, num2: String, priorityAction: Char): String {
            if (num2 == "") {
                val num1Calculated = calculateNum1Parts(num1)
                return (num1Calculated.junkPart + num1Calculated.priorityPart).toString()
            }

            val num1Parts = calculateNum1Parts(num1)
            var output= 0.0
            when (priorityAction) {
                '×' -> output = num1Parts.priorityPart * num2.toDouble()
                '÷' -> output = num1Parts.priorityPart / num2.toDouble()
                '√' -> {
                    output = if (num1Parts.actionAfterPriority != ' ') {
                        num1Parts.priorityPart - sqrt(num2.toDouble())
                    } else {
                        num1Parts.priorityPart * sqrt(num2.toDouble())
                    }
                }
            }
            output += num1Parts.junkPart
            return output.toString()
        }

        private fun calculateNum1Parts(num1: String): Num1Parts {
            val num1Parts = Num1Parts()
            val elements = num1.split("+").toMutableList()

            if (!elements[elements.size -1].contains("-")) {

                num1Parts.priorityPart = elements[elements.size - 1].toDouble()
                num1Parts.junkPart = calculateJunk(elements.subList(0, elements.size - 1))
            } else {
                if (elements[elements.size - 1][elements[elements.size - 1].length - 1] == '-') {
                    num1Parts.priorityPart = elements[elements.size - 1]
                        .substring(0, elements[elements.size - 1].length - 1).toDouble()
                    // Probably should also calculate the junk, but as for now, let it be
                    num1Parts.junkPart = 0.0
                    num1Parts.actionAfterPriority = '-'
                } else {
                    val lastElementSplit = elements[elements.size - 1].split("-")
                    val lastElementSubList = recoverAfterMinusSplit(
                        lastElementSplit.subList(
                            0,
                            lastElementSplit.size - 1
                        )
                    )
                    val otherElementsSublist = elements.subList(0, elements.size - 1)
                    num1Parts.priorityPart =
                        0.0 - lastElementSplit[lastElementSplit.size - 1].toDouble()
                    otherElementsSublist.let { list1 -> lastElementSubList.let(list1::addAll) }
                    num1Parts.junkPart = calculateJunk(otherElementsSublist)
                }
            }
            return num1Parts
        }

        private fun recoverAfterMinusSplit(lastElementsJunk: List<String>): MutableList<String> {
            val retList: MutableList<String> = lastElementsJunk.toMutableList()
            var firstCycle = true
            for (element: String in lastElementsJunk) {
                if (firstCycle) {
                    firstCycle = false
                    retList.add(element)
                } else {
                    retList.add("-$element")
                }
            }
            return retList
        }

        private fun calculateJunk(junkPart: MutableList<String>): Double {
            var retValue = 0.0
            for (element: String in junkPart) {
                if (!element.contains("-")) {
                    retValue += element.toDouble()
                } else {
                    var firstElement = true
                    for (innerElement: String in element.split("-")) {
                        if (firstElement) {
                            firstElement = false
                            retValue += innerElement.toDouble()
                        } else {
                            retValue -= innerElement.toDouble()
                        }
                    }
                }
            }
            return retValue
        }
    }
}
