@file:Suppress("ktlint:standard:no-wildcard-imports")

package cli

import ast.AstNode
import formatter.Formatter
import interpreter.interpreter.PrintScriptInterpreter
import interpreter.result.*
import interpreter.variable.Variable
import lexer.Lexer
import parser.parser.Parser
import sca.StaticCodeAnalyzer
import token.Token
import token.TokenType
import java.io.File

class PrintScriptRunner() {
    fun executeCode(
        reader: FileReader,
        lexer: Lexer,
        parser: Parser,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        envFile: File? = null,
    ): List<String> {
        val output = mutableListOf<String>()
        if (envFile != null) {
            insertEnvironmentVariablesInSymbolTable(symbolTable, envFile)
        }
        while (reader.canContinue()) {
            val statements = reader.getNextLine()

            var result: InterpreterResult
            for (statement in statements) {
                try {
                    var ast: AstNode = parser.createAST(statement)
                    if (statementContainsReadInput(statement)) {
                        val index = getReadInputTokenIndex(statement)
                        val input = getInput(statement, index)
                        ast = createNewAst(statement, input, index, lexer, parser)
                    }
                    result = interpreter.interpret(ast, symbolTable) as InterpreterResult
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

    private fun createNewAst(
        statement: List<Token>,
        input: String,
        index: Int,
        lexer: Lexer,
        parser: Parser,
    ): AstNode {
        val mutableStatement = statement.toMutableList()
        val size = mutableStatement.size - 1
        for (i in index + 1..<size) {
            mutableStatement.removeAt(index + 1)
        }
        mutableStatement[index] = lexer.lex(input)[0]
        return parser.createAST(mutableStatement)
    }

    private fun getInput(
        statement: List<Token>,
        index: Int,
    ): String {
        val promptToken = statement[index + 2]
        println(promptToken.value)
        val input = readLine() ?: throw NullPointerException("Input form cli is null")
        return input
    }

    private fun statementContainsReadInput(statement: List<Token>): Boolean {
        return getReadInputTokenIndex(statement) != -1
    }

    private fun getReadInputTokenIndex(statement: List<Token>): Int {
        for ((index, token) in statement.withIndex()) {
            if (token.type == TokenType.READINPUT) {
                return index
            }
        }
        return -1
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
            is Result -> Unit // If the result is not a print do nothing.
            is MultipleResults -> for (subResult in result.values) {
                addResults(subResult, output)
            } // Run this function for multiple results.
            is PromptResult -> {
                addResults(result.printPrompt, output)
            }
        }
    }
}
