export default {
    user: {
        name: "user",
        header: [
            "CardNo",
            "Pin",
            "Name",
            "Password",
            "Group",
            "StartTime",
            "EndTime",
        ]
    },
    userauthorize: {
        name: "userauthorize",
        header: [
            "Pin",
            "AuthorizeDoorId",
            "AuthorizeTimezoneId"
        ]
    },
    timezone: {
        name: "timezone",
        header: [
            "TimezoneId",
            "SunTime1", "SunTime2", "SunTime3",
            "MonTime1", "MonTime2", "MonTime3",
            "TueTime1", "TueTime2", "TueTime3",
            "WedTime1", "WedTime2", "WedTime3",
            "ThuTime1", "ThuTime2", "ThuTime3",
            "FriTime1", "FriTime2", "FriTime3",
            "SatTime1", "SatTime2", "SatTime3",
            "Hol1Time1", "Hol1Time2", "Hol1Time3",
            "Hol2Time1", "Hol2Time2", "Hol2Time3",
            "Hol3Time1", "Hol3Time2", "Hol3Time3"
        ]
    },
    transaction: {
        name: "transaction",
        header: [
            "Pin",
            "Cardno",
            "Verified",
            "DoorID",
            "EventType",
            "InOutState",
            "Time_second",
        ]
    },
    templatev10: {
        name: "templatev10",
        header: [
            "Pin", "FingerID", "Valid", "Template", "Size"
        ]
    },
    template: {
        name: "templatev10",
        header: [
            "Pin", "FingerID", "Valid", "Template", "Resverd", "EndTag"
        ]
    },

    //device2.GetDeviceData_Pull("user", "CardNo\tPin\tPassword\tGroup\tStartTime\tEndTime");
    //device2.GetDeviceData_Pull("userauthorize", "Pin\tAuthorizeDoorId\tAuthorizeTimezoneId");
    //device2.GetDeviceData_Pull("transaction", "Pin\tCardno\tVerified\tDoorID\tEventType\tInOutState\tTime_second");
}