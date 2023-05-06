package com.example.pillwatch.ui.alarms.frequency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pillwatch.utils.AlarmTiming

class AlarmFrequencyViewModel : ViewModel() {

    var selectedOption = MutableLiveData<AlarmTiming>()

}