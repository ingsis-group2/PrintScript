package interpreter.function

import ast.FunctionNode
import interpreter.input.InputProvider
import interpreter.interpreter.PrintScriptInterpreter
import interpreter.variable.Variable
import token.TokenType

class ReadEnvFunction : Function {
    override val type: TokenType = TokenType.READENV

    override fun run(
        interpreter: PrintScriptInterpreter,
        node: FunctionNode,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        return interpreter.interpret(node.expression, symbolTable, inputProvider)
    }

    override fun canHandle(nodeType: TokenType): Boolean {
        return nodeType == type
    }
}
