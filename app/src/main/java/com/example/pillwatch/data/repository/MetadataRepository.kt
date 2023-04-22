package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.data.datasource.local.MetadataDao
import com.example.pillwatch.data.model.MetadataEntity

class MetadataRepository (private val metadataDao: MetadataDao){

    fun insert(metadata: MetadataEntity){
        metadataDao.insert(metadata)
    }

    fun getAllMetadata(): LiveData<List<MetadataEntity>> {
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