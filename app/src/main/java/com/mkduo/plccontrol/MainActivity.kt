package com.mkduo.plccontrol

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.s7connector.api.DaveArea
import com.github.s7connector.api.S7Connector
import com.github.s7connector.impl.S7TCPConnection
import com.github.s7connector.api.S7Serializer
import com.github.s7connector.api.factory.S7ConnectorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {
    private var plc: S7Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val btnWrite = findViewById<Button>(R.id.btnWrite)
        val btnRead = findViewById<Button>(R.id.btnRead)

        btnConnect.setOnClickListener {
            connectToPLC(true)
        }

        btnWrite.setOnClickListener {
            writeToPLC(true)
        }

        btnRead.setOnClickListener {
            writeToPLC(false)
        }
    }
    private var connector: S7Connector? = null
    private fun connectToPLC(newBoolValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                connector = S7ConnectorFactory
                    .buildTCPConnector()
                    .withHost("192.168.1.196")
                    .withPort(102)
                    .build()

                // Prepare byte array to write
                val dataToWrite = ByteArray(1)
                dataToWrite[0] = if (newBoolValue) 1.toByte() else 0.toByte()

                // Write to DB1 at offset 0
                connector?.write(DaveArea.DB, 1, 0, dataToWrite)

                println("Connected to PLC and updated boolean value!")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Connection error: ${e.message}")
            }
        }
    }





    /* CoroutineScope(Dispatchers.IO).launch {
        try {
            plc = S7TCPConnection("192.168.0.1", 0,1,102, 5000) // IP symulatora PLC i port
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Połączono z PLC!", Toast.LENGTH_SHORT).show()
                Log.d("PLC_CONNECTION", "Połączono z PLC")
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Błąd połączenia: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("PLC_CONNECTION", "Błąd połączenia: ${e.message}")
            }
        }
    } */
    }

    private fun writeToPLC(boolean: Boolean) {
        val bs = ByteArray(10) // Tablica 10 elementów wypełniona zerami
        bs[0] = 0x00.toByte()

    }

    private fun readFromPLC() { /*
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dbNumber = 1
                val startAddress = 0
                val size = 1 // Odczytaj 1 bajt

                val data = plc?.readBytes(S7Client.DB, dbNumber, startAddress, size)

                runOnUiThread {
                    if (data != null) {
                        Toast.makeText(this@MainActivity, "Odczytano dane: ${data[0]}", Toast.LENGTH_SHORT).show()
                        Log.d("PLC_READ", "Odczytano dane: ${data.contentToString()}")
                    } else {
                        Toast.makeText(this@MainActivity, "Błąd odczytu: brak danych", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Błąd odczytu: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("PLC_READ", "Błąd odczytu: ${e.message}")
                }
            }
        }*/
    }


