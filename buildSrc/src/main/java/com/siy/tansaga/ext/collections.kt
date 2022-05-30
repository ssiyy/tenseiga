package com.siy.tansaga.ext

import com.didiglobal.booster.kotlinx.get
import java.util.stream.Stream
import java.util.stream.StreamSupport

fun <T> Iterable<T>.isEmpty() = !iterator().hasNext()

inline fun <K, V> Map<K, V>.ifNotEmpty(action: (Map<K, V>) -> Unit): Map<K, V> {
    if (isNotEmpty()) {
        action(this)
    }
    return this
}

inline fun <T> Collection<T>.ifNotEmpty(action: (Collection<T>) -> Unit): Collection<T> {
    if (isNotEmpty()) {
        action(this)
    }
    return this
}

fun <T> Iterator<T>.asIterable(): Iterable<T> = Iterable { this }

fun <T> Iterator<T>.stream(): Stream<T> = asIterable().stream()

fun <T> Iterator<T>.parallelStream(): Stream<T> = asIterable().parallelStream()

fun <T> Iterable<T>.stream(): Stream<T> = StreamSupport.stream(spliterator(), false)

fun <T> Iterable<T>.parallelStream(): Stream<T> = StreamSupport.stream(spliterator(), true)


fun <T> Collection<T>.join(j:String):String {
  return  if(this.isNullOrEmpty()){
        ""
    }else{
        this.foldIndexed(this.get(0).toString()){ index, acc, item ->
                if (index == 0){
                    acc
                }else{
                    "${acc}$j${item}"
                }
        }
    }

}
