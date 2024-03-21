package parser.sintactic.commons

import org.example.parser.sintactic.SintacticChecker
import parser.sintactic.isAssignation
import org.example.parser.sintactic.declarative.isDeclarative
import parser.sintactic.isPrint
import parser.Type
import token.Token
import token.TokenType

class justHaveNumber: SintacticChecker {
    override fun checkSyntax(tokenList: List<Token>): Boolean {
        var type = checkParseType(tokenList)
        return when(type){
            Type.ASSIGNATION -> checkContent(tokenList, 2, tokenList.size - 1) //content position depends on the type of parse
            Type.PRINT -> checkContent(tokenList, 2, tokenList.size - 2)
            Type.DECLARATION -> checkContent(tokenList, 5, tokenList.size - 1)
            Type.NIL -> false
        }
    }

    private fun checkContent(tokenList: List<Token>, from: Int, to: Int): Boolean{
        return isArithmetic(tokenList.subList(from, to))
    }

    private fun isArithmetic(tokenList: List<Token>): Boolean{
        var index = 0
        while(index < tokenList.size){
            when(tokenList[index].type){
                TokenType.NUMBER, TokenType.VALUE_IDENTIFIER -> index+=1
                TokenType.PLUS, TokenType.MINUS, TokenType.SLASH, TokenType.STAR -> {
                    if(isNextNumber(tokenList, index)) index+=1 else return false
                }
                else -> return false
            }
        }
        return true
    }

    private fun isNextNumber(tokenList: List<Token>, index: Int): Boolean{
        return if(index < tokenList.size-1){
            when(tokenList[index + 1].type){
                TokenType.NUMBER, TokenType.VALUE_IDENTIFIER -> true
                else -> false
            }
        }else{
            return false
        }
    }

    private fun checkParseType(tokenList: List<Token>): Type {
        return when{
            isDeclarative().checkSyntax(tokenList) -> Type.DECLARATION
            isPrint().checkSyntax(tokenList) -> Type.PRINT
            isAssignation().checkSyntax(tokenList) -> Type.ASSIGNATION
            else -> Type.NIL
        }
    }
}