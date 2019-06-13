package com.goolge.kleinikov.stanislav.textilefactory.domain

import java.lang.Thread.sleep
import kotlin.random.Random

sealed class Department<T> {

    var totalProcessed: Double = 0.0

    sealed class ExecutionDepartment<T : Material, R> : Department<T>(),
        Producer<T, R> {

        var totalProduced: Double = 0.0
        val percentUsefulness: Double get() = totalProduced * 100 / totalProcessed

        class ThreadProducer : ExecutionDepartment<Material.RawMaterials, Double>() {
            val producePerTime = 10.0
            @Synchronized
            override fun produce(material: Material.RawMaterials): Double {
                val produced = material.amount //amountToCheck * Random.nextDouble(0.3)
                totalProcessed += material.amount
                totalProduced += produced
                sleep(100)
                return produced
            }
        }

        class ColoredThreadProducer : ExecutionDepartment<Material.Threads, Material.ColoredThreads>() {
            val producePerTime = 10.0
            private val colors = Color.values()
            @Synchronized
            override fun produce(material: Material.Threads): Material.ColoredThreads {
                val produced = material.amount //amountToCheck * Random.nextDouble(0.3)
                totalProcessed += material.amount
                totalProduced += produced
                sleep(100)
                return Material.ColoredThreads(produced, colors[Random.nextInt(Color.values().size)])
            }
        }
    }

    sealed class QualityDepartment<T, R> : Department<T>(),
        Controller<T, R> {

        var totalDefective: Double = 0.0
        val totalSatisfied: Double get() = totalProcessed - totalDefective
        val percentSatisfied: Double get() = totalSatisfied * 100 / totalProcessed

        class RawQualityDepartment : QualityDepartment<Material.RawMaterials, Double>() {
            val checkPerTime = 10.0
            @Synchronized
            override fun control(material: Material.RawMaterials): Double {
                val defective = 0 //amountToCheck * Random.nextDouble(0.3)
                totalDefective += defective
                sleep(100)
                return material.amount - defective
            }
        }

        class ThreadQualityDepartment : QualityDepartment<Material.Threads, Double>() {
            val checkPerTime = 10.0
            @Synchronized
            override fun control(material: Material.Threads): Double {
                val defective = 0 //amountToCheck * Random.nextDouble(0.3)
                totalDefective += defective
                sleep(100)
                return material.amount - defective
            }
        }

        class ColoredThreadsQualityDepartment : QualityDepartment<Material.ColoredThreads, Material.ColoredThreads>() {
            val checkPerTime = 10.0
            @Synchronized
            override fun control(material: Material.ColoredThreads): Material.ColoredThreads {
                val defective = 0 //amountToCheck * Random.nextDouble(0.3)
                totalDefective += defective
                sleep(100)
                material.amount -= defective
                return material
            }
        }
    }
}
