import cli.PrintScriptRunner
import java.io.File
import kotlin.test.Test

class RunnerTest {
    private val runner = PrintScriptRunner("1.0", File("src/test/resources/test.ps"))

    @Test
    fun testValidateCode() {
        runner.validateCode()
    }

    @Test
    fun testExecuteCode() {
        runner.executeCode(null)
    }

    @Test
    fun testFormatCode() {
        runner.formatCode(File("src/test/resources/formatter.yaml"))
    }
}
