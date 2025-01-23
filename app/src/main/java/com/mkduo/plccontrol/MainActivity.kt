package com.mkduo.plccontrol

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.s7connector.api.DaveArea
import com.github.s7connector.api.S7Connector
import com.github.s7connector.api.factory.S7ConnectorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var connector: S7Connector? = null
    private lateinit var buttons1: List<Button>
    private lateinit var buttons2: List<Button>
    private lateinit var lights: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Buttons
        buttons1 = listOf(
            findViewById(R.id.btnWrite11),
            findViewById(R.id.btnWrite12),
            findViewById(R.id.btnWrite13),
            findViewById(R.id.btnWrite14),
            findViewById(R.id.btnWrite15),
            findViewById(R.id.btnWrite16),
            findViewById(R.id.btnWrite17),
            findViewById(R.id.btnWrite18)
        )
        buttons2 = listOf(
            findViewById(R.id.btnWrite01),
            findViewById(R.id.btnWrite02),
            findViewById(R.id.btnWrite03),
            findViewById(R.id.btnWrite04),
            findViewById(R.id.btnWrite05),
            findViewById(R.id.btnWrite06),
            findViewById(R.id.btnWrite07),
            findViewById(R.id.btnWrite08)
        )

        // Lights
        lights = listOf(
            findViewById(R.id.light1),
            findViewById(R.id.light2),
            findViewById(R.id.light3),
            findViewById(R.id.light4),
            findViewById(R.id.light5),
            findViewById(R.id.light6),
            findViewById(R.id.light7),
            findViewById(R.id.light8)
        )

        // Connect button
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            connectToPLC()
        }

        // Set up individual button listeners for precise bit toggling
        setupButtonListeners()

        // Read button
        findViewById<Button>(R.id.read).setOnClickListener {
            readFromPLC()
        }

        // Initial connection
        connectToPLC()
    }

    private fun setupButtonListeners() {
        buttons1.forEachIndexed { index, button ->
            button.setOnClickListener {
                toggleSpecificBit(index)
            }
        }
        buttons2.forEachIndexed { index, button ->
            button.setOnClickListener {
                toggleSpecificBit(index + buttons1.size)
            }
        }
    }

    private fun connectToPLC() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                connector = S7ConnectorFactory
                    .buildTCPConnector()
                    .withHost("192.168.1.196")
                    .withPort(102)
                    .build()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun toggleSpecificBit(bitPosition: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Read current byte
                val dataRead = connector?.read(DaveArea.DB, 2, 1, 0)
                val currentByte = dataRead?.get(0) ?: 0.toByte()

                // Create a mask with only the specific bit set
                val bitMask = (1 shl bitPosition).toByte()

                // Toggle only the specific bit
                val newByte = (currentByte.toInt() xor bitMask.toInt()).toByte()

                // Write modified byte to PLC
                connector?.write(DaveArea.DB, 2, 0, byteArrayOf(newByte))

                // Update UI to reflect current state
                withContext(Dispatchers.Main) {
                    readFromPLC()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun readFromPLC() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataRead = connector?.read(DaveArea.DB, 2, 1, 0)

                withContext(Dispatchers.Main) {
                    dataRead?.get(0)?.let { byte ->
                        lights.forEachIndexed { index, light ->
                            val isOn = byte.toInt() and (1 shl index) != 0
                            light.setColorFilter(
                                if (isOn) Color.GREEN else Color.RED
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}