package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.model.PrayerLogEntity
import com.example.data.model.QazaRecordEntity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupData(
    val exportDate: String,
    val prayerLogs: List<PrayerLogEntity>,
    val qazaRecords: List<QazaRecordEntity>
)

object BackupRestoreHelper {

    fun createBackupJson(
        prayerLogs: List<PrayerLogEntity>,
        qazaRecords: List<QazaRecordEntity>
    ): String {
        val root = JSONObject()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        root.put("appName", "NamazTracker")
        root.put("version", "1.0")
        root.put("exportDate", dateFormat.format(Date()))

        // Prayer logs array
        val logsArray = JSONArray()
        for (log in prayerLogs) {
            val obj = JSONObject().apply {
                put("date", log.date)
                put("prayerName", log.prayerName)
                put("status", log.status)
                put("isSunnah", log.isSunnah)
                put("notes", log.notes ?: "")
            }
            logsArray.put(obj)
        }
        root.put("prayerLogs", logsArray)

        // Qaza records array
        val qazaArray = JSONArray()
        for (qaza in qazaRecords) {
            val obj = JSONObject().apply {
                put("prayerName", qaza.prayerName)
                put("totalMissed", qaza.totalMissed)
                put("completed", qaza.completed)
            }
            qazaArray.put(obj)
        }
        root.put("qazaRecords", qazaArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData? {
        return try {
            val root = JSONObject(jsonString)
            val exportDate = root.optString("exportDate", "Unknown")

            val prayerLogs = mutableListOf<PrayerLogEntity>()
            val logsArray = root.optJSONArray("prayerLogs") ?: JSONArray()
            for (i in 0 until logsArray.length()) {
                val obj = logsArray.getJSONObject(i)
                prayerLogs.add(
                    PrayerLogEntity(
                        date = obj.getString("date"),
                        prayerName = obj.getString("prayerName"),
                        status = obj.optString("status", "pending"),
                        isSunnah = obj.optInt("isSunnah", 0),
                        notes = obj.optString("notes", "")
                    )
                )
            }

            val qazaRecords = mutableListOf<QazaRecordEntity>()
            val qazaArray = root.optJSONArray("qazaRecords") ?: JSONArray()
            for (i in 0 until qazaArray.length()) {
                val obj = qazaArray.getJSONObject(i)
                qazaRecords.add(
                    QazaRecordEntity(
                        prayerName = obj.getString("prayerName"),
                        totalMissed = obj.optInt("totalMissed", 0),
                        completed = obj.optInt("completed", 0)
                    )
                )
            }

            BackupData(
                exportDate = exportDate,
                prayerLogs = prayerLogs,
                qazaRecords = qazaRecords
            )
        } catch (e: Exception) {
            null
        }
    }

    fun shareBackup(context: Context, backupJson: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, backupJson)
            putExtra(Intent.EXTRA_SUBJECT, "Namaz Tracker Backup (${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())})")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Namaz Tracker Backup")
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }

    fun copyToClipboard(context: Context, text: String, isBangla: Boolean = false) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Namaz Tracker Backup", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            if (isBangla) "ব্যাকআপ ডাটা ক্লিপবোর্ডে কপি করা হয়েছে" else "Backup copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }
}
