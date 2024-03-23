package parser.sintactic.commons

import parser.Type
import parser.sintactic.IsAssignation
import parser.sintactic.IsDeclarative
import parser.sintactic.IsPrint
import parser.sintactic.SintacticChecker
import token.Token
import token.TokenType

class HasCombination : SintacticChecker {
    override fun checkSyntax(tokenList: List<Token>): Boolean { // If length is not enough, it will be NIL
        val type = checkParseType(tokenList)
        return when (type) {
            Type.ASSIGNATION -> checkStructure(tokenList, 2, tokenList.size - 1)
            Type.PRINT -> checkStructure(tokenList, 2, tokenList.size - 2)
            Type.DECLARATION -> checkStructure(tokenList, 5, tokenList.size - 1)
            Type.NIL -> false
        }
    }

    private fun checkStructure(
        tokenList: List<Token>,
        from: Int,
        to: Int,
    ): Boolean {
        return if (JustHaveNumber().checkSyntax(tokenList)) {
            checkNumberContent(tokenList.subList(from, to))
        } else {
            checkContent(tokenList.subList(from, to))
        }
    }

    private fun checkContent(tokenList: List<Token>): Boolean {
        if (tokenList.size == 1) return false // is just a simple expression, no combination!!!
        var index = 0
        while (index < tokenList.size) {
            when (tokenList[index].type) {
                TokenType.STRING, TokenType.NUMBER, TokenType.VALUE_IDENTIFIER -> {
                    index += 1
                }
                TokenType.PLUS -> if (isNextLiteral(tokenList, index)) index += 1 else return false
                else -> return false
            }
        }
        return true
    }

    private fun checkNumberContent(tokenList: List<Token>): Boolean {
        if (tokenList.size == 1) return false
        var index = 0
        while (index < tokenList.size) {
            when (tokenList[index].type) {
                TokenType.NUMBER, TokenType.VALUE_IDENTIFIER -> index += 1
                TokenType.PLUS, TokenType.STAR, TokenType.SLASH, TokenType.MINUS -> {
                    if (isNextLiteral(tokenList, index)) index += 1 else return false
                }
                else -> return false
            }
        }
        return true
    }

    private fun isNextLiteral(
        tokenList: List<Token>,
        index: Int,
    ): Boolean {
        return if (index < tokenList.size - 1) {
            when (tokenList[index + 1].type) {
                TokenType.STRING, TokenType.NUMBER, TokenType.VALUE_IDENTIFIER -> true
                else -> false
            }
        } else {
            return false
        }
    }

    private fun checkParseType(tokenList: List<Token>): Type {
        return when {
            IsDeclarative().checkSyntax(tokenList) -> Type.DECLARATION
            IsPrint().checkSyntax(tokenList) -> Type.PRINT
            IsAssignation().checkSyntax(tokenList) -> Type.ASSIGNATION
            else -> Type.NIL
        }
    }
}
