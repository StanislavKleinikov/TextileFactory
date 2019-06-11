package com.goolge.kleinikov.stanislav.textilefactory.domain

import kotlin.random.Random

sealed class Department<T> {

    var totalProcessed: Double = 0.0

    sealed class ExecutionDepartment<T, R> : Department<T>(),
        Producer<T, R> {

        var totalProduced: Double = 0.0
        val percentUsefulness: Double get() = totalProduced * 100 / totalProcessed

        object ThreadProducer : ExecutionDepartment<Material.RawMaterials, Material.Threads>() {
            override fun produce(material: Material.RawMaterials): Material.Threads {
                val threads = Material.Threads(material.amount - material.amount * Random.nextDouble(0.5))
                totalProcessed += material.amount
                totalProduced += threads.amount
                return threads
            }
        }

        object ColoredThreadProducer : ExecutionDepartment<Material.Threads, Material.ColoredThreads>() {
            override fun produce(material: Material.Threads): Material.ColoredThreads {
                val coloredThreads = Material.ColoredThreads(
                    material.amount - material.amount * Random.nextDouble(0.5),
                    Color.values()[Random.nextInt(3) + 1]
                )
                totalProcessed += material.amount
                totalProduced += coloredThreads.amount
                return coloredThreads
            }
        }
    }

    sealed class QualityDepartment<T> : Department<T>(),
        Controller<T> {

        var totalDefective: Double = 0.0
        val totalSatisfied: Double get() = totalProcessed - totalDefective
        val percentSatisfied: Double get() = totalSatisfied * 100 / totalProcessed

        object RawQualityDepartment : QualityDepartment<Material.RawMaterials>() {
            override fun control(material: Material.RawMaterials) {
                totalProcessed += material.amount
                material.amount = material.amount - material.amount * Random.nextDouble(0.3)
            }
        }

        object ThreadQualityDepartment : QualityDepartment<Material.Threads>() {
            override fun control(material: Material.Threads) {
                totalProcessed += material.amount
                material.amount = material.amount - material.amount * Random.nextDouble(0.3)
            }
        }

        object ColoredThreadsQualityDepartment : QualityDepartment<Material.ColoredThreads>() {
            override fun control(material: Material.ColoredThreads) {
                totalProcessed += material.amount
                material.amount = material.amount - material.amount * Random.nextDouble(0.3)
            }
        }
    }
}
