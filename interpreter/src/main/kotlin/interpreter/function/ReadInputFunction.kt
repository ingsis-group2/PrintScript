package interpreter.function

import ast.FunctionNode
import interpreter.input.InputProvider
import interpreter.interpreter.PrintScriptInterpreter
import interpreter.result.PrintResult
import interpreter.result.PromptResult
import interpreter.variable.Variable
import token.TokenType

class ReadInputFunction : Function {
    override val type: TokenType = TokenType.READINPUT

    override fun run(
        interpreter: PrintScriptInterpreter,
        node: FunctionNode,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        val expression = interpreter.interpret(node.expression, symbolTable, inputProvider).toString()
        // try to readLine, if not possible, try to get an input from the provider
        val input =
            try {
                readLine() ?: inputProvider.getNextInput()
            } catch (e: IllegalStateException) {
                throw IllegalStateException("No input available")
            }
        return PromptResult(input, PrintResult(expression))
    }

    override fun canHandle(nodeType: TokenType): Boolean {
        return nodeType == type
    }
}
