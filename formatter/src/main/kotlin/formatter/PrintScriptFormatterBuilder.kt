package formatter

import formatter.formatOperations.operators.AssignmentNodeFormat
import formatter.formatOperations.operators.BinaryOperationNodeFormat
import formatter.formatOperations.operators.CodeBlockFormat
import formatter.formatOperations.operators.FunctionNodeFormat
import formatter.formatOperations.operators.IfNodeFormat
import formatter.formatOperations.operators.LiteralNodeFormat
import formatter.formatOperations.operators.NilNodeFormat
import formatter.formatOperations.operators.PrintNodeFormat
import formatter.formatOperations.operators.VariableOperationNodeFormat
import token.TokenType

class PrintScriptFormatterBuilder : FormatterBuilder {
    override fun build(
        version: String,
        rulesPath: String,
    ): Formatter {
        return when (version) {
            "1.0" -> formatter10(rulesPath)
            "1.1" -> formatter11(rulesPath)
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }

    private fun formatter10(rulesPath: String): Formatter {
        val operatorList =
            listOf(
                NilNodeFormat(),
                LiteralNodeFormat(),
                BinaryOperationNodeFormat(),
                PrintNodeFormat(),
                VariableOperationNodeFormat(getAllowedDeclarationTokens("1.0"), getAllowedValueTypes("1.0")),
                AssignmentNodeFormat(),
            )
        return PrintScriptFormatter(rulesPath, operatorList)
    }

    private fun formatter11(rulesPath: String): Formatter {
        val operatorList =
            listOf(
                NilNodeFormat(),
                LiteralNodeFormat(),
                BinaryOperationNodeFormat(),
                PrintNodeFormat(),
                VariableOperationNodeFormat(getAllowedDeclarationTokens("1.1"), getAllowedValueTypes("1.1")),
                AssignmentNodeFormat(),
                IfNodeFormat(),
                FunctionNodeFormat(),
                CodeBlockFormat(),
            )
        return PrintScriptFormatter(rulesPath, operatorList)
    }

    private fun getAllowedDeclarationTokens(version: String): Map<TokenType, String> {
        return when (version) {
            "1.0" -> mapOf(TokenType.LET to "let")
            "1.1" -> mapOf(TokenType.LET to "let", TokenType.CONST to "const")
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }

    private fun getAllowedValueTypes(version: String): Map<TokenType, String> {
        return when (version) {
            "1.0" -> mapOf(TokenType.NUMBERTYPE to "number", TokenType.STRINGTYPE to "string")
            "1.1" -> mapOf(TokenType.NUMBERTYPE to "number", TokenType.STRINGTYPE to "string", TokenType.BOOLEANTYPE to "boolean")
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}
