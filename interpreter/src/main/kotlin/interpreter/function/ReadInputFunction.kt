package interpreter.function

import ast.FunctionNode
import interpreter.interpreter.PrintScriptInterpreter
import interpreter.result.PrintResult
import interpreter.result.PromptResult
import interpreter.variable.Variable
import token.TokenType

class ReadInputFunction : Function {
    override val type: TokenType = TokenType.READINPUT

    override fun run(
        interpreter: PrintScriptInterpreter,
        node: FunctionNode,
        symbolTable: MutableMap<Variable, Any>,
    ): Any {
        val expression = interpreter.interpret(node.expression, symbolTable).toString()
        val input = readLine() ?: symbolTable[getVariable(symbolTable, "input")] ?: throw NullPointerException("No input")
        return PromptResult(input.toString(), PrintResult(expression))
    }

    override fun canHandle(nodeType: TokenType): Boolean {
        return nodeType == type
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
}
