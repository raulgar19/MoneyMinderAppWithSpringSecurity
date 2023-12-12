package com.example.moneyminder.ui.screens.gastos

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GastosGrafScreen(
    gastosGrafViewModel: GastosGrafViewModel = hiltViewModel(),
    goBack: () -> Boolean,
    correo: String
) {
    val gastos = gastosGrafViewModel.gastos ?: emptyList()
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
                    text = "Estadísticas de los gastos",
                    color = Color.White,
                )
            }
        }
    ) {
        BackgroundImage()
        GastosGrafBody(gastosGrafViewModel, gastos)
        LaunchedEffect(correo) {
            gastosGrafViewModel.getUsuarioByCorreo(correo)
        }
    }
}

@Composable
fun GastosGrafBody(gastosGrafViewModel: GastosGrafViewModel, gastos: List<Gasto>) {
    val gastosTotales = gastosGrafViewModel.gastosTotales
    val gastosTotalesTransporte = gastosGrafViewModel.gastosTotalesTransporte
    val gastosTotalesAlimentacion = gastosGrafViewModel.gastosTotalesAlimentacion
    val gastosTotalesOcio = gastosGrafViewModel.gastosTotalesOcio
    val gastosTotalesAgua = gastosGrafViewModel.gastosTotalesAgua
    val gastosTotalesLuz = gastosGrafViewModel.gastosTotalesLuz
    val gastosTotalesHipoteca = gastosGrafViewModel.gastosTotalesHipoteca

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (gastos.size >= 2) {
            Box(
                modifier = Modifier.background(Color.White)
            ) {
                var puntos = ArrayList<LineChartData.Point>()
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                gastos.mapIndexed { index, ingresos ->
                    puntos.add(
                        LineChartData.Point(
                            value = ingresos.cantidad.toFloat(),
                            label = dateFormatter.format(ingresos.fecha)
                        )
                    )
                }
                var lineas = ArrayList<LineChartData>()
                lineas.add(
                    LineChartData(
                        points = puntos,
                        lineDrawer = SolidLineDrawer()
                    )
                )
                LineChart(
                    linesChartData = lineas,
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 30.dp)
                        .height(300.dp)
                )
            }
            Spacer(modifier = Modifier.padding(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Gastos Totales por Categorías", color = Color.White, fontSize = 20.sp)
            }
            val slices = listOf(
                PieChartData.Slice(gastosTotalesTransporte.toFloat(), Color.Green),
                PieChartData.Slice(gastosTotalesAlimentacion.toFloat(), Color.Blue),
                PieChartData.Slice(gastosTotalesOcio.toFloat(), Color.Yellow),
                PieChartData.Slice(gastosTotalesAgua.toFloat(), Color.Cyan),
                PieChartData.Slice(gastosTotalesLuz.toFloat(), Color.Magenta),
                PieChartData.Slice(gastosTotalesHipoteca.toFloat(), Color.Red)
            )

            PieChart(
                PieChartData(slices),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 30.dp, vertical = 30.dp)
            )
            ColorLegend(slices)
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
                    text = "Se necesitan al menos dos gastos",
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
        Spacer(modifier = Modifier.padding(5.dp))
        Text("Gastos Totales: $gastosTotales €", color = Color.White, fontSize = 20.sp)
    }
}

@Composable
fun ColorLegend(slices: List<PieChartData.Slice>) {
    val total = slices.sumByDouble { it.value.toDouble() }
    val halfSize = slices.size / 2
    val firstColumnSlices = slices.subList(0, halfSize)
    val secondColumnSlices = slices.subList(halfSize, slices.size)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ColorLegendColumn(firstColumnSlices, total)
        Spacer(modifier = Modifier.width(16.dp))
        ColorLegendColumn(secondColumnSlices, total)
    }
}

@Composable
fun ColorLegendColumn(slices: List<PieChartData.Slice>, total: Double) {
    Column {
        slices.forEach { slice ->
            ColorLegendItem(slice = slice, total = total)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ColorLegendItem(slice: PieChartData.Slice, total: Double) {
    val percentage = (slice.value / total) * 100
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = null,
            tint = slice.color,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Text(
            text = "${getCategoryLabel(slice)} - %.2f%%".format(percentage),
            color = Color.White
        )
    }
}

fun getCategoryLabel(slice: PieChartData.Slice): String {
    return when (slice.color) {
        Color.Green -> "Transporte"
        Color.Blue -> "Alimentación"
        Color.Yellow -> "Ocio"
        Color.Cyan -> "Agua"
        Color.Magenta -> "Luz"
        Color.Red -> "Hipoteca"
        else -> "Desconocido"
    }
}