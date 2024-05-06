@file:Suppress("ktlint:standard:no-wildcard-imports")

package cli

import ast.AstNode
import formatter.PrintScriptFormatter
import formatter.PrintScriptFormatterBuilder
import interpreter.builder.InterpreterBuilder
import interpreter.result.*
import interpreter.variable.Variable
import lexer.factory.LexerBuilder
import parser.parserBuilder.PrintScriptParserBuilder
import sca.StaticCodeAnalyzerImpl
import token.Token
import token.TokenType
import java.io.File

class PrintScriptRunner(val version: String, val source: File) {
    private val lexer = LexerBuilder().build(version)
    private val parser = PrintScriptParserBuilder().build(version)
    private val interpreter = InterpreterBuilder().build(version)
    private val symbolTable: MutableMap<Variable, Any> = mutableMapOf()
    val reader = FileReader(source.inputStream(), version)

    fun validateCode() {
        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            for (statement in statements) {
                try {
                    parser.createAST(statement)
                    println("Validation successful")
                } catch (e: Exception) {
                    println("error in parsing: $e")
                }
            }
        }
    }

    fun executeCode(envFile: File?) {
        if (envFile != null) {
            insertEnvironmentVariablesInSymbolTable(envFile)
        }
        symbolTable.put(Variable("input", TokenType.STRINGTYPE, TokenType.CONST), "hola")
        while (reader.canContinue()) {
            val statements = reader.getNextLine()

            var result: InterpreterResult
            for (statement in statements) {
                try {
                    var ast: AstNode = parser.createAST(statement)
                    if (statementContainsReadInput(statement)) {
                        val index = getReadInputTokenIndex(statement)
                        val input = getInput(statement, index)
                        ast = createNewAst(statement, input, index)
                    }
                    result = interpreter.interpret(ast, symbolTable) as InterpreterResult
                    printResults(result)
                } catch (e: Exception) {
                    println("error in execution: $e")
                    break
                }
            }
        }
    }

    fun formatCode(config: File?) {
        val formatter =
            PrintScriptFormatterBuilder().build(
                version,
                config?.path ?: throw NullPointerException("Config file null"),
            ) as PrintScriptFormatter
        val file = File(source.path)
        var text = ""
        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            for (statement in statements) {
                try {
                    val ast = parser.createAST(statement)
                    val line = ast?.let { formatter.format(it) }
                    text += line
                } catch (e: Exception) {
                    println("error in formatting: $e")
                }
            }
        }
        file.writeText(text)
    }

    fun analyzeCode(config: File?) {
        val sca = StaticCodeAnalyzerImpl(requireNotNull(config?.path) { "Expected config file path for sca." }, version)
        val errorList = mutableListOf<String>()
        while (reader.canContinue()) {
            val statements = reader.getNextLine()
            for (statement in statements) {
                try {
                    val ast = parser.createAST(statement)
                    val errors = ast?.let { sca.analyze(it) }
                    errorList += errors ?: emptyList()
                } catch (e: Exception) {
                    println("error in analyzing: $e")
                }
            }
        }
        errorList.forEach(::println)
    }

    private fun createNewAst(
        statement: List<Token>,
        input: String,
        index: Int,
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

    private fun insertEnvironmentVariablesInSymbolTable(envFile: File?) {
        // Read file contents line by line
        val lines = File(envFile!!.path).readLines()

        // Print each line of the file
        lines.forEach {
            val pair = it.split("=")
            symbolTable[Variable(pair[0], TokenType.STRINGTYPE, TokenType.CONST)] = pair[1]
        }
    }

    private fun printResults(result: InterpreterResult) {
        when (result) {
            is PrintResult -> println(result.toPrint)
            is Result -> Unit // If the result is not a print do nothing.
            is MultipleResults -> for (subResult in result.values) {
                printResults(subResult)
            } // Run this function for multiple results.
            is PromptResult -> {
                printResults(result.printPrompt)
            }
        }
    }
}
