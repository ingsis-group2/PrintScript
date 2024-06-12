package interpreter.function

import ast.FunctionNode
import interpreter.input.InputProvider
import interpreter.interpreter.PrintScriptInterpreter
import interpreter.variable.Variable
import token.TokenType

interface Function {
    val type: TokenType

    fun run(
        interpreter: PrintScriptInterpreter,
        node: FunctionNode,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any

    fun canHandle(nodeType: TokenType): Boolean
}
