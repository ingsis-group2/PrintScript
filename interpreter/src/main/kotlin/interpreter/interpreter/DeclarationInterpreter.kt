package interpreter.interpreter

import ast.AstNode
import ast.VariableDeclarationNode
import interpreter.input.InputProvider
import interpreter.result.MultipleResults
import interpreter.result.PrintResult
import interpreter.result.PromptResult
import interpreter.result.Result
import interpreter.variable.Variable

class DeclarationInterpreter : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as VariableDeclarationNode
        val identifier = node.identifier
        val valueType = node.valueType
        val declarationType = node.declarationType
        val value = interpreter.interpret(node.expression, symbolTable, inputProvider)
        if (value is MultipleResults) {
            symbolTable[Variable(identifier, valueType, declarationType)] = (value.values.first() as Result).value
            return value.values.get(1)
        } else if (value is PromptResult) {
            symbolTable[Variable(identifier, valueType, declarationType)] = value.input
            return value
        } else if (value is PrintResult) {
            // when this point is reached, it means the there was a readInput and there was no input to read
            // return the expression to be printed
            return value
        }
        symbolTable[Variable(identifier, valueType, declarationType)] = value
        return Result(value)
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is VariableDeclarationNode
    }
}
