package com.example.pillwatch.database.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.database.dao.MetadataDao
import com.example.pillwatch.database.entity.MetadataEntity

class MetadataRepository (private val metadataDao: MetadataDao){

    fun insert(metadata: MetadataEntity){
        metadataDao.insert(metadata)
    }

    fun getAllMetadata(): List<MetadataEntity>{
        return metadataDao.getAllMetadata()
    }

    fun update(metadata: MetadataEntity) {
        metadataDao.update(metadata)
    }

    fun clear() {
        metadataDao.clear()
    }

    fun getMetadata(nameMetadata: String) : MetadataEntity? {
        return metadataDao.getMetadata(nameMetadata)
    }
}