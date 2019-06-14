package com.goolge.kleinikov.stanislav.textilefactory.domain

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class Storage {

    private val consignments: MutableList<Consignment> = mutableListOf()

    private val rawMaterialManager = BehaviorSubject.create<Storage>()
    private val checkedRawMaterialManager = BehaviorSubject.create<Storage>()
    private val threadsManager = BehaviorSubject.create<Storage>()
    private val checkedThreadsManager = BehaviorSubject.create<Storage>()
    private val coloredThreadManager = BehaviorSubject.create<Storage>()
    private val completedConsignmentManager = BehaviorSubject.create<Storage>()

    fun processNewConsignment(consignment: Consignment) {
        consignments.add(consignment)
        rawMaterialManager.onNext(this)
    }

    fun getRawMaterialManager(): Observable<Storage> {
        return rawMaterialManager
    }

    fun getCheckedRawMaterialManager(): Observable<Storage> {
        return checkedRawMaterialManager
    }

    fun getThreadManager(): Observable<Storage> {
        return threadsManager
    }

    fun getCheckedThreadManager(): Observable<Storage> {
        return checkedThreadsManager
    }

    fun getColoredThreadManager(): Observable<Storage> {
        return coloredThreadManager
    }

    fun getCompletedConsignmentManager(): Observable<Storage> {
        return completedConsignmentManager
    }

    fun rawMaterialRemaining(): Double {
        return consignments.sumByDouble { consignment -> consignment.rawMaterials }
    }

    fun getRawMaterial(size: Double): Maybe<MaterialDTO> {
        return Observable
            .fromIterable(consignments)
            .filter { consignment -> consignment.rawMaterials > 0 }
            .firstElement()
            .map { consignment ->
                var returnValue = size
                val amount = consignment.rawMaterials
                if (amount < size) {
                    returnValue = amount
                }
                consignment.rawMaterials -= returnValue
                MaterialDTO(consignment.id, returnValue)
            }
    }

    fun putCheckedRawMaterial(id: Long, size: Double): Disposable {
        return Observable.fromIterable(consignments)
            .filter { consignment -> consignment.id == id }
            .firstElement()
            .map { consignment ->
                consignment.rawMaterials += size
                checkedRawMaterialManager.onNext(this)
            }.subscribe()
    }

    /* fun getCheckedRawMaterial(size: Double): Double {
         return checkedRawMaterial.reduce(size)
     }*/

    /* fun threadsRemaining(): Double {
         return threads.amount
     }

     fun putThreads(size: Double) {
         threads.increase(size)
         threadsManager.onNext(this)
     }

     fun getThreads(size: Double): Double {
         return threads.reduce(size)
     }

     fun checkedThreadsRemaining(): Double {
         return checkedThreads.amount
     }

     fun putCheckedThreads(size: Double) {
         checkedThreads.increase(size)
         checkedThreadsManager.onNext(this)
     }

     fun getCheckedThreads(size: Double): Double {
         return checkedThreads.reduce(size)
     }

     fun coloredThreadsRemaining(): Double {
         synchronized(coloredThreads) {
             return coloredThreads.values.sumByDouble { coloredThreads -> coloredThreads.amount }
         }
     }

     fun putColoredThreads(threads: Material.ColoredThreads) {
         synchronized(coloredThreads) {
             val coloredThreads = coloredThreads
                 .getOrPut(threads.color) { Material.ColoredThreads(0.0, threads.color) }
             coloredThreads.increase(threads.amount)
         }
         coloredThreadManager.onNext(this)
     }

     fun getColoredThreads(size: Double): Material.ColoredThreads? {
         synchronized(coloredThreads) {
             if (coloredThreads.isNotEmpty()) {
                 val random = coloredThreads.filterValues { coloredThreads -> coloredThreads.amount > 0 }
                 if (random.isNotEmpty()) {
                     val coloredThreads = random.values.random()
                     val reduced = coloredThreads.reduce(size)
                     return Material.ColoredThreads(reduced, coloredThreads.color)
                 }
             }
             return null
         }
     }

     fun checkedColoredThreadsRemaining(): Double {
         synchronized(checkedColoredThreads) {
             return checkedColoredThreads.values.sumByDouble { coloredThreads -> coloredThreads.amount }
         }
     }

     fun putCheckedColoredThreads(threads: Material.ColoredThreads) {
         val coloredThreads = checkedColoredThreads
             .getOrPut(threads.color) { Material.ColoredThreads(0.0, threads.color) }
         coloredThreads.increase(threads.amount)
         if (checkedColoredThreadsRemaining() == totalMaterial) {
             status = Status.COMPLETED
             completedConsignmentManager.onNext(this)
         }
     }*/
}