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

    private var postponedTimes: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            "START_ALARM" -> {
                postponedTimes = intent.getIntExtra("POSTPONED_TIMES", 0) ?: 0
                startAlarm()
            }
            "STOP_ALARM" -> stopAlarm()
        }

        return START_NOT_STICKY
    }

    private fun startAlarm() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, alarmUri)

        mediaPlayer?.start()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator // initialize vibrator here
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
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = null

        if (vibrator?.hasVibrator() == true) {
            stopVibration()
        }

        stopSelf()
    }

    private fun startVibration() {
        val vibrationPattern = when {
            postponedTimes > 2 -> longArrayOf(0, 800, 200)
            postponedTimes > 1 -> longArrayOf(0, 1000, 500)
            else -> longArrayOf(0, 1500, 1000)
        }

        vibrator?.vibrate(
            VibrationEffect.createWaveform(
                vibrationPattern,
                intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0),
                0
            )
        )
    }

    private fun stopVibration() {
        vibrator?.cancel()
    }
}