package com.weatherassignment.Model

import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("last_updated_epoch" ) var lastUpdatedEpoch : Double?       = null,
    @SerializedName("last_updated"       ) var lastUpdated      : String?    = null,
    @SerializedName("temp_c"             ) var tempC            : Double?       = null,
    @SerializedName("temp_f"             ) var tempF            : Double?    = null,
    @SerializedName("is_day"             ) var isDay            : Double?       = null,
    @SerializedName("condition"          ) var condition        : Condition? = Condition(),
    @SerializedName("wind_mph"           ) var windMph          : Double?    = null,
    @SerializedName("wind_kph"           ) var windKph          : Double?    = null,
    @SerializedName("wind_degree"        ) var windDegree       : Double?       = null,
    @SerializedName("wind_dir"           ) var windDir          : String?    = null,
    @SerializedName("pressure_mb"        ) var pressureMb       : Double?       = null,
    @SerializedName("pressure_in"        ) var pressureIn       : Double?    = null,
    @SerializedName("precip_mm"          ) var precipMm         : Double?       = null,
    @SerializedName("precip_in"          ) var precipIn         : Double?       = null,
    @SerializedName("humidity"           ) var humidity         : Double?       = null,
    @SerializedName("cloud"              ) var cloud            : Double?       = null,
    @SerializedName("feelslike_c"        ) var feelslikeC       : Double?    = null,
    @SerializedName("feelslike_f"        ) var feelslikeF       : Double?    = null,
    @SerializedName("windchill_c"        ) var windchillC       : Double?    = null,
    @SerializedName("windchill_f"        ) var windchillF       : Double?    = null,
    @SerializedName("heatindex_c"        ) var heatindexC       : Double?    = null,
    @SerializedName("heatindex_f"        ) var heatindexF       : Double?       = null,
    @SerializedName("dewpoDouble_c"         ) var dewpoDoubleC        : Double?    = null,
    @SerializedName("dewpoDouble_f"         ) var dewpoDoubleF        : Double?    = null,
    @SerializedName("vis_km"             ) var visKm            : Double?       = null,
    @SerializedName("vis_miles"          ) var visMiles         : Double?       = null,
    @SerializedName("uv"                 ) var uv               : Double?       = null,
    @SerializedName("gust_mph"           ) var gustMph          : Double?    = null,
    @SerializedName("gust_kph"           ) var gustKph          : Double?    = null,
    @SerializedName("short_rad"          ) var shortRad         : Double?       = null,
    @SerializedName("diff_rad"           ) var diffRad          : Double?       = null,
    @SerializedName("dni"                ) var dni              : Double?       = null,
    @SerializedName("gti"                ) var gti              : Double?       = null

)
