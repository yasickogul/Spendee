package com.example.spendee.ui


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.cashora.R
import com.example.cashora.databinding.ActivityBudgetBinding

import com.example.spendee.data.PreferencesManager
import com.example.spendee.data.TransactionRepository

import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class BudgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgetBinding
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var preferencesManager: PreferencesManager

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactionRepository = TransactionRepository(this)
        preferencesManager = PreferencesManager(this)

        setupBottomNavigation()
        setupMonthSelector()
        setupBudgetDisplay()
        setupBudgetChart()

        binding.btnSetBudget.setOnClickListener {
            val intent = Intent(this, SetBudgetActivity::class.java).apply {
                putExtra("month", calendar.get(Calendar.MONTH))
                putExtra("year", calendar.get(Calendar.YEAR))
            }
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_budget
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
                R.id.nav_budget -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMonthSelector() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.tvCurrentMonth.text = dateFormat.format(calendar.time)

        binding.btnPreviousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateBudgetDisplay()
            binding.tvCurrentMonth.text = dateFormat.format(calendar.time)
        }

        binding.btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateBudgetDisplay()
            binding.tvCurrentMonth.text = dateFormat.format(calendar.time)
        }
    }

    private fun setupBudgetDisplay() {
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val budget = preferencesManager.getBudget()
        val currency = preferencesManager.getCurrency()

        if (budget.month == month && budget.year == year && budget.amount > 0) {
            binding.tvNoBudget.visibility = View.GONE
            binding.cardBudgetInfo.visibility = View.VISIBLE

            val totalExpenses = transactionRepository.getTotalExpensesForMonth(month, year)
            val remaining = budget.amount - totalExpenses
            val percentage = (totalExpenses / budget.amount) * 100

            binding.tvBudgetAmount.text = String.format("%s %.2f", currency, budget.amount)
            binding.tvExpensesAmount.text = String.format("%s %.2f", currency, totalExpenses)
            binding.tvRemainingAmount.text = String.format("%s %.2f", currency, remaining)

            binding.progressBudget.progress = percentage.toInt().coerceAtMost(100)
            binding.tvBudgetPercentage.text = String.format("%.1f%%", percentage)

            if (percentage >= 100) {
                binding.tvBudgetStatus.text = getString(R.string.budget_exceeded)
                binding.tvBudgetStatus.setTextColor(Color.RED)
                binding.tvRemainingAmount.setTextColor(Color.RED)
            } else if (percentage >= 80) {
                binding.tvBudgetStatus.text = getString(R.string.budget_warning)
                binding.tvBudgetStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
                binding.tvRemainingAmount.setTextColor(Color.parseColor("#FFA500"))
            } else {
                binding.tvBudgetStatus.text = getString(R.string.budget_good)
                binding.tvBudgetStatus.setTextColor(Color.GREEN)
                binding.tvRemainingAmount.setTextColor(Color.GREEN)
            }
        } else {
            binding.tvNoBudget.visibility = View.VISIBLE
            binding.cardBudgetInfo.visibility = View.GONE
        }
    }

    private fun setupBudgetChart() {
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val expensesByCategory = transactionRepository.getExpensesByCategory(month, year)

        if (expensesByCategory.isEmpty()) {
            binding.barChart.setNoDataText(getString(R.string.no_expenses_this_month))
            binding.barChart.invalidate()
            return
        }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        expensesByCategory.entries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))
            labels.add(entry.key)
        }

        val dataSet = BarDataSet(entries, "Expenses by Category")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#795548"),
            Color.parseColor("#607D8B")
        )
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.xAxis.granularity = 1f
        binding.barChart.xAxis.labelRotationAngle = 45f
        binding.barChart.animateY(1000)
        binding.barChart.invalidate()
    }

    private fun updateBudgetDisplay() {
        setupBudgetDisplay()
        setupBudgetChart()
    }

    override fun onResume() {
        super.onResume()
        updateBudgetDisplay()
    }
}