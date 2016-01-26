package com.mcxiaoke.next.kotlin.ext

import android.database.Cursor
import java.util.*

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 16:57
 */

inline fun <T> Cursor?.map(transform: Cursor.() -> T): MutableCollection<T> {
    return mapTo(LinkedList<T>(), transform)
}

inline fun <T, C : MutableCollection<T>> Cursor?.mapTo(result: C, transform: Cursor.() -> T): C {
    return if (this == null) result else {
        if (moveToFirst())
            do {
                result.add(transform())
            } while (moveToNext())
        result
    }
}

inline fun <T> Cursor?.mapAndClose(create: Cursor.() -> T): MutableCollection<T> {
    try {
        return map(create)
    } finally {
        this?.close()
    }
}
