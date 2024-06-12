package interpreter.interpreter

import ast.AstNode
import ast.IfNode
import ast.NilNode
import interpreter.input.InputProvider
import interpreter.result.Result
import interpreter.variable.Variable

class ConditionalInterpreter : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as IfNode
        val conditionValue = interpreter.interpret(node.condition, symbolTable, inputProvider) as Boolean
        return if (conditionValue) {
            interpreter.interpret(node.thenBlock, symbolTable, inputProvider)
        } else {
            if (node.elseBlock == NilNode) {
                return Result(NilNode)
            }
            interpreter.interpret(node.elseBlock, symbolTable, inputProvider)
        }
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is IfNode
    }
}
