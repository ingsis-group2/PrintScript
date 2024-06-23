@file:Suppress("ktlint:standard:no-wildcard-imports")

package cli

import ast.AstNode
import formatter.Formatter
import interpreter.input.InputProvider
import interpreter.interpreter.PrintScriptInterpreter
import interpreter.result.*
import interpreter.variable.Variable
import parser.parser.Parser
import sca.StaticCodeAnalyzer

class PrintScriptRunner() {
    data class ExecutionOutput(
        val outputs: List<String>,
        val errors: List<String>,
    )

    data class FormatterOutput(
        val formattedCode: String,
        val errors: List<String>,
    )

    data class AnalyzerOutput(
        val reportList: List<String>,
        val errors: List<String>,
    )

    fun executeCode(
        reader: FileReader,
        parser: Parser,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): ExecutionOutput {
        val outputs = mutableListOf<String>()
        val errors = mutableListOf<String>()

        while (reader.canContinue()) {
            val statements = reader.getNextLine()

            var result: InterpreterResult
            for (statement in statements) {
                try {
                    var ast: AstNode = parser.createAST(statement)
                    result = interpreter.interpret(ast, symbolTable, inputProvider) as InterpreterResult
                    addResults(result, outputs)
                } catch (e: Exception) {
                    errors.add("Error while executing: $e")
                }
            }
        }
        return ExecutionOutput(outputs, errors)
    }

    fun formatCode(
        reader: FileReader,
        parser: Parser,
        formatter: Formatter,
    ): FormatterOutput {
        val formattedCode = StringBuilder()
        val errors = mutableListOf<String>()
        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            for (statement in statements) {
                try {
                    val ast = parser.createAST(statement)
                    formattedCode.append(formatter.format(ast))
                } catch (e: Exception) {
                    errors.add("Error while formatting: $e")
                }
            }
        }
        return FormatterOutput(formattedCode.toString(), errors)
    }

    fun analyzeCode(
        reader: FileReader,
        parser: Parser,
        analyzer: StaticCodeAnalyzer,
    ): AnalyzerOutput {
        val reportList = mutableListOf<String>()
        val errors = mutableListOf<String>()
        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            for (statement in statements) {
                try {
                    val ast = parser.createAST(statement)
                    val report = analyzer.analyze(ast)
                    reportList.addAll(report)
                } catch (e: Exception) {
                    errors.add("Error while linting: $e")
                }
            }
        }
        return AnalyzerOutput(reportList, errors)
    }

    private fun addResults(
        result: InterpreterResult,
        output: MutableList<String>,
    ) {
        when (result) {
            is PrintResult -> output.add(result.toPrint)
            is MultipleResults -> for (subResult in result.values) {
                addResults(subResult, output)
            } // Run this function for multiple results.
            is PromptResult -> {
                // Add the printed prompt message
                addResults(result.printPrompt, output)
                // Add the input received from the user
                output.add(result.input)
            }
            is Result -> {
                if (result.value is InterpreterResult) {
                    addResults(result.value as InterpreterResult, output)
                }
            }
        }
    }
}
