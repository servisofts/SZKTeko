export default {
    user:{
        name:"user",
        header:[
            "Pin",
            "Name",
            "CardNo",
            "Password",
            "Group",
            "StartTime",
            "EndTime",
        ]
    },
    userauthorize:{
        name:"userauthorize",
        header:[
            "Pin",
            "AuthorizeDoorId",
            "AuthorizeTimezoneId"
        ]
    },
    transaction:{
        name:"transaction",
        header:[
            "Pin",
            "Cardno",
            "Verified",
            "DoorID",
            "EventType",
            "InOutState",
            "Time_second",
        ]
    }
            //device2.GetDeviceData_Pull("user", "CardNo\tPin\tPassword\tGroup\tStartTime\tEndTime");
            //device2.GetDeviceData_Pull("userauthorize", "Pin\tAuthorizeDoorId\tAuthorizeTimezoneId");
            //device2.GetDeviceData_Pull("transaction", "Pin\tCardno\tVerified\tDoorID\tEventType\tInOutState\tTime_second");
}