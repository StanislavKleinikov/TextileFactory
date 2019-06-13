package com.goolge.kleinikov.stanislav.textilefactory.domain

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.usecase.Transporter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Thread.sleep

import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TextileFactory private constructor(
    private val storage: Storage = Storage(),
    private val transporter: Transporter = Transporter(storage, Schedulers.io()),
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
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(transporter.startManageRawMaterial(rawQualityDepartments))
        compositeDisposable.add(transporter.startManageCheckedRawMaterial(threadProducers))
        compositeDisposable.add(transporter.startManageThreads(threadQualityDepartment))
        compositeDisposable.add(transporter.startManageCheckedThreads(coloredThreadProducer))
        compositeDisposable.add(transporter.startManageColoredThreads(coloredThreadsQualityDepartment))

        val interval = Observable.interval(5, TimeUnit.SECONDS).startWith(1)
        interval.map {
            storage.putRawMaterial((Random.nextInt(200) + 100).toDouble())
        }.mergeWith(Observable.interval(100, TimeUnit.MILLISECONDS)
            .map {
                storage.stat()
            }).subscribe()

        sleep(30000)
    }
}
