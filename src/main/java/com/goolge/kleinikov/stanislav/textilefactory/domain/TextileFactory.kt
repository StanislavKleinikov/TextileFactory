package com.goolge.kleinikov.stanislav.textilefactory.domain

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import io.reactivex.Observable
import java.lang.Thread.sleep
import java.text.DateFormat
import java.util.*

import java.util.concurrent.TimeUnit

class TextileFactory private constructor(
    private val rawQualityDepartments: List<RawQualityDepartment> = listOf(RawQualityDepartment()),
    private val threadQualityDepartment: List<ThreadQualityDepartment> = listOf(ThreadQualityDepartment()),
    private val coloredThreadsQualityDepartment: List<ColoredThreadsQualityDepartment> = listOf(
        ColoredThreadsQualityDepartment()
    ),
    private val threadProducers: List<ThreadProducer> = listOf(ThreadProducer()),
    private val coloredThreadProducer: List<ColoredThreadProducer> = listOf(ColoredThreadProducer()),
    private val storage: Storage = Storage(),
    private val transport: Transport = Transport(
        storage,
        rawQualityDepartments,
        threadQualityDepartment,
        coloredThreadsQualityDepartment,
        threadProducers, coloredThreadProducer
    )
) {

    companion object {
        fun getDefaultFactory(): TextileFactory {
            return TextileFactory()
        }
    }

    fun start() {
        storage.getCompletedConsignmentManager().subscribe{println(it)}
        transport.startManageStorage()

        val interval = Observable.interval(5, TimeUnit.SECONDS).startWith(1)
        interval.map {
            storage.processNewConsignment(Consignment(id = it, date = DateFormat.getDateTimeInstance().format(Date())))
        }.subscribe()

        sleep(30000)
    }
}
