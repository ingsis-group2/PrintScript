package interpreter.interpreter

import ast.AssignmentNode
import ast.AstNode
import interpreter.input.InputProvider
import interpreter.result.Result
import interpreter.variable.Variable
import token.TokenType

class AssignationInterpreter : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as AssignmentNode
        val identifier = node.identifier
        val variable: Variable = getVariable(symbolTable, identifier)
        if (variable.declarationType != TokenType.LET) {
            throw UnsupportedOperationException(
                "Cannot assign a constant.  In position " +
                    node.position.start.string() + ":" + node.position.end.string(),
            )
        }

        if (node.valueType != variable.valueType) {
            throw UnsupportedOperationException(
                "Cannot assign a value of type ${node.valueType} " +
                    "to a variable of type ${variable.valueType}. " +
                    "In position " + node.position.start.string() + ":" + node.position.end.string(),
            )
        }
        symbolTable[variable] = interpreter.interpret(node.expression, symbolTable, inputProvider)
        val value = interpreter.interpret(node.expression, symbolTable, inputProvider)

        return Result(value)
    }

    private fun getVariable(
        symbolTable: MutableMap<Variable, Any>,
        identifier: String,
    ): Variable {
        for (variableSymbol in symbolTable.keys) {
            if (variableSymbol.name == identifier) {
                return variableSymbol
            }
        }
        throw NullPointerException("Variable not declared")
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is AssignmentNode
    }
}
