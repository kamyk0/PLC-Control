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
            findViewById(R.id.btnWrite1),
            findViewById(R.id.btnWrite2),
            findViewById(R.id.btnWrite3),
            findViewById(R.id.btnWrite4),
            findViewById(R.id.btnWrite5),
            findViewById(R.id.btnWrite6),
            findViewById(R.id.btnWrite7),
            findViewById(R.id.btnWrite8)
        )
        buttons2 = listOf(
            findViewById(R.id.btnWrite9),
            findViewById(R.id.btnWrite10),
            findViewById(R.id.btnWrite11),
            findViewById(R.id.btnWrite12),
            findViewById(R.id.btnWrite13),
            findViewById(R.id.btnWrite14),
            findViewById(R.id.btnWrite15),
            findViewById(R.id.btnWrite16)
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
            findViewById(R.id.light8),
            findViewById(R.id.light9),
            findViewById(R.id.light10),
            findViewById(R.id.light11),
            findViewById(R.id.light12),
            findViewById(R.id.light13),
            findViewById(R.id.light14),
            findViewById(R.id.light15),
            findViewById(R.id.light16)
        )

        // Connect button
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            connectToPLC()
        }

        // Set up individual button listeners for precise bit toggling
        setupButtonListeners()

        // Read button


        // Initial connection
        connectToPLC()
    }

    private fun setupButtonListeners() {
        buttons1.forEachIndexed { index, button ->
            button.setOnClickListener {
                toggleSpecificBit(index, 0)
            }
        }
        buttons2.forEachIndexed { index, button ->
            button.setOnClickListener {
                toggleSpecificBit(index, 1)
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

    private fun toggleSpecificBit(bitPosition: Int, byteOffset: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Read specific byte based on offset
                val dataRead = connector?.read(DaveArea.DB, 2, 1, byteOffset)
                val currentByte = dataRead?.get(0) ?: 0.toByte()

                // Create a mask with only the specific bit set
                val bitMask = (1 shl bitPosition).toByte()

                // Toggle only the specific bit
                val newByte = (currentByte.toInt() xor bitMask.toInt()).toByte()

                // Write modified byte to PLC
                connector?.write(DaveArea.DB, 2, byteOffset, byteArrayOf(newByte))

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
                val dataRead0 = connector?.read(DaveArea.DB, 2, 1, 0)
                val dataRead1 = connector?.read(DaveArea.DB, 2, 1, 1)

                withContext(Dispatchers.Main) {
                    dataRead0?.get(0)?.let { byte0 ->
                        dataRead1?.get(0)?.let { byte1 ->
                            lights.forEachIndexed { index, light ->
                                val isOn = if (index < 8) {
                                    byte0.toInt() and (1 shl index) != 0
                                } else {
                                    byte1.toInt() and (1 shl (index - 8)) != 0
                                }
                                light.setColorFilter(
                                    if (isOn) Color.GREEN else Color.RED
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}