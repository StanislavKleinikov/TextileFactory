package com.goolge.kleinikov.stanislav.textilefactory.domain

import io.reactivex.subjects.BehaviorSubject

class Consignment(private val date: String) {

    private var totalMaterial: Double = 0.0
    private var rawMaterial: Material.RawMaterials = Material.RawMaterials(0.0)
    private var defectiveRawMaterial: Material.RawMaterials = Material.RawMaterials(0.0)
    private var checkedRawMaterial: Material.RawMaterials = Material.RawMaterials(0.0)
    private var threads: Material.Threads = Material.Threads(0.0)
    private var defectiveThreads: Material.Threads = Material.Threads(0.0)
    private var checkedThreads: Material.Threads = Material.Threads(0.0)
    private var coloredThreads: MutableMap<Color, Material.ColoredThreads> = mutableMapOf()
    private var checkedColoredThreads: MutableMap<Color, Material.ColoredThreads> = mutableMapOf()

    val rawMaterialManager = BehaviorSubject.create<Consignment>()
    val checkedRawMaterialManager = BehaviorSubject.create<Consignment>()
    val threadsManager = BehaviorSubject.create<Consignment>()
    val checkedThreadsManager = BehaviorSubject.create<Consignment>()
    val coloredThreadManager = BehaviorSubject.create<Consignment>()
    val completedConsignmentManager = BehaviorSubject.create<Consignment>()

    fun rawMaterialRemaining(): Double {
        return rawMaterial.amount
    }

    fun putRawMaterial(size: Double) {
        println("RawMaterial has been added $size")
        totalMaterial += size
        rawMaterial.increase(size)
        rawMaterialManager.onNext(this)
    }

    fun getRawMaterial(size: Double): Double {
        return rawMaterial.reduce(size)
    }

    fun checkedRawMaterialRemaining(): Double {
        return checkedRawMaterial.amount
    }

    fun putCheckedRawMaterial(size: Double) {
        checkedRawMaterial.increase(size)
        checkedRawMaterialManager.onNext(this)
    }

    fun getCheckedRawMaterial(size: Double): Double {
        return checkedRawMaterial.reduce(size)
    }

    fun threadsRemaining(): Double {
        return threads.amount
    }

    fun putThreads(size: Double) {
        threads.increase(size)
        threadsManager.onNext(this)
    }

    fun getThreads(size: Double): Double {
        return threads.reduce(size)
    }

    fun checkedThreadsRemaining(): Double {
        return checkedThreads.amount
    }

    fun putCheckedThreads(size: Double) {
        checkedThreads.increase(size)
        checkedThreadsManager.onNext(this)
    }

    fun getCheckedThreads(size: Double): Double {
        return checkedThreads.reduce(size)
    }

    fun coloredThreadsRemaining(): Double {
        synchronized(coloredThreads) {
            return coloredThreads.values.sumByDouble { coloredThreads -> coloredThreads.amount }
        }
    }

    fun putColoredThreads(threads: Material.ColoredThreads) {
        synchronized(coloredThreads) {
            val coloredThreads = coloredThreads
                .getOrPut(threads.color) { Material.ColoredThreads(0.0, threads.color) }
            coloredThreads.increase(threads.amount)
        }
        coloredThreadManager.onNext(this)
    }

    fun getColoredThreads(size: Double): Material.ColoredThreads? {
        synchronized(coloredThreads) {
            if (coloredThreads.isNotEmpty()) {
                val random = coloredThreads.filterValues { coloredThreads -> coloredThreads.amount > 0 }
                if (random.isNotEmpty()) {
                    val coloredThreads = random.values.random()
                    val reduced = coloredThreads.reduce(size)
                    return Material.ColoredThreads(reduced, coloredThreads.color)
                }
            }
            return null
        }
    }

    fun checkedColoredThreadsRemaining(): Double {
        synchronized(checkedColoredThreads) {
            return checkedColoredThreads.values.sumByDouble { coloredThreads -> coloredThreads.amount }
        }
    }

    fun putCheckedColoredThreads(threads: Material.ColoredThreads) {
        val coloredThreads = checkedColoredThreads
            .getOrPut(threads.color) { Material.ColoredThreads(0.0, threads.color) }
        coloredThreads.increase(threads.amount)
        if (checkedColoredThreadsRemaining() == totalMaterial) {
            completedConsignmentManager.onNext(this)
        }
    }


    override fun toString(): String {
        return """Report(date='$date',
                 |total raw material = $totalMaterial
                 |raw material = $rawMaterial kilo,
                 |defective raw material = $defectiveRawMaterial kilo,
                 |threads were produced = $threads m,
                 |defective threads in the batch = $defectiveThreads m,
                 |coloredThreads were produced = $checkedColoredThreads
                 |""".trimMargin()
    }
}


