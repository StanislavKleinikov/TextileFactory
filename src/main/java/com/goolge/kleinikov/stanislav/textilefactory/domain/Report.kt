package com.goolge.kleinikov.stanislav.textilefactory.domain

data class Report(
    val date: String,
    var rawMaterial: Double = 0.0,
    var defectiveRawMaterial: Double = 0.0,
    var threads: Double = 0.0,
    var defectiveThreads: Double = 0.0,
    var coloredThread: Double = 0.0,
    var color: Color? = null,
    var defectiveColoredThread: Double = 0.0,
    var threadsProduced: Double = 0.0,
    var percentThreadProducing: Double = 0.0,
    var coloredThreadProduced: Double = 0.0,
    var percentColoredThreadProducing: Double = 0.0
) {
    override fun toString(): String {
        return """Report(date='$date',
                 |rawMaterial=$rawMaterial kilo,
                 |defectiveRawMaterial=$defectiveRawMaterial kilo,
                 |threads=$threads m,
                 |defectiveThreads=$defectiveThreads m,
                 |coloredThread=$coloredThread m,
                 |color=$color,
                 |defectiveColoredThread=$defectiveColoredThread m,
                 |threadsProduced=$threadsProduced m,
                 |percentThreadProducing=$percentThreadProducing %,
                 |coloredThreadProduced=$coloredThreadProduced m,
                 |percentColoredThreadProducing=$percentColoredThreadProducing %)
                 |""".trimMargin()
    }
}


