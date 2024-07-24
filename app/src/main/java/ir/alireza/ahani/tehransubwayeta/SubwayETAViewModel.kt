import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.alireza.ahani.tehransubwayeta.R
import ir.alireza.ahani.tehransubwayeta.ui.theme.DarkColorScheme
import ir.alireza.ahani.tehransubwayeta.ui.theme.TehranSubwayETATheme
import ir.alireza.ahani.tehransubwayeta.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class Station(val name: String, val id: Int)
data class EtaDetail(val ETA: String, val ETAValue: Int?, val ETAValueText: String, val NickName: String?)
data class SubwayEtaRequest(val StationID: Int?)
data class SubwayRouteStationData(
    val DestinationName: String,
    val Details: List<EtaDetail>,
    val Direction: String,
    val ErrorText: String?,
    val OriginationName: String,
    val RouteCode: Int?,
    val RouteID: Int = 0,
    val StationID: Int = 0,
    val StationName: String,
    val StationOrder: Int,
    val isFavorite: Boolean?,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

interface EtaApi {
    @POST("SubwayStationETA")
    fun getSubwayETA(@Body body: SubwayEtaRequest): Call<List<SubwayRouteStationData?>?>
}


class SubwayETAViewModel(application: Application) : AndroidViewModel(application) {
    private var api: EtaApi

    var lastUpdateTime by mutableStateOf<String?>(null)

    var selectedStation by mutableStateOf<Station?>(null)
    var searchQuery by mutableStateOf("")

    var isTracking by mutableStateOf(false)
    var isSearching by mutableStateOf(false)

    private var stations = mutableListOf<Station>()
    var searchedStations = mutableListOf<Station>()
    var etaResponse = mutableListOf<SubwayRouteStationData>()

    fun startTracking() {
        if (selectedStation == null) return
        isTracking = true

        viewModelScope.launch {
            while (isTracking) {
                fetchETA()
                delay(10000L)
            }
        }
    }

    private fun fetchETA() {
        api.getSubwayETA(SubwayEtaRequest(selectedStation?.id)).enqueue(object : Callback<List<SubwayRouteStationData?>?> {
            override fun onFailure(call: Call<List<SubwayRouteStationData?>?>, e: Throwable) {
                Toast.makeText(
                    getApplication(),
                    "اشکال در دریافت داده",
                    Toast.LENGTH_LONG
                ).show()
                updateLastUpdateTime()
                stopTracking()
            }

            override fun onResponse(
                call: Call<List<SubwayRouteStationData?>?>,
                response: Response<List<SubwayRouteStationData?>?>
            ) {
                if (response.body() != null) {
                    etaResponse.clear()
                    response.body()!!.forEach { it ->
                        if (it != null) {
                            etaResponse.add(it)
                        }
                    }
                    updateLastUpdateTime()
                }
            }
        })
    }

    fun stopTracking() {
        isTracking = false
        lastUpdateTime = null
        searchedStations.clear()
        etaResponse.clear()
        searchQuery = ""
        selectedStation = null
    }

    private fun loadStations() {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = getApplication<Application>().resources.openRawResource(R.raw.stations)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            jsonObject.keys().forEach { stationName ->
                val stationId = jsonObject.getInt(stationName)
                stations.add(Station(stationName, stationId))
            }
        }
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://application2.irantracking.com/modsapi/api/PublicTransport/") // Replace with actual API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(EtaApi::class.java)

        loadStations()
    }

    private fun updateLastUpdateTime() {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        lastUpdateTime = sdf.format(Date())
    }

    fun changeQuery(query: String) {
        searchQuery = query
        searchedStations.clear()

        if(query.length < 3) {
            return
        }

        for (station in stations) {
            if (station.name.contains(searchQuery, true)) {
                searchedStations.add(station)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubwayETATracker(viewModel: SubwayETAViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "تخمین زمان رسیدن مترو",
            style = Typography.headlineMedium,
        )

        SearchBar(query = viewModel.searchQuery,
            onQueryChange = { viewModel.changeQuery(it) },
            onSearch = { viewModel.changeQuery(it) },
            active = viewModel.isSearching,
            onActiveChange = { viewModel.isSearching = it },
            enabled = !viewModel.isTracking) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(viewModel.searchedStations) { station ->
                    Button(onClick = {
                        viewModel.selectedStation = station
                        viewModel.searchQuery = station.name
                        viewModel.isSearching = false
                    }) {
                        Text(text = station.name)
                    }
                }
            }

        }

        Button(onClick = { viewModel.startTracking() }, enabled = !viewModel.isTracking) {
            Text("شروع رهگیری")
        }

        Button(onClick = { viewModel.stopTracking() }, enabled = viewModel.isTracking) {
            Text("توقف رهگیری")
        }

        viewModel.lastUpdateTime?.let { time ->
            Text("آخرین بررسی: $time")
        }

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(viewModel.etaResponse) { response ->
                response.Details.forEach { eta ->
                    Log.i("eta", eta.toString())
                    ETAResultComponent(response.OriginationName, response.DestinationName, eta.ETA)
                }
            }
        }
    }
}

@Composable
fun ETAResultComponent(origin: String, destination: String, etaTime: String) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkColorScheme.onSurface.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = origin,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Arrow Icon",
                )

                Text(
                    text = destination,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val etaText = when (etaTime) {
                    "--" -> { "داده ای پیدا نشد." }
                    else -> { etaTime }
                }

                val etaIcon = when (etaTime) {
                    "--" -> { Icons.Default.Warning }
                    else -> { Icons.Default.Info }
                }

                Text(
                    text = etaText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = etaIcon,
                    contentDescription = "Clock Icon",
                )
            }
        }
    }
}