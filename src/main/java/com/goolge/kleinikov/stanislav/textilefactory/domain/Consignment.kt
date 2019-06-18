package com.goolge.kleinikov.stanislav.textilefactory.domain

data class Consignment(val id: Long, val date: String, val totalMaterial: Double) {
    var rawMaterial: Material.RawMaterial = Material.RawMaterial(totalMaterial)
    var defectiveRawMaterial: Material.RawMaterial = Material.RawMaterial(0.0)
    var checkedRawMaterial: Material.RawMaterial = Material.RawMaterial(0.0)

    var threads: Material.Threads = Material.Threads(0.0)
    var defectiveThreads: Material.Threads = Material.Threads(0.0)
    var checkedThreads: Material.Threads = Material.Threads(0.0)
    var threadsProduced: Material.Threads = Material.Threads(0.0)

    var coloredThreads: MutableMap<Color, Material.ColoredThreads> = mutableMapOf()
    var defectiveColoredThreads: MutableMap<Color, Material.ColoredThreads> = mutableMapOf()
    var checkedColoredThreads: MutableMap<Color, Material.ColoredThreads> = mutableMapOf()
    var coloredThreadsProduced: MutableMap<Color, Material.ColoredThreads> = mutableMapOf()

    var status: Status = Status.IN_PROGRESS

    override fun toString(): String {
        return """|------------------- Consignment $id-------------------
                  |date='$date',
                  |total raw material = $totalMaterial
                  |defective raw material = ${defectiveRawMaterial.amount} kilo,
                  |threads were produced = ${threadsProduced.amount} m,
                  |defective threads = ${defectiveThreads.amount} m,
                  |colored threads were produced = $coloredThreadsProduced
                  |defective colored threads = $defectiveColoredThreads
                  |status = $status
                  |------------------------------------------------------
                  |""".trimMargin()
    }
}


