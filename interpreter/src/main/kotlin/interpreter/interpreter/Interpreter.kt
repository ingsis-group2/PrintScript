package interpreter.interpreter

import ast.AstNode
import interpreter.input.InputProvider
import interpreter.variable.Variable

interface Interpreter {
    fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any

    fun canHandle(node: AstNode): Boolean
}
