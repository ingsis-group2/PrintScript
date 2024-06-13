package interpreter.interpreter

import ast.AstNode
import ast.PrintNode
import interpreter.input.InputProvider
import interpreter.result.PrintResult
import interpreter.variable.Variable

class PrintInterpreter : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as PrintNode
        return PrintResult(interpreter.interpret(node.expression, symbolTable, inputProvider).toString())
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is PrintNode
    }
}
