import cli.FileReader
import cli.PrintScriptRunner
import formatter.PrintScriptFormatterBuilder
import interpreter.builder.InterpreterBuilder
import interpreter.input.InputProvider
import interpreter.variable.Variable
import org.junit.jupiter.api.Assertions.assertFalse
import parser.parserBuilder.PrintScriptParserBuilder
import sca.StaticCodeAnalyzerImpl
import token.TokenType
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class RunnerTest {
    private val runner = PrintScriptRunner()

    @Test
    fun testExecuteCode() {
        val file = File("src/test/resources/SimplePrint.ps")
        val output =
            runner.executeCode(
                FileReader(file.inputStream(), "1.0"),
                PrintScriptParserBuilder().build("1.0"),
                InterpreterBuilder().build("1.0"),
                mutableMapOf(),
                InputProvider(emptyList()),
            )
        assertEquals(1, output.outputs.size)
        assertEquals("1", output.outputs[0])
        assertEquals(0, output.errors.size)
    }

    @Test
    fun testExecuteCodeWithError() {
        val file = File("src/test/resources/Error.ps")
        val output =
            runner.executeCode(
                FileReader(file.inputStream(), "1.0"),
                PrintScriptParserBuilder().build("1.0"),
                InterpreterBuilder().build("1.0"),
                mutableMapOf(),
                InputProvider(emptyList()),
            )
        assertEquals(0, output.outputs.size)
        assertEquals(1, output.errors.size)
    }

    @Test
    fun testFormat() {
        val file = File("src/test/resources/UnformattedSnippet.ps")
        val formattedCode =
            runner.formatCode(
                FileReader(file.inputStream(), "1.0"),
                PrintScriptParserBuilder().build("1.0"),
                PrintScriptFormatterBuilder().build("1.0", "src/test/resources/formatter.yaml"),
            )
        println(formattedCode)
    }

    @Test
    fun testAnalyze() {
        val file = File("src/test/resources/FailLint.ps")
        val output =
            runner.analyzeCode(
                FileReader(file.inputStream(), "1.0"),
                PrintScriptParserBuilder().build("1.0"),
                StaticCodeAnalyzerImpl("src/test/resources/sca.yaml", "1.0"),
            )
        assertEquals(1, output.reportList.size)
        assertEquals(0, output.errors.size)
    }

    @Test
    fun executeWithInput() {
        val file = File("src/test/resources/ReadInput.ps")
        val output =
            runner.executeCode(
                FileReader(file.inputStream(), "1.1"),
                PrintScriptParserBuilder().build("1.1"),
                InterpreterBuilder().build("1.1"),
                mutableMapOf(),
                InputProvider(listOf("100", "algo")),
            )
        assertEquals(3, output.outputs.size)
        assertEquals("Escribe un número: ", output.outputs[0])
        assertEquals("100", output.outputs[1])
        assertEquals("El número es: 100", output.outputs[2])
    }

    @Test
    fun executeWithoutInputShouldReturnThePrompt() {
        val file = File("src/test/resources/ReadInput.ps")
        val output =
            runner.executeCode(
                FileReader(file.inputStream(), "1.1"),
                PrintScriptParserBuilder().build("1.1"),
                InterpreterBuilder().build("1.1"),
                mutableMapOf(),
                InputProvider(listOf()),
            )
        assertEquals(1, output.outputs.size)
        assertEquals("Escribe un número: ", output.outputs[0])
        assertFalse(output.errors.isEmpty())
    }

    @Test
    fun executeCodeWithReadEnv() {
        val file = File("src/test/resources/ReadEnv.ps")
        val envs = mapOf("ENV_VAR" to "100")
        val symbolTable = mutableMapOf<Variable, Any>()
        envs.forEach { (key, value) ->
            symbolTable[Variable(key, TokenType.STRINGTYPE, TokenType.CONST)] = value
        }
        val output =
            runner.executeCode(
                FileReader(file.inputStream(), "1.1"),
                PrintScriptParserBuilder().build("1.1"),
                InterpreterBuilder().build("1.1"),
                symbolTable,
                InputProvider(listOf()),
            )
        assertEquals(1, output.outputs.size)
        assertEquals("100", output.outputs[0])
    }
}
