package com.bos.payment.appName.ui.view.travel.flightBooking

import android.os.Build
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.bos.payment.appName.data.model.travel.flight.DataItem
import com.bos.payment.appName.data.model.travel.flight.FlightsItem
import com.bos.payment.appName.data.model.travel.flight.TripDetailsItem
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

class FlightConstant {

    companion object{

        // for flight
        var adultCount: Int = 1
        var childCount: Int = 0
        var infantCount: Int = 0
        var className : String = "Economy Class"
        var classnameforprint : String = "Economy"
        var classNumber: String = "0"
        var totalCount: String = ""
        var bookingType: Int = 0

        // for air ticket .....................

        var BaseFare: String = ""
        var TaxAndFees: String = ""
        var GrossFare: String = ""
        val airlinecode ="airlinecode"


        var  fromCityName: String = "Delhi"
        var  fromAirportCode: String = "DEL"
        var  fromAirportName: String = "Indira Gandhi International Airport"
        var  fromCountryName: String = "India"

        var travelType:Int=0

        var toCityName: String = "Mumbai"
        var toAirportCode: String= "BOM"
        var toAirportName: String = "Chhatrapati Shivaji International Airport"
        var toCountryName: String = "India"

        var datepassangerandclassstring: String = ""

        var totalDurationTime: String = ""

        var travelDate:String= ""

        var DepartureDateAndTime:String= ""

        @kotlin.jvm.JvmField
        var checkFrom: Boolean = false
        var checkFromBus: Boolean = false

        @kotlin.jvm.JvmField
        var checkTo: Boolean = false

        var nonStops : Boolean = false
        var Stops : Boolean =false

        var before6layout : Boolean = false
        var after6amlayout : Boolean =false
        var after12pmlayout : Boolean = false
        var after6pmlayout : Boolean =false


        var getAllFlightListAdapter: ArrayAdapter<DataItem>? = null
        var FlightList: MutableList<DataItem>? = mutableListOf()
        var FlightHint: MutableList<DataItem>? = mutableListOf()


        var srCitizen:Boolean= false
        var studentFare:Boolean= false
        var defenceFare:Boolean= false


        var TripDetailsList: MutableList<TripDetailsItem?> = mutableListOf()
        var FlightDetails: MutableList<FlightsItem?> = mutableListOf()
        var airportNameList: MutableList<Pair<String, Boolean>> = mutableListOf()
        var AllAirNameList: MutableList<Pair<String, Boolean>> = mutableListOf()
        var FlightListForFilter: MutableList<FlightsItem> = mutableListOf()
        var AllFlightList: MutableList<FlightsItem> = mutableListOf()



        fun GetAirlineLogo(airlineCode: String): String {
            return "https://content.airhex.com/content/logos/airlines_${airlineCode}_30_30_s.png"
        }



        @RequiresApi(Build.VERSION_CODES.O)
        fun splitDateTime(datetimeStr: String): Pair<String, String> {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
            val dateTime = LocalDateTime.parse(datetimeStr, formatter)

            val datePart = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            val timePart = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

            return Pair(datePart, timePart)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun splitDateTimeTicket(datetimeStr: String): Pair<String, String> {
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
            val dateTime = LocalDateTime.parse(datetimeStr, formatter)

            val datePart = dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            val timePart = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

            return Pair(datePart, timePart)
        }



        fun calculateTotalFlightDuration(flightSegments: List<Map<String, String>>): String {
            val format = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())

            // Parse first departure and last arrival
            val firstDeparture = format.parse(flightSegments.first()["departure_DateTime"]!!)
            val lastArrival = format.parse(flightSegments.last()["arrival_DateTime"]!!)

            val durationInMillis = lastArrival!!.time - firstDeparture!!.time

            val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60

            return "${hours}h ${minutes}m"
        }


        fun calculateTotalFlightDurationTicketPrint(flightSegments: List<Map<String, String>>): String {
            val format = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

            // Parse first departure and last arrival
            val firstDeparture = format.parse(flightSegments.first()["departure_DateTime"]!!)
            val lastArrival = format.parse(flightSegments.last()["arrival_DateTime"]!!)

            val durationInMillis = lastArrival!!.time - firstDeparture!!.time

            val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60

            return "${hours}h ${minutes}m"
        }

        fun convertDurationToReadableFormat(duration: String, addHours: Int = 0): String {
            val parts = duration.split(":")
            val hours = parts[0].toIntOrNull() ?: 0
            val minutes = parts[1].toIntOrNull() ?: 0

            val totalHours = hours + addHours

            return "${totalHours}h ${minutes}m"
        }


        fun animateOpenFromTripCard(booknowView: View, tripcard: View) {
            // Ensure visibility and camera distance for 3D effect
            booknowView.cameraDistance = 8000 * booknowView.context.resources.displayMetrics.density
            booknowView.visibility = View.VISIBLE

            // Get tripcard Y location
            val tripLocation = IntArray(2)
            tripcard.getLocationOnScreen(tripLocation)
            val tripY = tripLocation[1]

            // Get booknow original Y location
            val bookLocation = IntArray(2)
            booknowView.getLocationOnScreen(bookLocation)
            val bookY = bookLocation[1]

            val offsetY = tripY - bookY

            // Apply initial state
            booknowView.translationY = offsetY.toFloat()
            booknowView.rotationX = -90f
            booknowView.alpha = 0f
            booknowView.scaleY = 0.8f

            // Animate to normal state
            booknowView.animate()
                .translationY(0f)
                .rotationX(0f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(400)
                .start()

        }


        fun animateCloseToTripCard(booknowView: View, tripcard: View) {
            // Ensure camera distance for 3D effect
            booknowView.cameraDistance = 8000 * booknowView.context.resources.displayMetrics.density

            // Get tripcard Y location
            val tripLocation = IntArray(2)
            tripcard.getLocationOnScreen(tripLocation)
            val tripY = tripLocation[1]

            // Get booknow original Y location
            val bookLocation = IntArray(2)
            booknowView.getLocationOnScreen(bookLocation)
            val bookY = bookLocation[1]

            val offsetY = tripY - bookY

            // Animate to close state
            booknowView.animate()
                .translationY(offsetY.toFloat())
                .rotationX(90f)
                .scaleY(0.8f)
                .alpha(0f)
                .setDuration(600)
                .withEndAction {
                    booknowView.visibility = View.GONE

                    // Reset the view to default state for future re-use
                    booknowView.translationY = 0f
                    booknowView.rotationX = 0f
                    booknowView.scaleY = 1f
                    booknowView.alpha = 1f
                }
                .start()
        }

        fun formatDate1(inputDate: String): String {
            val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())

            return try {
                val date = inputFormat.parse(inputDate)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                "Invalid Date"
            }
        }


        fun formatDate2(inputDate: String): String {
            val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

            return try {
                val date = inputFormat.parse(inputDate)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                "Invalid Date"
            }
        }


        fun calculateLayoverTime(arrival: String, departure: String): String {
            val format = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
            val arrivalTime = format.parse(arrival)
            val departureTime = format.parse(departure)

            if (arrivalTime != null && departureTime != null && departureTime.after(arrivalTime)) {
                val diffInMillis = departureTime.time - arrivalTime.time
                val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60
                return "${hours}h ${minutes}m"
            }
            return "Invalid Time"
        }



        fun View.slideInFromTop(duration: Long = 300) {
            if (visibility == View.VISIBLE) return
            alpha = 0f
            translationY = -height.toFloat()
            visibility = View.VISIBLE
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(duration)
                .start()
        }

        fun View.slideOutToTop(duration: Long = 300) {
            if (visibility != View.VISIBLE) return
            animate()
                .translationY(-height.toFloat())
                .alpha(0f)
                .setDuration(duration)
                .withEndAction { visibility = View.GONE }
                .start()
        }




    }

}