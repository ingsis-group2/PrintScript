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
import token.TokenType
import java.io.File

class PrintScriptRunner() {
    fun executeCode(
        reader: FileReader,
        parser: Parser,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): List<String> {
        val output = mutableListOf<String>()

        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            var result: InterpreterResult
            for (statement in statements) {
                try {
                    var ast: AstNode = parser.createAST(statement)
                    result = interpreter.interpret(ast, symbolTable, inputProvider) as InterpreterResult
                    addResults(result, output)
                } catch (e: Exception) {
                    println("error in execution: $e")
                    break
                }
            }
        }
        return output
    }

    fun formatCode(
        reader: FileReader,
        parser: Parser,
        formatter: Formatter,
    ): String {
        val formattedCode = StringBuilder()
        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            for (statement in statements) {
                val ast = parser.createAST(statement)
                formattedCode.append(formatter.format(ast))
            }
        }
        return formattedCode.toString()
    }

    fun analyzeCode(
        reader: FileReader,
        parser: Parser,
        analyzer: StaticCodeAnalyzer,
    ): List<String> {
        val output = mutableListOf<String>()
        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            for (statement in statements) {
                try {
                    val ast = parser.createAST(statement)
                    output.addAll(analyzer.analyze(ast))
                } catch (e: Exception) {
                    output.add("error in analysis: $e")
                }
            }
        }
        return output
    }

    private fun insertEnvironmentVariablesInSymbolTable(
        symbolTable: MutableMap<Variable, Any>,
        envFile: File?,
    ) {
        // Read file contents line by line
        val lines = File(envFile!!.path).readLines()

        // Print each line of the file
        lines.forEach {
            val pair = it.split("=")
            symbolTable[Variable(pair[0], TokenType.STRINGTYPE, TokenType.CONST)] = pair[1]
        }
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
