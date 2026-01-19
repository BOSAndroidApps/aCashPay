package com.bos.payment.appName.data.model.loginSignUp

import com.google.gson.annotations.SerializedName


data class Data (
  @SerializedName("userID"          ) var userID          : String? = null,
  @SerializedName("agentPassword"   ) var agentPassword   : String? = null,
  @SerializedName("agentType"       ) var agentType       : String? = null,
  @SerializedName("applicationType" ) var applicationType : String? = null,
  @SerializedName("firstName"       ) var firstName       : String? = null,
  @SerializedName("lastName"        ) var lastName        : String? = null,
  @SerializedName("emailId"         ) var emailId         : String? = null,
  @SerializedName("mobileNo"        ) var mobileNo        : String? = null,
  @SerializedName("adminBaseUrl"    ) var adminBaseUrl    : String? = null,
  @SerializedName("adminCode"       ) var adminCode       : String? = null,
  @SerializedName("merchantCode"    ) var merchantCode    : String? = null,
  @SerializedName("superAdminCode"  ) var superAdminCode  : String? = null,
  @SerializedName("refrenceID"      ) var refrenceID      : String? = null,
  @SerializedName("agencyname"      ) var agencyname      : String? = null,
  @SerializedName("fullName"        ) var fullName        : String? = null,
  @SerializedName("state"           ) var state           : String? = null,
  @SerializedName("applicationMode" ) var applicationmode           : String? = null,
  @SerializedName("kycApproved"     ) var kycapproved           : String? = null,

  )