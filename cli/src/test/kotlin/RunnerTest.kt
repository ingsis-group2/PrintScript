import cli.FileReader
import cli.PrintScriptRunner
import formatter.PrintScriptFormatterBuilder
import interpreter.builder.InterpreterBuilder
import lexer.factory.LexerBuilder
import parser.parserBuilder.PrintScriptParserBuilder
import sca.StaticCodeAnalyzerImpl
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
                LexerBuilder().build("1.0"),
                PrintScriptParserBuilder().build("1.0"),
                InterpreterBuilder().build("1.0"),
                mutableMapOf(),
            )
        assertEquals(1, output.outputs.size)
        assertEquals("1", output.outputs[0])
        assertEquals(0, output.errors.size)
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
}
