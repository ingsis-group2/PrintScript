package interpreter.interpreter

import ast.AstNode
import ast.FunctionNode
import interpreter.function.Function
import interpreter.input.InputProvider
import interpreter.variable.Variable

class FunctionInterpreter(private val functionList: List<Function>) : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as FunctionNode
        for (function in functionList) {
            if (function.canHandle(node.function)) {
                return function.run(interpreter, node, symbolTable, inputProvider)
            }
        }
        throw UnsupportedOperationException("Function not found: ${node.function}")
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is FunctionNode
    }
}
