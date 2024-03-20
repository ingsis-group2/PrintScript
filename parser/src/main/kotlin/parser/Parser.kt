package parser

import ast.Node
import org.example.parser.PrintParse
import token.Token
import token.TokenType
import org.example.parser.semantic.SemanticChecker
import org.example.parser.sintactic.SintacticChecker

data class InvalidTokenInput(override val message: String): Exception(message)
class Parser(
    val syntaxRules: List<SintacticChecker>?,
    val semanticRules: List<SemanticChecker>?
) {
    fun parse(tokenList: List<Token>): Node?{
        return if(tokenList.isNotEmpty() && hasGoodSyntax(tokenList) && hasGoodSemantic(tokenList)){
            chooseParse(tokenList)
        }else{
            null
        }
    }

    private fun chooseParse(tokenList: List<Token>): Node{
        val token = tokenList.first()
        when(token.type){
            TokenType.VARIABLE_KEYWORD -> return DeclarationParse().parse(tokenList)
            TokenType.PRINT -> return PrintParse().parse(tokenList)
            else -> { throw InvalidTokenInput("Invalid TokenInput") }
        }
    }

    private fun hasGoodSemantic(tokenList: List<Token>): Boolean{
        if (semanticRules != null) {
            for(rule in semanticRules){
                if(rule.checkSemantic(tokenList)) continue
                else return false
            }
        }
        return true
    }

    private fun hasGoodSyntax(tokenList: List<Token>): Boolean{
        if (syntaxRules != null) {
            for(rule in syntaxRules){
                if(rule.checkSyntax(tokenList)) continue
                else return false
            }
        }
        return true
    }
}