package interpreter.interpreter

import ast.AstNode
import ast.CodeBlock
import interpreter.input.InputProvider
import interpreter.result.InterpreterResult
import interpreter.result.MultipleResults
import interpreter.variable.Variable

class CodeBlockInterpreter : Interpreter {
    override fun interpret(
        node: AstNode?,
        interpreter: PrintScriptInterpreter,
        symbolTable: MutableMap<Variable, Any>,
        inputProvider: InputProvider,
    ): Any {
        node as CodeBlock
        var resultList = listOf<InterpreterResult>()
        for (subNode in node.nodes) {
            resultList = resultList.plus(interpreter.interpret(subNode, symbolTable, inputProvider) as InterpreterResult)
        }
        return MultipleResults(resultList)
    }

    override fun canHandle(node: AstNode): Boolean {
        return node is CodeBlock
    }
}
