package com.goolge.kleinikov.stanislav.textilefactory.entities

sealed class QualityDepartment<T> : Department<T>() {
    var totalProcessed: Double = 0.0
    var totalDefective: Double = 0.0
    val totalSatisfied: Double get() = totalProcessed - totalDefective

    abstract fun checkQuality(materials: T)

    object RawQualityDepartment : QualityDepartment<RawMaterials>() {
        override fun checkQuality(materials: RawMaterials) {
            totalProcessed += materials.amount
            materials.amount = materials.amount - (Math.random() * 50)
        }
    }

    object ThreadQualityDepartment : QualityDepartment<Threads>() {
        override fun checkQuality(materials: Threads) {
            totalProcessed += materials.amount
            materials.amount = materials.amount - (Math.random() * 50)
        }
    }

    object ColoredThreadsQualityDepartment : QualityDepartment<ColoredThreads>() {
        override fun checkQuality(materials: ColoredThreads) {
            totalProcessed += materials.amount
            materials.amount = materials.amount - (Math.random() * 50)
        }
    }
}