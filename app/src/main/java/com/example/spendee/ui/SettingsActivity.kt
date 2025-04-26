package com.example.spendee.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cashora.R
import com.example.cashora.databinding.ActivitySettingsBinding

import com.example.spendee.data.PreferencesManager
import com.example.spendee.data.TransactionRepository

import com.example.spendee.notification.NotificationManager

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)
        transactionRepository = TransactionRepository(this)
        notificationManager = NotificationManager(this)

        setupBottomNavigation()
        setupCurrencySpinner()
        setupNotificationSettings()
        setupBackupButtons()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_settings
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_budget -> {
                    startActivity(Intent(this, BudgetActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }

    private fun setupCurrencySpinner() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "INR", "CNY")
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, currencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = adapter

        val currentCurrency = preferencesManager.getCurrency()
        val position = currencies.indexOf(currentCurrency)
        if (position >= 0) {
            binding.spinnerCurrency.setSelection(position)
        }

        binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencies[position]
                if (selectedCurrency != currentCurrency) {
                    preferencesManager.setCurrency(selectedCurrency)
                    Toast.makeText(
                        this@SettingsActivity,
                        getString(R.string.currency_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupNotificationSettings() {
        binding.switchBudgetAlerts.isChecked = preferencesManager.isNotificationEnabled()
        binding.switchDailyReminders.isChecked = preferencesManager.isReminderEnabled()

        binding.switchBudgetAlerts.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setNotificationEnabled(isChecked)
        }

        binding.switchDailyReminders.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setReminderEnabled(isChecked)
            if (isChecked) {
                notificationManager.scheduleDailyReminder()
            } else {
                notificationManager.cancelDailyReminder()
            }
        }
    }

    private fun setupBackupButtons() {
        binding.btnBackupData.setOnClickListener {
            if (transactionRepository.backupToInternalStorage(this)) {
                Toast.makeText(
                    this,
                    getString(R.string.backup_success),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.backup_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnRestoreData.setOnClickListener {
            if (transactionRepository.restoreFromInternalStorage(this)) {
                Toast.makeText(
                    this,
                    getString(R.string.restore_success),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.restore_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}