package com.goolge.kleinikov.stanislav.textilefactory.domain

data class Report(
    val date: String,
    var rawMaterial: Double,
    var defectiveRawMaterial: Double = 0.0,
    var threads: Double = 0.0,
    var defectiveThreads: Double = 0.0,
    var coloredThread: Double = 0.0,
    var color: Color? = null
) {
    override fun toString(): String {
        return """Report(date='$date',
                 |raw material = $rawMaterial kilo,
                 |defective raw material = $defectiveRawMaterial kilo,
                 |threads were produced = $threads m,
                 |defective threads in the batch = $defectiveThreads m,
                 |coloredThreads were produced = $coloredThread m,
                 |color of the threads in the batch = ${color ?: ""},
                 |""".trimMargin()
    }
}


