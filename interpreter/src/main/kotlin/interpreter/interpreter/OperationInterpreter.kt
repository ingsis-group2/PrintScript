package interpreter.interpreter

import ast.AstNode
import ast.BinaryOperationNode
import interpreter.input.InputProvider
import interpreter.operation.Operation
import interpreter.variable.Variable

class OperationInterpreter(private val operations: List<Operation>) : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as BinaryOperationNode
        val l = interpreter.interpret(node.left, symbolTable, inputProvider)
        val r = interpreter.interpret(node.right, symbolTable, inputProvider)
        for (operation in operations) {
            if (operation.symbol == node.operator) {
                return operation.resolve(l, r)
            }
        }
        throw UnsupportedOperationException("Operation interpreter does not have a resolver for symbol: " + node.operator)
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is BinaryOperationNode
    }
}
