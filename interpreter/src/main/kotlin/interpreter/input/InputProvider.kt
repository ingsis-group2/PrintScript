package interpreter.input

class InputProvider(val inputs: List<String>) {
    private var index = 0

    fun getNextInput(): String {
        if (index >= inputs.size) {
            throw IllegalStateException("No more inputs")
        }
        return inputs[index++]
    }
}
