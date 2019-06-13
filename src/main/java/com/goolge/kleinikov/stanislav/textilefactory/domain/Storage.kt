package com.goolge.kleinikov.stanislav.textilefactory.domain

import io.reactivex.subjects.BehaviorSubject
import java.text.DateFormat
import java.util.*

class Storage {

    val rawMaterialManager = BehaviorSubject.create<Storage>()
    val checkedRawMaterialManager = BehaviorSubject.create<Storage>()
    val threadsManager = BehaviorSubject.create<Storage>()
    val checkedThreadsManager = BehaviorSubject.create<Storage>()
    val coloredThreadManager = BehaviorSubject.create<Storage>()

    private var rawMaterial: Material.RawMaterials = Material.RawMaterials(0.0)
    private var checkedRawMaterial: Material.RawMaterials = Material.RawMaterials(0.0)
    private var threads: Material.Threads = Material.Threads(0.0)
    private var checkedThreads: Material.Threads = Material.Threads(0.0)
    private var coloredThreads = mutableMapOf<Color, Material.ColoredThreads>()
    private var checkedColoredThreads = mutableMapOf<Color, Material.ColoredThreads>()


    fun rawMaterialRemaining(): Double {
        return rawMaterial.amount
    }

    fun putRawMaterial(size: Double) {
        println("RawMaterial has been added $size")
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


    fun putCheckedColoredThreads(threads: Material.ColoredThreads) {
        val coloredThreads = checkedColoredThreads
            .getOrPut(threads.color) { Material.ColoredThreads(0.0, threads.color) }
        coloredThreads.increase(threads.amount)
    }

    fun printStorageState() {
        println(
            """|------------------------------------------------------
               |date ${DateFormat.getDateTimeInstance().format(Date())}
               |raw material ${rawMaterial.amount}
               |checked raw material  ${checkedRawMaterial.amount}
               |threads ${threads.amount}
               |checked threads ${checkedThreads.amount}
               |colored threads $coloredThreads
               |checked colored Threads $checkedColoredThreads
               |------------------------------------------------------""".trimMargin()
        )
    }

    fun stat() {
        println(
            "raw ${rawMaterial.amount} checkedRaw ${checkedRawMaterial.amount} threads ${threads.amount} " +
                    " checkedThreads = ${checkedThreads.amount}  coloredThreads = $coloredThreads" +
                    "  checkedColoredThread = $checkedColoredThreads"
        )
    }
}

