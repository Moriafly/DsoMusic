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
@Database(version = AppDatabase.DATABASE_VERSION, entities = [MyFavoriteData::class, PlayQueueData::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun myFavoriteDao(): MyFavoriteDao

    abstract fun playQueueDao(): PlayQueueDao

    companion object {

        // 数据库版本
        const val DATABASE_VERSION = 3

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table MyFavoriteData add column pl INTEGER")
                database.execSQL("alter table MyFavoriteData add column flag INTEGER")
                database.execSQL("alter table MyFavoriteData add column maxbr INTEGER")
            }
        }

        /**
         * 数据库 2 -> 3 的升级
         * 应用版本 677
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.apply {
                    execSQL("create table PlayQueueData (databaseId integer primary key autoincrement not null)")
                    execSQL("alter table PlayQueueData add column flag INTEGER")
                    execSQL("alter table PlayQueueData add column size INTEGER")
                    execSQL("alter table PlayQueueData add column artists TEXT")
                    execSQL("alter table PlayQueueData add column imageUrl TEXT")
                    execSQL("alter table PlayQueueData add column fee INTEGER")
                    execSQL("alter table PlayQueueData add column name TEXT")
                    execSQL("alter table PlayQueueData add column maxbr INTEGER")
                    execSQL("alter table PlayQueueData add column source INTEGER")
                    execSQL("alter table PlayQueueData add column id TEXT")
                    execSQL("alter table PlayQueueData add column pl INTEGER")
                    execSQL("alter table PlayQueueData add column url TEXT")

                    execSQL("CREATE TABLE MyFavoriteData_temp (databaseId integer primary key autoincrement not null, flag INTEGER, size INTEGER, artists TEXT, imageUrl TEXT, fee INTEGER, name TEXT, maxbr INTEGER, source INTEGER, id TEXT, pl INTEGER, url TEXT)")
                    execSQL(" INSERT INTO MyFavoriteData_temp (flag, size, artists, imageUrl, fee, name, maxbr, source, id, pl, url) SELECT flag, size, artists, imageUrl, fee, name, maxbr, source, id, pl, url FROM MyFavoriteData ")
                    execSQL(" DROP TABLE MyFavoriteData")
                    execSQL(" ALTER  TABLE MyFavoriteData_temp  RENAME to MyFavoriteData")
                }
            }
        }

        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "app_database"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                // .fallbackToDestructiveMigration() // 上线移除
                .build().apply {
                    instance = this
                }
        }
    }

}