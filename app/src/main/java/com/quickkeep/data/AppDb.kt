package com.quickkeep.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [KeepItem::class, Collection::class, KeepItemCollection::class],
    version = 2,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun keepDao(): KeepDao

    companion object {
        @Volatile private var INSTANCE: AppDb? = null
        fun get(context: Context): AppDb = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context, AppDb::class.java, "quickkeep.db")
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
        }
    }
}
