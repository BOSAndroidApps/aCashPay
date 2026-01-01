package com.bos.payment.appName.data.model.travel.bus.searchBus

import com.google.gson.annotations.SerializedName


data class Buses (
  @SerializedName("ac"                           ) var ac                         : Boolean?                   = null,
  @SerializedName("arrival_Time"                 ) var arrivalTime                : String?                    = null,
  @SerializedName("available_Seats"              ) var availableSeats             : Int?                       = null,
  @SerializedName("boardingDetails"              ) var boardingDetails            : ArrayList<BoardingDetails> = arrayListOf(),
  @SerializedName("bookable"                     ) var bookable                   : Boolean?                   = null,
  @SerializedName("bus_Key"                      ) var busKey                     : String?                    = null,
  @SerializedName("bus_Type"                     ) var busType                    : String?                    = null,
  @SerializedName("departure_Time"               ) var departureTime              : String?                    = null,
  @SerializedName("dropPoint_Mandatory"          ) var dropPointMandatory         : Boolean?                   = null,
  @SerializedName("droppingDetails"              ) var droppingDetails            : ArrayList<DroppingDetails> = arrayListOf(),
  @SerializedName("fareMasters"                  ) var fareMasters                : ArrayList<FareMasters>     = arrayListOf(),
  @SerializedName("from_City"                    ) var fromCity                   : String?                    = null,
  @SerializedName("getFareMandatory"             ) var getFareMandatory           : Boolean?                   = null,
  @SerializedName("operator_Name"                ) var operatorName               : String?                    = null,
  @SerializedName("partial_Cancellation_Allowed" ) var partialCancellationAllowed : Boolean?                   = null,
  @SerializedName("seat_Layout"                  ) var seatLayout                 : Boolean?                   = null,
  @SerializedName("seat_Type"                    ) var seatType                   : Int?                       = null,
  @SerializedName("to_City"                      ) var toCity                     : String?                    = null,
  @SerializedName("travelDate"                   ) var travelDate                 : String?                    = null,
  @SerializedName("vehicle_Type"                 ) var vehicleType                : String?                    = null,
  @SerializedName("mTicket"                      ) var mTicket                    : Boolean?                   = null

)