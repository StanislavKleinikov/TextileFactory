package com.goolge.kleinikov.stanislav.textilefactory.domain

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Transport(
        private val storage: Storage,
        private val rawQualityDepartments: List<RawQualityDepartment>,
        private val threadQualityDepartment: List<ThreadQualityDepartment>,
        private val coloredThreadsQualityDepartment: List<ColoredThreadsQualityDepartment>,
        private val threadProducers: List<Department.ExecutionDepartment.ThreadProducer>,
        private val coloredThreadProducer: List<Department.ExecutionDepartment.ColoredThreadProducer>
) {

    fun startManageStorage(): CompositeDisposable {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(startManageRawMaterial())
        compositeDisposable.add(startManageCheckedRawMaterial())
        compositeDisposable.add(startManageThreads())
        compositeDisposable.add(startManageCheckedThreads())
        compositeDisposable.add(startManageColoredThreads())
        return compositeDisposable
    }

    private fun startManageRawMaterial(): Disposable {
        return storage.getRawMaterialManager()
                .toFlowable(BackpressureStrategy.LATEST)
                .concatMap {
                    createRawMaterialObservable()
                }
                .subscribe()
    }

    private fun createRawMaterialObservable(): Flowable<Unit> {
        return Flowable.fromIterable(rawQualityDepartments)
                .flatMap { department ->
                    storage.getRawMaterial(department.checkPerTime)
                            .subscribeOn(Schedulers.io())
                            .repeatUntil { storage.rawMaterialRemaining() == 0.0 }
                            .map { dto ->
                                val defective = department.control(dto.material)
                                storage.putCheckedRawMaterial(dto.id, dto.material.amount, defective)
                            }
                }
                .map { Unit }
    }

    private fun startManageCheckedRawMaterial(): Disposable {
        return storage.getCheckedRawMaterialManager()
                .toFlowable(BackpressureStrategy.LATEST)
                .concatMap {
                    createCheckedRawMaterialObservable()
                }
                .subscribe()
    }

    private fun createCheckedRawMaterialObservable(): Flowable<Unit> {
        return Flowable.fromIterable(threadProducers)
                .flatMap { department ->
                    storage.getCheckedRawMaterial(department.producePerTime)
                            .subscribeOn(Schedulers.io())
                            .repeatUntil { storage.checkedRawMaterialRemaining() == 0.0 }
                            .map { dto ->
                                val produced = department.produce(dto.material)
                                storage.putThreads(dto.id, produced)
                            }
                }
                .map { Unit }
    }

    private fun startManageThreads(): Disposable {
        return storage.getThreadsManager()
                .toFlowable(BackpressureStrategy.LATEST)
                .concatMap {
                    createThreadsObservable()
                }
                .subscribe()
    }

    private fun createThreadsObservable(): Flowable<Unit> {
        return Flowable.fromIterable(threadQualityDepartment)
                .flatMap { department ->
                    storage.getThreads(department.checkPerTime)
                            .subscribeOn(Schedulers.io())
                            .repeatUntil { storage.threadsRemaining() == 0.0 }
                            .map { dto ->
                                val defective = department.control(dto.material)
                                storage.putCheckedThreads(dto.id, dto.material.amount, defective)
                            }
                }
                .map { Unit }
    }


    private fun startManageCheckedThreads(): Disposable {
        return storage.getCheckedThreadsManager()
                .toFlowable(BackpressureStrategy.LATEST)
                .concatMap {
                    createCheckedThreadsObservable()
                }
                .subscribe()
    }

    private fun createCheckedThreadsObservable(): Flowable<Unit> {
        return Flowable.fromIterable(coloredThreadProducer)
                .flatMap { department ->
                    storage.getCheckedThreads(department.producePerTime)
                            .subscribeOn(Schedulers.io())
                            .repeatUntil { storage.checkedThreadsRemaining() == 0.0 }
                            .map { dto ->
                                val produced = department.produce(dto.material)
                                storage.putColoredThreads(dto.id, produced.amount, produced.color)
                            }
                }
                .map { Unit }
    }

    private fun startManageColoredThreads(): Disposable {
        return storage.getColoredThreadsManager()
                .toFlowable(BackpressureStrategy.LATEST)
                .concatMap {
                    createColoredThreadsObservable()
                }
                .subscribe()
    }

    private fun createColoredThreadsObservable(): Flowable<Unit> {
        return Flowable.fromIterable(coloredThreadsQualityDepartment)
                .flatMap { department ->
                    storage.getColoredThreads(department.checkPerTime)
                            .subscribeOn(Schedulers.io())
                            .repeatUntil { storage.coloredThreadsRemaining() == 0.0 }
                            .map { dto ->
                                val defective = department.control(dto.material)
                                storage.putCheckedColoredThreads(dto.id, dto.material.amount, defective, dto.material.color)
                            }
                }
                .map { Unit }
    }
}