package com.goolge.kleinikov.stanislav.textilefactory.domain

import java.lang.Thread.sleep
import kotlin.random.Random

sealed class Department<T : Material> {

    var totalProcessed: Double = 0.0

    sealed class ExecutionDepartment<T : Material, R> : Department<T>(),
            Producer<T, R> {

        var totalProduced: Double = 0.0
        val percentUsefulness: Double get() = totalProduced * 100 / totalProcessed

        class ThreadProducer : ExecutionDepartment<Material.RawMaterial, Double>() {
            val producePerTime = 10.0
            override fun produce(material: Material.RawMaterial): Double {
                val produced = material.amount - material.amount * Random.nextDouble(0.1)
                totalProcessed += material.amount
                totalProduced += produced
                sleep(100)
                return produced
            }
        }

        class ColoredThreadProducer : ExecutionDepartment<Material.Threads, Material.ColoredThreads>() {
            val producePerTime = 10.0
            private val colors = Color.values()
            override fun produce(material: Material.Threads): Material.ColoredThreads {
                val produced = material.amount - material.amount * Random.nextDouble(0.1)
                totalProcessed += material.amount
                totalProduced += produced
                sleep(100)
                return Material.ColoredThreads(produced, colors[Random.nextInt(Color.values().size)])
            }
        }
    }

    sealed class QualityDepartment<T : Material, R> : Department<T>(),
            Controller<T, R> {

        var totalDefective: Double = 0.0
        val totalSatisfied: Double get() = totalProcessed - totalDefective
        val percentSatisfied: Double get() = totalSatisfied * 100 / totalProcessed

        class RawQualityDepartment : QualityDepartment<Material.RawMaterial, Double>() {
            val checkPerTime = 10.0
            override fun control(material: Material.RawMaterial): Double {
                val defective = material.amount * Random.nextDouble(0.1)
                totalDefective += defective
                sleep(100)
                return defective
            }
        }

        class ThreadQualityDepartment : QualityDepartment<Material.Threads, Double>() {
            val checkPerTime = 10.0
            override fun control(material: Material.Threads): Double {
                val defective = material.amount * Random.nextDouble(0.1)
                totalDefective += defective
                sleep(100)
                return defective
            }
        }

        class ColoredThreadsQualityDepartment : QualityDepartment<Material.ColoredThreads, Double>() {
            val checkPerTime = 10.0
            override fun control(material: Material.ColoredThreads): Double {
                val defective = material.amount * Random.nextDouble(0.1)
                totalDefective += defective
                sleep(100)
                return defective
            }
        }
    }
}
