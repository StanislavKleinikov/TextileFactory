package com.goolge.kleinikov.stanislav.textilefactory.domain

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.usecase.Transporter
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Thread.sleep
import java.text.DateFormat
import java.util.*

import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TextileFactory private constructor(
    private val rawQualityDepartments: List<RawQualityDepartment> = listOf(RawQualityDepartment()),
    private val threadQualityDepartment: List<ThreadQualityDepartment> = listOf(ThreadQualityDepartment()),
    private val coloredThreadsQualityDepartment: List<ColoredThreadsQualityDepartment> = listOf(
        ColoredThreadsQualityDepartment()
    ),
    private val threadProducers: List<ThreadProducer> = listOf(ThreadProducer()),
    private val coloredThreadProducer: List<ColoredThreadProducer> = listOf(ColoredThreadProducer())
) {

    companion object {
        fun getDefaultFactory(): TextileFactory {
            return TextileFactory()
        }
    }

    fun start() {
        val interval = Observable.interval(5, TimeUnit.SECONDS).startWith(1)
        interval.map {
            Consignment(date = DateFormat.getDateTimeInstance().format(Date()))
        }.doOnNext { consignment ->
            val transporter = Transporter(consignment, Schedulers.io())
            transporter.startManageRawMaterial(rawQualityDepartments)
            transporter.startManageCheckedRawMaterial(threadProducers)
            transporter.startManageThreads(threadQualityDepartment)
            transporter.startManageCheckedThreads(coloredThreadProducer)
            transporter.startManageColoredThreads(coloredThreadsQualityDepartment)
            consignment.putRawMaterial((Random.nextInt(200) + 100).toDouble())
            consignment.completedConsignmentManager
                .subscribe {
                    println(it.toString())
                }
        }.subscribe()

        sleep(30000)
    }
}
