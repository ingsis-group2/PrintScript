@file:Suppress("ktlint:standard:no-wildcard-imports")

package interpreter

import ast.*
import interpreter.builder.InterpreterBuilder
import interpreter.result.MultipleResults
import interpreter.result.PrintResult
import interpreter.result.Result
import interpreter.variable.Variable
import lexer.PrintScriptLexer
import lexer.getTokenMapV11
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import parser.InvalidDataTypeException
import parser.parserBuilder.printScript11.PrintScript11ParserBuilder
import position.Coordinate
import position.TokenPosition
import token.TokenType
import kotlin.test.assertEquals

class PrintScriptInterpreterTest {
    private val interpreter = InterpreterBuilder().build("1.1")
    private val lexer = PrintScriptLexer(getTokenMapV11())
    private val parser = PrintScript11ParserBuilder().build()
    private val symbolTable = mutableMapOf<Variable, Any>()

    private fun getTree(code: String): AstNode {
        val tokenList = lexer.lex(code)
//        for (token in tokenList) {
//            println(token.value + " "+ token.type)
//        }
        return parser.createAST(tokenList) ?: throw NullPointerException("Parser return null")
    }

    @BeforeEach
    fun clearSymbolTable() {
        symbolTable.clear()
    }

    @Test
    fun testBasicPrint() {
        val string = "println(1);"
        val result = interpreter.interpret(getTree(string), symbolTable)
        result as PrintResult
        assertEquals("1", result.toPrint)
    }

    @Test
    fun variableDeclarationTest() {
        val string = "let num: number = 3;"
        val result = interpreter.interpret(getTree(string), symbolTable)
        result as Result
        assertEquals(3, result.value)
    }

    @Test
    fun testStringPlusNumberVariableDeclaration() {
        val string = "println('tista' + 3);"
        val result = interpreter.interpret(getTree(string), symbolTable)
        result as PrintResult
        assertEquals("tista3", result.toPrint)
    }

    @Test
    fun testDivisionPrint() {
        val string = "println(3 / 3);"
        val result = interpreter.interpret(getTree(string), symbolTable)
        result as PrintResult
        assertEquals("1", result.toPrint)
    }

    @Test
    fun testSubPrint() {
        val string = "println(3 - 3);"
        val result = interpreter.interpret(getTree(string), symbolTable)
        result as PrintResult
        assertEquals("0", result.toPrint)
    }

    @Test
    fun testMulPrint() {
        val string = "println(3 * 3);"
        val result = interpreter.interpret(getTree(string), symbolTable)
        result as PrintResult
        assertEquals("9", result.toPrint)
    }

    @Test
    fun testDeclareVariableAndThenPrintIt() {
        val string = "let num: number = 3;"
        interpreter.interpret(getTree(string), symbolTable)
        val string2 = "println(num);"
        val result = interpreter.interpret(getTree(string2), symbolTable)
        result as PrintResult
        assertEquals("3", result.toPrint)
    }

    @Test
    fun testDeclareVariableOfTypeStringWithNumberLiteral() {
        val string = "let num: number = 'hola';"
        assertThrows<InvalidDataTypeException> { interpreter.interpret(getTree(string), symbolTable) }
    }

    @Test
    fun testDeclareVariableWithTypeStringAndThenAssignANumber() {
        val string = "let str: string;"
        interpreter.interpret(getTree(string), symbolTable)
        val string2 = "str = 3;"

        assertThrows<UnsupportedOperationException> { interpreter.interpret(getTree(string2), symbolTable) }
    }

    @Test
    fun testDeclareVariableWithTypeNumberAndThenAssignAString() {
        val string = "let num: number;"
        interpreter.interpret(getTree(string), symbolTable)
        val string2 = "num = 'hola';"

        assertThrows<UnsupportedOperationException> { interpreter.interpret(getTree(string2), symbolTable) }
    }

    @Test
    fun testFloatAndStringConcatenation() {
        val string = "let num: number = 3.14;"
        interpreter.interpret(getTree(string), symbolTable)
        val string2 = "println('pi is ' + num);"
        val result = interpreter.interpret(getTree(string2), symbolTable)
        result as PrintResult
        assertEquals("pi is 3.14", result.toPrint)
    }

    @Test
    fun testDecimalDeclarationThenPrintItAnOperation() {
        val string = "let num: number = 3.14;"
        interpreter.interpret(getTree(string), symbolTable)
        val string2 = "println(num/2);"
        val result = interpreter.interpret(getTree(string2), symbolTable)
        result as PrintResult
        assertEquals("1.57", result.toPrint)
    }

    @Test
    fun testDeclareVariableWithoutValue() {
        val string = "let num: number;"
        interpreter.interpret(getTree(string), symbolTable)
        assertEquals("num", symbolTable.keys.elementAt(0).name)
    }

    @Test
    fun testAssignDeclarationWithoutValue() {
        val string = "let num: number;"
        val string2 = "num = 3;"
        interpreter.interpret(getTree(string), symbolTable)
        val result = interpreter.interpret(getTree(string2), symbolTable)
        result as Result
        assertEquals(3, result.value)
    }

    @Test
    fun testAssignDeclarationWithoutValueAndThenPrintIt() {
        val string = "let s: string;"
        val string2 = "s = 'hola';"
        interpreter.interpret(getTree(string), symbolTable)
        interpreter.interpret(getTree(string2), symbolTable)
        val string3 = "println(s);"
        val result = interpreter.interpret(getTree(string3), symbolTable)
        result as PrintResult
        assertEquals("hola", result.toPrint)
    }

    @Test
    fun testConstDeclarationCannotBeReassigned() {
        val string = "const num: number = 3;"
        val string2 = "num = 4;"
        interpreter.interpret(getTree(string), symbolTable)
        assertThrows<UnsupportedOperationException> {
            interpreter.interpret(getTree(string2), symbolTable)
        }
    }

    @Test
    fun testLetDeclarationCanBeReassigned() {
        val string = "let num: number = 3;"
        val string2 = "num = 4;"
        interpreter.interpret(getTree(string), symbolTable)
        val result = interpreter.interpret(getTree(string2), symbolTable)
        result as Result
        assertEquals(4, result.value)
    }

    @Test
    fun testEnvVariableIsCorrectlyPrinted() {
        symbolTable[Variable("PRINT_ENV", TokenType.STRINGTYPE, TokenType.CONST)] = "hola"
        val tokenPosition = TokenPosition(Coordinate(1, 0), Coordinate(1, 0))
        val literalNode = LiteralNode("PRINT_ENV", TokenType.VALUEIDENTIFIERLITERAL, tokenPosition)
        val tree = FunctionNode(TokenType.READENV, literalNode, tokenPosition)
        val result = interpreter.interpret(tree, symbolTable)
        assertEquals("hola", result) // No Interpreter result because it's an internal test.
    }

    @Test
    fun testBooleanLiteralReturn() {
        val string = "let bool: boolean = true;"
        val result = interpreter.interpret(getTree(string), symbolTable) as Result
        assertEquals(true, result.value)
    }

    @Test
    fun testIfNodeThatHasTrueConditional() {
        val string1 = "println('hola');"
        val string2 = "println('chau');"
        val tokenPosition = TokenPosition(Coordinate(1, 0), Coordinate(1, 0))
        val literalNode = LiteralNode("true", TokenType.BOOLEANLITERAL, tokenPosition)
        val thenBlock = CodeBlock(listOf(getTree(string1)), tokenPosition)
        val elseBlock = CodeBlock(listOf(getTree(string2)), tokenPosition)
        val ifNode = IfNode(literalNode, thenBlock, elseBlock, tokenPosition)
        val result = interpreter.interpret(ifNode, symbolTable) as MultipleResults
        val printResult = result.values[0] as PrintResult
        assertEquals("hola", printResult.toPrint)
    }

    @Test
    fun testIfNodeThatHasFalseConditional() {
        val string1 = "println('hola');"
        val string2 = "println('chau');"
        val tokenPosition = TokenPosition(Coordinate(1, 0), Coordinate(1, 0))
        val literalNode = LiteralNode("false", TokenType.BOOLEANLITERAL, tokenPosition)
        val thenBlock = CodeBlock(listOf(getTree(string1)), tokenPosition)
        val elseBlock = CodeBlock(listOf(getTree(string2)), tokenPosition)
        val ifNode = IfNode(literalNode, thenBlock, elseBlock, tokenPosition)
        val result = interpreter.interpret(ifNode, symbolTable) as MultipleResults
        val printResult = result.values[0] as PrintResult
        assertEquals("chau", printResult.toPrint)
    }

    @Test
    fun testIfNodeWithoutElseBlockWhenConditionIsFalse() {
        val string1 = "println('hola');"
        val tokenPosition = TokenPosition(Coordinate(1, 0), Coordinate(1, 0))
        val literalNode = LiteralNode("false", TokenType.BOOLEANLITERAL, tokenPosition)
        val thenBlock = CodeBlock(listOf(getTree(string1)), tokenPosition)
        val elseBlock = NilNode
        val ifNode = IfNode(literalNode, thenBlock, elseBlock, tokenPosition)
        val result: Result = interpreter.interpret(ifNode, symbolTable) as Result
        assertEquals(Result(NilNode), result)
    }

    @Test
    fun testBuilderReturnsCorrectVersion() {
        val oldInterpreter = InterpreterBuilder().build("1.0")
        val string = "let a:boolean = true;"
        assertThrows<UnsupportedOperationException> { oldInterpreter.interpret(getTree(string), symbolTable) }
    }
}
