package com.bos.payment.appName.data.model.travel.bus.forservicecharge

data class SeattypeModel(
    var sleeper: Int ,
    var seater : Int ,
    var busType : String,
    var busfare : List<SeatFair>
)

data class SeatFair(
    var basicamount :Double,
    var otheramount : Double,
    var length : String ?
)

