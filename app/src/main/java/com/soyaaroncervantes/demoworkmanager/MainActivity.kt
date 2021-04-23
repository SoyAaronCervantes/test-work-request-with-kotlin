package com.soyaaroncervantes.demoworkmanager

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.soyaaroncervantes.demoworkmanager.databinding.ActivityMainBinding
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
  lateinit var binding: ActivityMainBinding
  private lateinit var datePicker: MaterialDatePicker<Long>
  private lateinit var timePicker: MaterialTimePicker
  private var date: Long = 0
  private var totalTime: Long = 0
  private var calendar = Calendar.getInstance()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    datePicker = createDatePicker()
    timePicker = createTimePicker()

    val datePickerButton = binding.displayDatePickerButton!!
    val timePickerButton = binding.displayTimePickerButton!!
    val submitButton = binding.createNotificationButton!!

    val textInputEditMessage = binding.message!!
    val textInputEditTitle = binding.title!!

    val message = textInputEditMessage.text.toString()
    val title = textInputEditTitle.text.toString()

    datePickerButton.setOnClickListener {
      datePicker.show(supportFragmentManager, "date_picker")
      datePicker.addOnPositiveButtonClickListener { date = it }
    }
    timePickerButton.setOnClickListener {
      timePicker.show( supportFragmentManager, "time_picker" )
      timePicker.addOnPositiveButtonClickListener {
        val hour = timePicker.hour
        val minutes = timePicker.minute

        val hourToMinutes = hour.times(60 )
        val totalMinutes = minutes.plus( hourToMinutes )
        val milliseconds = totalMinutes.times( 60 ).times(1000).toLong()

        totalTime = date.plus( milliseconds )
      }

    }

    submitButton.setOnClickListener { setOneTimeRequest( totalTime, message, title ) }

  }

  private fun createDatePicker(): MaterialDatePicker<Long> {
    val calendarConstraintsBuilder = CalendarConstraints.Builder()
    calendarConstraintsBuilder.setValidator( DateValidatorPointForward.now() )

    val calendar = calendarConstraintsBuilder.build()

    val builder = MaterialDatePicker.Builder.datePicker()
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    builder.setTitleText("Selecciona la fecha")
    builder.setSelection( today )
    builder.setCalendarConstraints( calendar )

    return builder.build();
  }

  private fun createTimePicker(): MaterialTimePicker {
    val isSystem24hrs =  DateFormat.is24HourFormat( applicationContext )
    val clockFormat = if (isSystem24hrs) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
    val hour = calendar.get( Calendar.HOUR_OF_DAY )
    val minutes = calendar.get( Calendar.MINUTE )
    val builder = MaterialTimePicker.Builder()

    builder.setTimeFormat( clockFormat )
    builder.setTitleText("Selecciona la hora")
    builder.setHour( hour )
    builder.setMinute( minutes )
    return builder.build()
  }

  private fun setOneTimeRequest(date: Long, message: String, title: String) {
    val workManager = WorkManager.getInstance(applicationContext)
    val now = calendar.time.time
    val isDateValid = date > now

    val dateToNotify = if ( isDateValid ) date else calendar.time.time.plus( 60000 ) // <- 1000 * 60 = 1 min

    val data = Data.Builder()
      .putString("message", message)
      .putString("title", title)
      .putLong("date", dateToNotify)
      .build()

    val uploadRequest =
      OneTimeWorkRequest.Builder(UploadManager::class.java)
        .setInputData(data)
        .setInitialDelay(date, TimeUnit.MILLISECONDS)
        .build()

    workManager.enqueue(uploadRequest)
    workManager.getWorkInfoByIdLiveData(uploadRequest.id).observeForever {
      if (it !== null) {
        Log.d("[Period Work]", "Is it finished? ${it.state.isFinished}")
      }
    }
  }

}
