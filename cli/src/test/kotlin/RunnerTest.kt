import cli.FileReader
import cli.PrintScriptRunner
import formatter.PrintScriptFormatterBuilder
import interpreter.builder.InterpreterBuilder
import interpreter.input.InputProvider
import parser.parserBuilder.PrintScriptParserBuilder
import sca.StaticCodeAnalyzerImpl
import java.io.File
import kotlin.test.Test

class RunnerTest {
    private val runner = PrintScriptRunner()

    @Test
    fun testExecuteCode() {
        val file = File("src/test/resources/test.ps")
        val output =
            runner.executeCode(
                FileReader(file.inputStream(), "1.0"),
                PrintScriptParserBuilder().build("1.0"),
                InterpreterBuilder().build("1.0"),
                mutableMapOf(),
                InputProvider(emptyList()),
            )
        output.forEach { println(it) }
    }

    @Test
    fun testFormat() {
        val snippet = "println('  Hello, World!');"
        val formattedCode =
            runner.formatCode(
                FileReader(snippet.byteInputStream(), "1.0"),
                PrintScriptParserBuilder().build("1.0"),
                PrintScriptFormatterBuilder().build("1.0", "src/test/resources/formatter.yaml"),
            )
        println(formattedCode)
    }

    @Test
    fun testAnalyze() {
        val snippet = "println(1 + 1);"
        val errors =
            runner.analyzeCode(
                FileReader(snippet.byteInputStream(), "1.0"),
                PrintScriptParserBuilder().build("1.0"),
                StaticCodeAnalyzerImpl("src/test/resources/sca.yaml", "1.0"),
            )
        errors.forEach { println(it) }
    }

    @Test
    fun executeWithInput() {
        val file = File("src/test/resources/test.ps")
        val output =
            runner.executeCode(
                FileReader(file.inputStream(), "1.1"),
                PrintScriptParserBuilder().build("1.1"),
                InterpreterBuilder().build("1.1"),
                mutableMapOf(),
                InputProvider(listOf("Funciona", "No funciona", "ffff")),
            )
        output.forEach { println(it) }
    }
}
