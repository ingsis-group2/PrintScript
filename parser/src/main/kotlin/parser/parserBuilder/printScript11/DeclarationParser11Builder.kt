@file:Suppress("ktlint:standard:no-wildcard-imports")

package parser.parserBuilder.printScript11

import parser.analysis.semantic.analyser.DeclarationSemanticAnalyzer
import parser.analysis.semantic.analyser.SemanticAnalyzer
import parser.analysis.semantic.rule.BooleanSemantic
import parser.analysis.semantic.rule.FunctionSemantic
import parser.analysis.semantic.rule.NumberSemantic
import parser.analysis.semantic.rule.StringSemantic
import parser.analysis.syntax.analyzer.SyntaxAnalyzer
import parser.analysis.syntax.analyzer.SyntaxAnalyzerImpl
import parser.analysis.syntax.rule.*
import parser.analysis.syntax.rule.ifSyntax.IsIfElseSyntax
import parser.nodeBuilder.*
import parser.parser.DeclarationParser
import parser.parser.Parser
import parser.parserBuilder.ParserBuilder
import token.TokenType

class DeclarationParser11Builder : ParserBuilder {
    override fun build(): Parser {
        return DeclarationParser(
            TokenType.SEMICOLON,
            getTypeMap(),
            getDeclarationList(),
            getSyntaxAnalyzer(),
            getSemanticAnalyzer(),
            getNodeBuilders(),
        )
    }

    private fun getDeclarationList(): List<TokenType> {
        return listOf(
            TokenType.LET,
            TokenType.CONST,
        )
    }

    private fun getTypeMap(): Map<TokenType, TokenType> {
        return mapOf(
            Pair(TokenType.NUMBERLITERAL, TokenType.NUMBERTYPE),
            Pair(TokenType.STRINGLITERAL, TokenType.STRINGTYPE),
            Pair(TokenType.BOOLEANLITERAL, TokenType.BOOLEANTYPE),
        )
    }

    private fun getNodeBuilders(): Map<TokenType, NodeBuilder> {
        return mapOf(
            Pair(TokenType.STRINGTYPE, StringNodeBuilder()),
            Pair(TokenType.NUMBERTYPE, ArithmeticNodeBuilder()),
            Pair(TokenType.BOOLEANTYPE, BooleanNodeBuilder()),
            Pair(TokenType.READENV, FunctionNodeBuilder()),
            Pair(TokenType.READINPUT, FunctionNodeBuilder()),
        )
    }

    private fun getSemanticAnalyzer(): SemanticAnalyzer {
        return DeclarationSemanticAnalyzer(
            mapOf(
                Pair(TokenType.NUMBERTYPE, listOf(NumberSemantic())),
                Pair(TokenType.STRINGTYPE, listOf(StringSemantic())),
                Pair(TokenType.BOOLEANTYPE, listOf(BooleanSemantic())),
                Pair(TokenType.READENV, listOf(FunctionSemantic())),
                Pair(TokenType.READINPUT, listOf(FunctionSemantic())),
            ),
            getValidDataTypes(),
        )
    }

    private fun getValidDataTypes(): Map<TokenType, List<TokenType>> {
        return mapOf(
            Pair(TokenType.NUMBERTYPE, listOf(TokenType.NUMBERTYPE, TokenType.READINPUT, TokenType.READENV)),
            Pair(TokenType.STRINGTYPE, listOf(TokenType.STRINGTYPE, TokenType.READENV, TokenType.READINPUT)),
            Pair(TokenType.BOOLEANTYPE, listOf(TokenType.BOOLEANTYPE, TokenType.READENV, TokenType.READINPUT)),
        )
    }

    private fun getSyntaxAnalyzer(): SyntaxAnalyzer {
        return SyntaxAnalyzerImpl(
            mapOf(
                Pair(TokenType.NUMBERTYPE, IsArithmeticSyntax()),
                Pair(TokenType.STRINGTYPE, IsStringSyntax()),
                Pair(TokenType.BOOLEANTYPE, IsBooleanSyntax()),
                Pair(TokenType.READINPUT, IsReadInputFunctionSyntax()),
                Pair(TokenType.READENV, IsReadEnvFunctionSyntax()),
                Pair(TokenType.IF, IsIfElseSyntax()),
            ),
        )
    }
}
