package com.goolge.kleinikov.stanislav.textilefactory.entities

sealed class ExecutionDepartment<T, R> : Department<T>() {

    abstract fun produce(material: T): R

    object ThreadProducer : ExecutionDepartment<RawMaterials, Threads>() {
        override fun produce(material: RawMaterials): Threads {
            return Threads(material.amount - Math.random() * 50)
        }
    }

    object ColoredThreadProducer : ExecutionDepartment<Threads, ColoredThreads>() {
        override fun produce(material: Threads): ColoredThreads {
            return ColoredThreads(material.amount - Math.random() * 50, Color.BLUE)
        }
    }

}