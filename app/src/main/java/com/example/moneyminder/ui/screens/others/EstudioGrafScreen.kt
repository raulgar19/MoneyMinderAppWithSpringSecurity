package com.example.moneyminder.ui.screens.others

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneyminder.R
import com.example.moneyminder.ui.components.BackgroundImage
import com.example.moneyminder.ui.models.Gasto
import com.example.moneyminder.ui.models.Ingreso
import com.example.moneyminder.ui.screens.gastos.ColorLegend
import com.example.moneyminder.ui.screens.others.EstudioGrafViewModel
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.piechart.PieChartData
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EstudioGrafScreen(
    estudioGrafViewModel: EstudioGrafViewModel = hiltViewModel(),
    goBack: () -> Boolean,
    correo: String
) {
    val ingresos = estudioGrafViewModel.ingresos ?: emptyList()
    val gastos = estudioGrafViewModel.gastos ?: emptyList()
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = Color.Black) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Arrow Back",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        goBack()
                    }
                )
                Image(
                    painterResource(id = R.mipmap.carga),
                    contentDescription = "icono",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Comparativa Financiera",
                    color = Color.White,
                )
            }
        }
    ) {
        BackgroundImage()
        EstudioGrafBody(ingresos, gastos)
        LaunchedEffect(correo) {
            estudioGrafViewModel.getUsuarioByCorreo(correo)
        }
    }
}

@Composable
fun EstudioGrafBody(
    ingresos: List<Ingreso>,
    gastos: List<Gasto>,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (ingresos.size >= 2 && gastos.size >= 2) {
            Box(
                modifier = Modifier.background(Color.White)
            ) {
                var puntosIngresos = ArrayList<LineChartData.Point>()
                var puntosGastos = ArrayList<LineChartData.Point>()

                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                ingresos.mapIndexed { index, ingreso ->
                    puntosIngresos.add(
                        LineChartData.Point(
                            value = ingreso.cantidad.toFloat(),
                            label = dateFormatter.format(ingreso.fecha)
                        )
                    )
                }

                gastos.mapIndexed { index, gasto ->
                    puntosGastos.add(
                        LineChartData.Point(
                            value = gasto.cantidad.toFloat(),
                            label = dateFormatter.format(gasto.fecha)
                        )
                    )
                }

                var lineas = ArrayList<LineChartData>()
                lineas.add(
                    LineChartData(
                        points = puntosIngresos,
                        lineDrawer = SolidLineDrawer(color = Color.Blue)
                    )
                )
                lineas.add(
                    LineChartData(
                        points = puntosGastos,
                        lineDrawer = SolidLineDrawer(color = Color.Red)
                    )
                )

                LineChart(
                    linesChartData = lineas,
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 80.dp)
                        .height(300.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ColorLegend(color = Color.Blue, label = "Ingresos")
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorLegend(color = Color.Red, label = "Gastos")
                }
            }
        }
        else {
            Snackbar(
                backgroundColor = Color.DarkGray,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { Text(
                    text = "Se necesitan al menos dos ingresos y dos gastos",
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
                    Spacer(modifier = Modifier.padding(5.dp))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(16.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}
@Composable
fun ColorLegend(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
            )
        )
    }
}