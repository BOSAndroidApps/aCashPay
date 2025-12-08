package com.bos.payment.appName.data.model.travel.bus.busSeatMap

import com.google.gson.annotations.SerializedName


data class FareMaster (

  @SerializedName("basic_Amount"         ) var basicAmount         : Double?                   = null,
  @SerializedName("cancellation_Charges" ) var cancellationCharges : Double?                   = null,
  @SerializedName("fareDetails"          ) var fareDetails         : ArrayList<FareDetails> = arrayListOf(),
  @SerializedName("gst"                  ) var gst                 : Double?                   = null,
  @SerializedName("gross_Commission"     ) var grossCommission     : Double?                   = null,
  @SerializedName("net_Commission"       ) var netCommission       : Double?                   = null,
  @SerializedName("other_Amount"         ) var otherAmount         : Double?                = null,
  @SerializedName("service_Fee_Amount"   ) var serviceFeeAmount    : Double?                   = null,
  @SerializedName("total_Amount"         ) var totalAmount         : Double?                = null,
  @SerializedName("trade_Markup_Amount"  ) var tradeMarkupAmount   : Double?                   = null

)