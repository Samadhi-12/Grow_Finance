package com.app.growfinance

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.app.growfinance.data.PreferenceManager

object NotificationHelper {


    fun pushTransactionNotification(title: String, amount: Float, category: String, date: String, type: String, context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val preferenceManager = PreferenceManager(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "GrowFinanceTransactionChannel",
                "Transaction Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, "GrowFinanceTransactionChannel")
            .setSmallIcon(R.drawable.wallet)
            .setContentTitle("💸 $category $type Transaction")
            .setContentText("$title: ${preferenceManager.getCurrency()} ${"%.2f".format(amount)} \n$date")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }


    fun dataResetNotification(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "GrowFinanceResetChannel",
                "Reset Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context,"GrowFinanceResetChannel")
            .setSmallIcon(R.drawable.market)
            .setContentTitle("You Reset Grow Finance App")
            .setContentText("All data removed and changed to default")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }



}
