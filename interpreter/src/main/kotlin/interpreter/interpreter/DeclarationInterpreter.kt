package interpreter.interpreter

import ast.AstNode
import ast.VariableDeclarationNode
import interpreter.result.MultipleResults
import interpreter.result.Result
import interpreter.variable.Variable

class DeclarationInterpreter : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
    ): Any {
        node as VariableDeclarationNode
        val identifier = node.identifier
        val valueType = node.valueType
        val declarationType = node.declarationType
        val value = interpreter.interpret(node.expression, symbolTable)
        if (value is MultipleResults) {
            symbolTable[Variable(identifier, valueType, declarationType)] = (value.values.first() as Result).value
            return value.values.get(1)
        }
        symbolTable[Variable(identifier, valueType, declarationType)] = value
        return Result(value)
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is VariableDeclarationNode
    }
}
