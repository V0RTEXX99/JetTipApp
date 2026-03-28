package com.aayaniqbal.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aayaniqbal.jettipapp.components.InputField
import com.aayaniqbal.jettipapp.ui.theme.JetTipAppTheme
import com.aayaniqbal.jettipapp.util.calculateTotalPerPerson
import com.aayaniqbal.jettipapp.util.calculateTotalTip
import com.aayaniqbal.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MyApp {
                //TopHeader()
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(
    content: @Composable () -> Unit
) {
    JetTipAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(
    totalPerPerson : Double = 0.0
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(150.dp)
            .clip(
                shape = RoundedCornerShape(corner = CornerSize(12.dp))
            ),
        color = Color(0xFFE3CDF5)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "₹${"%.2f".format(totalPerPerson)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun MainContent(

) {
    val splitByState = remember {
        mutableStateOf(1)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    BillForm(
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ) {
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {

    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    //val tipPercentage = (sliderPositionState.value * 100).toInt()
    val tipPercentage = remember(sliderPositionState.value) {
        mutableStateOf((sliderPositionState.value * 100).toInt())
    }



    Column(
        modifier = Modifier
            .padding(12.dp)
    ) {

        TopHeader(totalPerPerson = totalPerPersonState.value)

        Surface(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())

                        keyboardController?.hide()
                    }
                )
            if (validState) {
                Row(
                    modifier = Modifier
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value = if(splitByState.value > 1) splitByState.value - 1 else 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage.value)
                            }
                        )
                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value += 1
                                }
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage.value)
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(
                        text = "₹${tipAmountState.value}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${tipPercentage.value}%"
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    //Slider
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage.value)
                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage.value)
                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp),
                        steps = 9,
                        onValueChangeFinished = {
                            //Log.d("SliderChgd", "BillForm: Finished")
                        }
                    )
                }
            } else {
                Box() {

                }
            }
            }
        }
    }
}



//@Preview(showBackground = true)
@Composable
fun AppPreview() {
    JetTipAppTheme {
        MyApp {
            Text(
                text = "Hello Again!"
            )
        }
    }
}