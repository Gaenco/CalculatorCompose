package com.cortezjoya.calculatorip.ui.calculator

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.math.pow

@Preview(showBackground = true)
@Composable
fun AppContent() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val ipAddressState = remember { mutableStateOf("") }
            val networkAddressState = remember { mutableStateOf("") }
            val broadcastAddressState = remember { mutableStateOf("") }
            val hostsAvailableState = remember { mutableStateOf("") }

            HeaderTitle()
            IPTextField(ipAddressState)

            Spacer(modifier = Modifier.height(16.dp))

            CalculateButton(
                ipAddress = ipAddressState.value,
                onCalculate = { calculateIPDetails(ipAddressState.value,
                    networkAddressState, broadcastAddressState, hostsAvailableState) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            IPDetails(
                networkAddress = networkAddressState.value,
                broadcastAddress = broadcastAddressState.value,
                hostsAvailable = hostsAvailableState.value
            )
        }
    }


@Composable
fun HeaderTitle(){
    Text(
        text = "IP Calculator",
        Modifier.padding(bottom = 65.dp),
        color = Color.White,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Cursive
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IPTextField(ipAddressState: MutableState<String>) {
    TextField(
        value = ipAddressState.value,
        onValueChange = { ipAddressState.value = it },
        label = { Text("IP Address") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CalculateButton(
    ipAddress: String,
    onCalculate: () -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (isValidIPAddress(ipAddress)) {
                onCalculate()
            } else {
                Toast.makeText(context, "Invalid IP Address", Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        Text("Calculate", color = Color.Black)
    }
}

@Composable
fun IPDetails(
    networkAddress: String,
    broadcastAddress: String,
    hostsAvailable: String
) {
    Text("Network Address: $networkAddress")
    Text("Broadcast Address: $broadcastAddress")
    Text("Hosts Available: $hostsAvailable")
}

fun isValidIPAddress(ipAddress: String): Boolean {
    val pattern = """^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$""".toRegex()
    return pattern.matches(ipAddress)
}

fun Int.toBinaryString(): String {
    return Integer.toBinaryString(this).padStart(8, '0')
}

fun calculateIPDetails(
    ipAddress: String,
    networkAddressState: MutableState<String>,
    broadcastAddressState: MutableState<String>,
    hostsAvailableState: MutableState<String>
) {
    try {
        val inetAddress = InetAddress.getByName(ipAddress)

        val subnetMaskArray = inetAddress.hostAddress.split(".")
        val networkAddressArray = subnetMaskArray.toMutableList()
        val broadcastAddressArray = subnetMaskArray.toMutableList()

        val hostBits = 32 - subnetMaskArray.sumBy { Integer.bitCount(it.toInt()) }

        // Conversion de ip y mascara a binario
        val ipAddressBinary = subnetMaskArray.joinToString("") { it.toInt().toBinaryString() }
        val subnetMaskBinary = subnetMaskArray.joinToString("") { it.toInt().toBinaryString() }

        //  Calculando la ip
        val networkAddressBinary = ipAddressBinary.zip(subnetMaskBinary) { ipBit, subnetBit ->
            if (subnetBit == '1') ipBit else '0'
        }.joinToString("")

        // Calcular el broadcast
        val broadcastAddressBinary = ipAddressBinary.zip(subnetMaskBinary) { ipBit, subnetBit ->
            if (subnetBit == '1') ipBit else '1'
        }.joinToString("")

        // Conversion a decimal
        val networkAddressValue = networkAddressBinary.chunked(8).map { binary -> Integer.parseInt(binary, 2) }
        val broadcastAddressValue = broadcastAddressBinary.chunked(8).map { binary -> Integer.parseInt(binary, 2) }
        val hostsAvailableValue = 2.0.pow(hostBits.toDouble()) - 2

        networkAddressState.value = networkAddressValue.joinToString(".")
        broadcastAddressState.value = broadcastAddressValue.joinToString(".")
        hostsAvailableState.value = hostsAvailableValue.toInt().toString()
    } catch (e: UnknownHostException) {
        networkAddressState.value = ""
        broadcastAddressState.value = ""
        hostsAvailableState.value = ""
    }
}


