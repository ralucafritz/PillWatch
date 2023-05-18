package com.example.pillwatch.storage

interface Storage {
    fun setString(key: String, value: String)
    fun setLong(key: String, value: Long)
    fun setBoolean(key: String, value: Boolean)
    fun setInt(key: String, value: Int)
    fun getString(key: String): String
    fun getLong(key: String): Long
    fun getBoolean(key: String): Boolean
    fun getInt(key:String): Int
}
