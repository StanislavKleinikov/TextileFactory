package com.goolge.kleinikov.stanislav.textilefactory.domain

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.CopyOnWriteArrayList

class Storage {

    private val consignments: MutableList<Consignment> = CopyOnWriteArrayList()

    private val rawMaterialManager = BehaviorSubject.create<Unit>()
    private val checkedRawMaterialManager = BehaviorSubject.create<Unit>()
    private val threadsManager = BehaviorSubject.create<Unit>()
    private val checkedThreadsManager = BehaviorSubject.create<Unit>()
    private val coloredThreadsManager = BehaviorSubject.create<Unit>()
    private val completedConsignmentManager = BehaviorSubject.create<Consignment>()

    fun processNewConsignment(consignment: Consignment) {
        consignments.add(consignment)
        rawMaterialManager.onNext(Unit)
    }

    fun getRawMaterialManager(): Observable<Unit> {
        return rawMaterialManager
    }

    fun getCheckedRawMaterialManager(): Observable<Unit> {
        return checkedRawMaterialManager
    }

    fun getThreadsManager(): Observable<Unit> {
        return threadsManager
    }

    fun getCheckedThreadsManager(): Observable<Unit> {
        return checkedThreadsManager
    }

    fun getColoredThreadsManager(): Observable<Unit> {
        return coloredThreadsManager
    }

    fun getCompletedConsignmentManager(): Observable<Consignment> {
        return completedConsignmentManager
    }

    fun rawMaterialRemaining(): Double {
        return consignments.sumByDouble { consignment -> consignment.rawMaterial.amount }
    }

    fun getRawMaterial(size: Double): Maybe<MaterialDTO<Material.RawMaterial>> {
        return Observable
                .fromIterable(consignments)
                .filter { consignment -> consignment.rawMaterial.amount > 0 }
                .firstElement()
                .map { consignment ->
                    MaterialDTO(consignment.id, Material.RawMaterial(consignment.rawMaterial.reduce(size)))
                }
    }

    fun putCheckedRawMaterial(id: Long, total: Double, defective: Double): Disposable {
        return Observable.fromIterable(consignments)
                .filter { consignment -> consignment.id == id }
                .firstElement()
                .map { consignment ->
                    consignment.checkedRawMaterial.increase(total - defective)
                    consignment.defectiveRawMaterial.increase(defective)
                    checkedRawMaterialManager.onNext(Unit)
                }.subscribe()
    }

    fun checkedRawMaterialRemaining(): Double {
        return consignments.sumByDouble { consignment -> consignment.checkedRawMaterial.amount }
    }


    fun getCheckedRawMaterial(size: Double): Maybe<MaterialDTO<Material.RawMaterial>> {
        return Observable
                .fromIterable(consignments)
                .filter { consignment -> consignment.checkedRawMaterial.amount > 0 }
                .firstElement()
                .map { consignment ->
                    MaterialDTO(consignment.id, Material.RawMaterial(consignment.checkedRawMaterial.reduce(size)))
                }
    }

    fun threadsRemaining(): Double {
        return consignments.sumByDouble { consignment -> consignment.threads.amount }
    }


    fun putThreads(id: Long, produced: Double): Disposable {
        return Observable.fromIterable(consignments)
                .filter { consignment -> consignment.id == id }
                .firstElement()
                .map { consignment ->
                    consignment.threads.increase(produced)
                    consignment.threadsProduced.increase(produced)
                    threadsManager.onNext(Unit)
                }
                .subscribe()
    }

    fun getThreads(size: Double): Maybe<MaterialDTO<Material.Threads>> {
        return Observable
                .fromIterable(consignments)
                .filter { consignment -> consignment.threads.amount > 0 }
                .firstElement()
                .map { consignment ->
                    MaterialDTO(consignment.id, Material.Threads(consignment.threads.reduce(size)))
                }
    }

    fun checkedThreadsRemaining(): Double {
        return consignments.sumByDouble { consignment -> consignment.checkedThreads.amount }
    }

    fun putCheckedThreads(id: Long, total: Double, defective: Double): Disposable {
        return Observable.fromIterable(consignments)
                .filter { consignment -> consignment.id == id }
                .firstElement()
                .map { consignment ->
                    consignment.checkedThreads.increase(total - defective)
                    consignment.defectiveThreads.increase(defective)
                    checkedThreadsManager.onNext(Unit)
                }
                .subscribe()
    }

    fun getCheckedThreads(size: Double): Maybe<MaterialDTO<Material.Threads>> {
        return Observable
                .fromIterable(consignments)
                .filter { consignment -> consignment.checkedThreads.amount > 0 }
                .firstElement()
                .map { consignment ->
                    MaterialDTO(consignment.id, Material.Threads(consignment.checkedThreads.reduce(size)))
                }
    }


    fun coloredThreadsRemaining(): Double {
        return consignments.sumByDouble { consignment ->
            consignment.coloredThreads.values
                    .sumByDouble { coloredThreads ->
                        coloredThreads.amount
                    }
        }
    }

    fun putColoredThreads(id: Long, produced: Double, color: Color): Disposable {
        return Observable.fromIterable(consignments)
                .filter { consignment -> consignment.id == id }
                .firstElement()
                .map {
                    val coloredThreads = it.coloredThreads
                            .getOrPut(color) { Material.ColoredThreads(0.0, color) }
                    coloredThreads.increase(produced)

                    val totalProduced = it.coloredThreadsProduced
                            .getOrPut(color) { Material.ColoredThreads(0.0, color) }
                    totalProduced.increase(produced)

                    coloredThreadsManager.onNext(Unit)

                }
                .subscribeOn(Schedulers.single())
                .subscribe()
    }

    fun getColoredThreads(size: Double): Maybe<MaterialDTO<Material.ColoredThreads>> {
        return Observable
                .fromIterable(consignments)
                .filter { consignment ->
                    consignment.coloredThreads.values.sumByDouble { coloredThreads -> coloredThreads.amount } > 0
                }
                .firstElement()
                .map { consignment ->
                    val threads = consignment.coloredThreads.filterValues { coloredThreads -> coloredThreads.amount > 0 }.values.random()
                    MaterialDTO(consignment.id, Material.ColoredThreads(threads.reduce(size), threads.color))
                }.subscribeOn(Schedulers.single())
    }

    fun putCheckedColoredThreads(id: Long, total: Double, defective: Double, color: Color): Disposable {
        return Observable.fromIterable(consignments)
                .filter { consignment -> consignment.id == id }
                .firstElement()
                .map {
                    val coloredThreads = it.checkedColoredThreads
                            .getOrPut(color) { Material.ColoredThreads(0.0, color) }
                    coloredThreads.increase(total - defective)

                    val defectiveThreads = it.defectiveColoredThreads
                            .getOrPut(color) { Material.ColoredThreads(0.0, color) }
                    defectiveThreads.increase(defective)


                    if ((it.rawMaterial.amount
                                    + it.checkedRawMaterial.amount
                                    + it.threads.amount
                                    + it.checkedThreads.amount
                                    + it.coloredThreads.values.sumByDouble { threads -> threads.amount }
                                    ) == 0.0) {
                        it.status = Status.COMPLETED
                        completedConsignmentManager.onNext(it)
                    }
                }
                .subscribeOn(Schedulers.single())
                .subscribe()
    }
}