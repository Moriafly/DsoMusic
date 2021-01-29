package com.dirror.music.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room 数据库
 * @author Moriafly
 */
@Database(version = AppDatabase.DATABASE_VERSION, entities = [MyFavoriteData::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun myFavoriteDao(): MyFavoriteDao

    companion object {

        // 数据库版本
        const val DATABASE_VERSION = 2

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table MyFavoriteData add column pl INTEGER")
                database.execSQL("alter table MyFavoriteData add column flag INTEGER")
                database.execSQL("alter table MyFavoriteData add column maxbr INTEGER")
            }
        }

        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "app_database")
                .addMigrations(MIGRATION_1_2)
                // .fallbackToDestructiveMigration() // 上线移除
                .build().apply {
                    instance = this
                }
        }
    }

}