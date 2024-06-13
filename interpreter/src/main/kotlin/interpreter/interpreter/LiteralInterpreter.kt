package interpreter.interpreter

import ast.AstNode
import ast.LiteralNode
import ast.NilNode
import interpreter.input.InputProvider
import interpreter.literal.Literal
import interpreter.variable.Variable
import token.TokenType

class LiteralInterpreter(private val literals: List<Literal>) : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as LiteralNode
        val type = node.type
        if (type == TokenType.VALUEIDENTIFIERLITERAL) {
            val variable = getVariable(symbolTable, node.value)
            val value = symbolTable[variable] ?: throw NullPointerException("Variable not declared: ${variable.name}")
            if (value is NilNode) throw NullPointerException("Variable not initialized: ${variable.name}")
            return value
        } else {
            return getLiteralValue(literals, node)
        }
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is LiteralNode
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

    private fun getLiteralValue(
        literals: List<Literal>,
        node: LiteralNode,
    ): Any {
        val type = node.type
        for (literal in literals) {
            if (literal.type == type) {
                return literal.eval(node)
            }
        }
        throw UnsupportedOperationException("Unrecognized literal type: $type")
    }
}
