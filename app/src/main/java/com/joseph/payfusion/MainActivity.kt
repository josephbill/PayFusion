package com.joseph.payfusion

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MpesaStkPushScreen()
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MpesaStkPushScreen(viewModel: MpesaViewModel = viewModel()) {
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var amount by remember { mutableStateOf(TextFieldValue()) }
    var accessToken by remember { mutableStateOf("") }
    var responseMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mpesa),
                contentDescription = "Mpesa Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "M-Pesa STK Push",
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Icon") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = "Money Icon") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedButton(
                onClick = {
                    viewModel.generateAccessToken { token ->
                        if (token != null) {
                            accessToken = token
                            viewModel.sendStkPush(
                                phoneNumber = phoneNumber.text,
                                amount = amount.text,
                                accessToken = token
                            ) {
                                responseMessage = it ?: "Unknown error occurred"
                            }
                        } else {
                            responseMessage = "Failed to fetch access token"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Send STK Push",
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = responseMessage,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Powered by Apen Softwares",
            )
        }
    }
}
