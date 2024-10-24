package com.ashfaque.demopoi.roomdb

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ashfaque.demopoi.Constants.TABLE_NAME


@Entity(tableName = TABLE_NAME, indices = [Index(value = ["title"], unique = true)])
data class EntityDataClass(

    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val title: String,
    val ownerName: String,
    val locationName: String,
    val tag: String,
    val createdDate:String
)