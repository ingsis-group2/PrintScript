package org.example.sca.rules

import org.example.parser.ASTSingleNode
import org.example.parser.Node
import org.example.token.TokenType

class VariableNamesShouldBeSnakeCase : Rule {
    override fun validate(ast: Node): String? {
        if (ast is ASTSingleNode && ast.token.type == TokenType.VARIABLE_KEYWORD) {
            return validateVariableNames(ast, "[a-z][a-z_0-9]*")
        }
        return null
    }

    private fun validateVariableNames(node: Node?, regex: String): String? {
        if (node is ASTSingleNode && node.node?.token?.type == TokenType.VALUE_IDENTIFIER) {
            val variableName = node.node.token.value
            if (!variableName.matches(Regex(regex))) {
                return "Variable name '$variableName' should be in snake case"
            }
        }
        return null
    }
}