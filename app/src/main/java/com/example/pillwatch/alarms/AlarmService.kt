package com.example.pillwatch.alarms

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            "START_ALARM" -> startAlarm()
            "STOP_ALARM" -> stopAlarm()
        }

        return START_NOT_STICKY
    }

    private fun startAlarm() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, alarmUri)


        mediaPlayer?.start()
        startVibration()



        Handler(Looper.getMainLooper()).postDelayed({
            if(mediaPlayer!=null) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                stopVibration()
            }
        }, 30000) // 30 seconds
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopVibration()
        stopSelf()
    }

    private fun startVibration() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator?.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 1000, 500),
                    intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0),
                    0
                )
            )
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }
}