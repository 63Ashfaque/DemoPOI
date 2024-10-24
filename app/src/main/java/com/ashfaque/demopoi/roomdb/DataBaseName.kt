package com.ashfaque.demopoi.roomdb

import android.content.Context
import androidx.room.*
import com.ashfaque.demopoi.Constants.DATABASE_NAME

@Database(entities = [EntityDataClass::class], version = 1)
@TypeConverters(Converters::class)
abstract class DataBaseName : RoomDatabase() {

    abstract fun interfaceDao(): InterfaceDAO

    companion object {

        @Volatile
        private var INSTANCE: DataBaseName? = null
        fun getDataBase(context: Context): DataBaseName {
            if (INSTANCE == null) {
                synchronized(this)
                {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DataBaseName::class.java,
                        DATABASE_NAME
                    ).build()
                }

            }
            return INSTANCE!!
        }
    }
}