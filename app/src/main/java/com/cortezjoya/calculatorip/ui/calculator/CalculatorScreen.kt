package com.cortezjoya.calculatorip.ui.calculator
import android.R.attr.left
import android.R.attr.textAlignment
import android.R.attr.value
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    var ipText by remember { mutableStateOf(TextFieldValue()) }
    var maskText by remember { mutableStateOf(TextFieldValue()) }
    var networkIp by remember { mutableStateOf("") }
    var broadcastIp by remember { mutableStateOf("") }
    var hostCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IP Calculator", fontFamily = FontFamily.Cursive, fontSize = 48.sp) },
                modifier = Modifier.fillMaxWidth().padding(top = 100.dp).padding(start = 60.dp)

            )
        },

        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = ipText,
                    onValueChange = { ipText = it },
                    label = { Text("IP Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = maskText,
                    onValueChange = { maskText = it },
                    label = { Text("Subnet Mask") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        calculateIP(ipText.text, maskText.text)?.let { (network, broadcast, hosts) ->
                            networkIp = network
                            broadcastIp = broadcast
                            hostCount = hosts
                        }
                    }
                ) {
                    Text("Calculate")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Network IP: $networkIp")
                Text("Broadcast IP: $broadcastIp")
                Text("Hosts Available: $hostCount")
            }
        }
    )
}

fun calculateIP(ip: String, mask: String): Triple<String, String, Int>? {
    try {
        val ipParts = ip.split(".").map { it.toInt() }
        val maskParts = mask.split(".").map { it.toInt() }

        val networkParts = ipParts.zip(maskParts) { ipPart, maskPart -> ipPart and maskPart }
        val networkIp = networkParts.joinToString(".")

        val broadcastParts = ipParts.zip(maskParts) { ipPart, maskPart -> ipPart or (maskPart.inv() and 0xFF) }
        val broadcastIp = broadcastParts.joinToString(".")

        val hostCount = 2.0.pow(32 - maskParts.sumOf { Integer.bitCount(it) }).toInt() - 2

        return Triple(networkIp, broadcastIp, hostCount)
    } catch (e: Exception) {
        return null
    }
}
